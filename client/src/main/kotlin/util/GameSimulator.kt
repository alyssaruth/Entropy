package util

import game.BidAction
import game.ChallengeAction
import `object`.Player
import screen.ScreenCache.get
import screen.SimulationDialog
import utils.CoreGlobals.logger

class GameSimulator(private val params: SimulationParams) {
    private var personToStart = 0

    private var lastBid: BidAction<*>? = null

    val opponentZero: Player = Player(0, "").also { it.name = "0" }
    val opponentOne: Player = Player(1, "").also { it.name = "1" }
    val opponentTwo: Player = Player(2, "").also { it.name = "2" }
    val opponentThree: Player = Player(3, "").also { it.name = "3" }

    var playOrder = listOf(0, 2, 3, 1) // standard play order

    /** Simulation methods */
    fun startNewGame(number: Int) {
        log("*** Game $number ***")

        initVariablesForSimulationNewGame()
        initVariablesForSimulationNewRound()
        personToStart = computeRandomPersonToStart()

        val player: Player = getPlayer(personToStart)
        processOpponentTurn(player)
    }

    private fun startNewRound() {
        log("*** New Round ***")
        log("opponentZeroNumberOfCards = " + opponentZero.numberOfCards)
        log("opponentOneNumberOfCards = " + opponentOne.numberOfCards)
        log("opponentTwoNumberOfCards = " + opponentTwo.numberOfCards)
        log("opponentThreeNumberOfCards = " + opponentThree.numberOfCards)

        knockOutPlayers()
        val gameOver = checkForResult()

        if (!gameOver) {
            initVariablesForSimulationNewRound()

            val playerToStart: Player = getPlayer(personToStart)
            if (playerToStart.isEnabled) {
                processOpponentTurn(playerToStart)
            } else {
                processOpponentTurn(nextPlayer(playerToStart))
            }
        }
    }

    private fun populateHands(deck: List<String>) {
        log("Dealing hands...")

        allPlayers().forEach { GameUtil.populateHand(it, deck, params.enableLogging) }
    }

    private fun computeRandomPersonToStart(): Int {
        if (params.forceStart) {
            return 0
        }

        return allPlayers().filter(Player::isEnabled).random().playerNumber
    }

    private fun knockOutPlayers() {
        log("Knocking out players...")

        allPlayers().forEach(::knockOut)
    }

    private fun knockOut(player: Player) {
        if (player.numberOfCards == 0 && player.isEnabled) {
            log("Opponent $player disabled")
            player.isEnabled = false
        }
    }

    private fun checkForResult(): Boolean {
        val stillAlive = allPlayers().filter { it.numberOfCards > 0 }
        if (stillAlive.size == 1) {
            val winner = stillAlive.first()
            val perfectGame = winner.numberOfCards == params.settings.startingCards
            get(SimulationDialog::class.java).recordWin(winner.playerNumber, perfectGame)
            return true
        }

        return false
    }

    private fun allPlayers() = playOrder.map(::getPlayer)

    private fun allCards() = allPlayers().flatMap { it.hand }

    private fun processOpponentTurn(opponent: Player) {
        if (!opponent.isEnabled) {
            throw Exception("Trying to take turn for $opponent, but they're disabled")
        }

        log("*** Opponent $opponent ***")
        if (lastBid != null) {
            get(SimulationDialog::class.java).recordOpportunityToChallenge(opponent.playerNumber)
        }

        val stratParms = getStrategyParms(opponent)
        val action =
            CpuStrategies.processOpponentTurn(stratParms, opponent)
                ?: // Abort the simulation, something's gone wrong
                throw SimulationException("Simulation error")

        if (action is ChallengeAction) {
            processChallenge(opponent)
        } else {
            lastBid = action as BidAction<*>
            processOpponentTurn(nextPlayer(opponent))
        }
    }

    fun nextPlayer(opponent: Player): Player {
        val currentPlayerIndex = playOrder.indexOf(opponent.playerNumber)
        val nextIndices = (1..3).map { (currentPlayerIndex + it) % playOrder.size }
        val nextPlayers = nextIndices.map { getPlayer(playOrder[it]) }.filter { it.isEnabled }

        if (nextPlayers.isEmpty()) {
            throw Exception(
                "No valid next player found. Play order is $playOrder, currentPlayer is ${opponent.playerNumber}, nextIndices: $nextIndices"
            )
        }

        return nextPlayers.first()
    }

    private fun processChallenge(challenger: Player) {
        log("Challenged")

        val lastBid = lastBid ?: throw Exception("Processing challenge with no lastBid")

        val dialog = get(SimulationDialog::class.java)
        if (!lastBid.isOverbid(allCards(), params.settings)) {
            log("not overbid")
            dialog.recordChallenge(challenger.playerNumber, false)

            challenger.cardsToSubtract = 1
            challenger.doSubtraction()
            personToStart = challenger.playerNumber
        } else {
            log("overbid")
            dialog.recordChallenge(challenger.playerNumber, true)

            val name = lastBid.playerName
            val bidder = getPlayer(name.toInt())
            bidder.cardsToSubtract = 1
            bidder.doSubtraction()
            personToStart = bidder.playerNumber
        }

        startNewRound()
    }

    private fun initVariablesForSimulationNewGame() {
        opponentZero.isEnabled = true
        opponentOne.isEnabled = true
        val opponentTwoEnabled = params.opponentTwoEnabled
        opponentTwo.isEnabled = opponentTwoEnabled
        val opponentThreeEnabled = params.opponentThreeEnabled
        opponentThree.isEnabled = opponentThreeEnabled
        val opponentTwoCoeff = if (opponentTwoEnabled) 1 else 0
        val opponentThreeCoeff = if (opponentThreeEnabled) 1 else 0

        val numberOfCards = params.settings.startingCards
        opponentZero.numberOfCards = numberOfCards
        opponentOne.numberOfCards = numberOfCards
        opponentTwo.numberOfCards = opponentTwoCoeff * numberOfCards
        opponentThree.numberOfCards = opponentThreeCoeff * numberOfCards

        opponentZero.strategy = params.opponentZeroStrategy
        opponentOne.strategy = params.opponentOneStrategy
        opponentTwo.strategy = params.opponentTwoStrategy
        opponentThree.strategy = params.opponentThreeStrategy

        setPlayOrder(params.randomiseOrder)
    }

    private fun setPlayOrder(randomiseOrder: Boolean) {
        playOrder =
            if (randomiseOrder) {
                playOrder.shuffled()
            } else {
                listOf(2, 3, 1, 0)
            }

        log("Player order: $playOrder")
    }

    private fun initVariablesForSimulationNewRound() {
        lastBid = null

        opponentZero.resetHand()
        opponentOne.resetHand()
        opponentTwo.resetHand()
        opponentThree.resetHand()

        val deck = CardsUtil.createAndShuffleDeck(params.settings)

        populateHands(deck)
    }

    private fun log(text: String) {
        if (params.enableLogging) {
            logger.info("simulation", text)
        }
    }

    private fun getStrategyParms(opponent: Player): StrategyParams {
        val totalCards = allPlayers().sumOf(Player::getNumberOfCards)
        val otherPlayers = allPlayers().filterNot { it == opponent }
        val cardsOnShow = otherPlayers.flatMap { it.revealedCards }

        return StrategyParams(
            params.settings,
            totalCards,
            cardsOnShow,
            lastBid,
            params.enableLogging,
        )
    }

    private fun getPlayer(playerNumber: Int) =
        when (playerNumber) {
            0 -> opponentZero
            1 -> opponentOne
            2 -> opponentTwo
            else -> opponentThree
        }
}
