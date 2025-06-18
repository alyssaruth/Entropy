package util

import java.util.Random
import `object`.Bid
import `object`.Player
import screen.ScreenCache.get
import screen.SimulationDialog
import utils.CoreGlobals.logger

class GameSimulator(private val params: SimulationParams) {
    private var personToStart = 0

    private var lastBid: Bid? = null

    private val opponentZero: Player = Player(0, "").also { it.name = "0" }
    private val opponentOne: Player = Player(1, "").also { it.name = "1" }
    private val opponentTwo: Player = Player(2, "").also { it.name = "2" }
    private val opponentThree: Player = Player(3, "").also { it.name = "3" }

    private var playOrder = listOf(0, 2, 3, 1) // standard play order

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
            processOpponentTurn(playerToStart)
        }
    }

    private fun populateHands(deck: List<String>) {
        log("Dealing hands...")

        GameUtil.populateHand(opponentZero, deck, params.enableLogging)
        GameUtil.populateHand(opponentOne, deck, params.enableLogging)
        GameUtil.populateHand(opponentTwo, deck, params.enableLogging)
        GameUtil.populateHand(opponentThree, deck, params.enableLogging)
    }

    private tailrec fun computeRandomPersonToStart(): Int {
        if (params.forceStart) {
            return 0
        }

        val result = Random().nextInt(4)
        return if (getPlayer(result).isEnabled) {
            result
        } else {
            computeRandomPersonToStart()
        }
    }

    private fun knockOutPlayers() {
        log("Knocking out players...")

        knockOut(opponentZero)
        knockOut(opponentOne)
        knockOut(opponentTwo)
        knockOut(opponentThree)
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

    private fun processOpponentTurn(opponent: Player) {
        if (!opponent.isEnabled) {
            val nextPlayerNumber = playOrder[opponent.playerNumber]
            processOpponentTurn(getPlayer(nextPlayerNumber))
            return
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

        if (action.isChallenge) {
            processChallenge(opponent)
        } else {
            lastBid = action
            processOpponentTurn(nextPlayer(opponent))
        }
    }

    private fun nextPlayer(opponent: Player): Player {
        val currentPlayerIndex = playOrder.indexOf(opponent.playerNumber)
        val nextPlayerIndex = (currentPlayerIndex + 1) % playOrder.size
        val nextPlayerNumber = playOrder[nextPlayerIndex]
        val nextPlayer = getPlayer(nextPlayerNumber)

        log(
            "$playOrder: ${opponent.playerNumber} -> ${nextPlayer.playerNumber}, enabled = ${nextPlayer.isEnabled}"
        )
        return if (nextPlayer.isEnabled) nextPlayer else nextPlayer(nextPlayer)
    }

    private fun processChallenge(challenger: Player) {
        log("Challenged")

        val dialog = get(SimulationDialog::class.java)
        if (
            !lastBid!!.isOverbid(
                opponentZero.hand,
                opponentOne.hand,
                opponentTwo.hand,
                opponentThree.hand,
                params.settings.jokerValue,
            )
        ) {
            log("not overbid")
            dialog.recordChallenge(challenger.playerNumber, false)

            challenger.cardsToSubtract = 1
            challenger.doSubtraction()
            personToStart = challenger.playerNumber
        } else {
            log("overbid")
            dialog.recordChallenge(challenger.playerNumber, true)

            val bidder = lastBid!!.player
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
