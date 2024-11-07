package room

import auth.UserConnection
import game.GameSettings
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import `object`.Bid
import `object`.BidHistory
import `object`.ExtendedConcurrentHashMap
import `object`.GameWrapper
import `object`.HandDetails
import `object`.LeftBid
import `object`.OnlineMessage
import `object`.Player
import server.EntropyServer
import util.CardsUtil
import util.Debug
import util.EntropyUtil
import util.ServerGlobals
import util.ServerGlobals.uscStore
import util.XmlBuilderServer
import util.XmlConstants

class Room(
    val name: String,
    val settings: GameSettings,
    val capacity: Int,
    private val server: EntropyServer
) {
    var isCopy: Boolean = false

    private val hmPlayerByPlayerNumber = ExtendedConcurrentHashMap<Int, String>()
    private val hmFormerPlayerByPlayerNumber: ConcurrentHashMap<Int, String> = ConcurrentHashMap()
    val chatHistory: ArrayList<OnlineMessage> = ArrayList()
    private val currentPlayers: MutableList<String> = ArrayList()
    private val observers: MutableList<String> = ArrayList()
    private var previousGame: GameWrapper? = null
    private var currentGame: GameWrapper? = null

    val isFull: Boolean
        get() = currentPlayers.size == capacity

    val isEmpty: Boolean
        get() = (currentPlayers.isEmpty() && observers.isEmpty())

    fun clearChatIfEmpty() {
        synchronized(this) {
            if (!isEmpty) {
                return
            }
            chatHistory.clear()
        }
    }

    fun addToCurrentPlayers(username: String, playerNumber: Int): Int {
        synchronized(this) {
            val existingUsername: String? = hmPlayerByPlayerNumber.get(playerNumber)
            if (existingUsername != null) {
                Debug.append(
                    (username +
                        " tried to join room " +
                        name +
                        " as player " +
                        playerNumber +
                        " but the space was taken by " +
                        existingUsername)
                )
                return -1
            }

            if (hmPlayerByPlayerNumber.containsValue(username)) {
                Debug.append(username + " tried to join room " + name + " twice!")
                return -1
            }

            currentPlayers.add(username)
            hmPlayerByPlayerNumber.put(playerNumber, username)
            observers.remove(username)

            notifyAllPlayersOfPlayerChange(username, false)
            ServerGlobals.lobbyService.lobbyChanged()
            return playerNumber
        }
    }

    fun removePlayer(username: String, fireLobbyChanged: Boolean) {
        for (playerNumber in 0 until capacity) {
            val user: String? = hmPlayerByPlayerNumber.get(playerNumber)
            if ((user != null && (username == user))) {
                hmPlayerByPlayerNumber.remove(playerNumber)
                hmFormerPlayerByPlayerNumber[playerNumber] = user

                // Notify everyone in the room that this player has left. Block on this.
                notifyAllPlayersOfPlayerChange(username, true)

                // The game has not started
                if (currentGame!!.gameStartMillis == -1L) {
                    // Unset the countdown if it's going, reset current capacity and get out of this
                    // madness
                    currentGame!!.countdownStartMillis = -1
                    resetCurrentPlayers(fireLobbyChanged)
                    return
                }

                // There is a game in progress
                if (currentGame!!.gameEndMillis == -1L) {
                    val bid: LeftBid = LeftBid()
                    val player: Player =
                        Player(playerNumber, EntropyUtil.getColourForPlayerNumber(playerNumber))
                    player.name = username
                    bid.player = player

                    val history: BidHistory = currentGame!!.currentBidHistory
                    history.addBidForPlayer(playerNumber, bid)

                    // Moved this into here as otherwise we set it to 0 incorrectly and a person
                    // ends up with no cards!
                    val details: HandDetails = currentGame!!.currentRoundDetails
                    val hmHandSizeByPlayerNumber: ConcurrentHashMap<Int, Int> = details.handSizes
                    hmHandSizeByPlayerNumber[playerNumber] = 0
                }

                val playerSize: Int = hmPlayerByPlayerNumber.size
                if (playerSize == 1) {
                    val remainingPlayerNumber: Int = hmPlayerByPlayerNumber.onlyKey
                    finishCurrentGame(remainingPlayerNumber)
                } else if (playerSize == 0) {
                    resetCurrentPlayers(fireLobbyChanged)
                    clearChatIfEmpty()
                }
            }
        }
    }

    private fun notifyAllPlayersOfPlayerChange(userToExclude: String, blocking: Boolean) {
        val notification: String = XmlBuilderServer.getPlayerNotification(this)
        notifyAllUsersViaGameSocket(notification, userToExclude, blocking)
    }

    private fun notifyAllUsersViaGameSocket(
        notification: String,
        userToExclude: String?,
        blocking: Boolean
    ) {
        val usersToNotify: HashSet<String> = allUsersInRoom
        if (userToExclude != null) {
            usersToNotify.remove(userToExclude)
        }

        val uscs: List<UserConnection> = uscStore.getAllForNames(usersToNotify)
        server.sendViaNotificationSocket(
            uscs,
            notification,
            XmlConstants.SOCKET_NAME_GAME,
            blocking
        )
    }

    private fun finishCurrentGame(winningPlayer: Int) {
        val roundNumber: Int = currentGame!!.roundNumber
        val winningUsername: String? = hmPlayerByPlayerNumber.get(winningPlayer)
        resetCurrentPlayers()
        currentGame!!.winningPlayer = winningPlayer

        if (roundNumber > 1) {
            val om: OnlineMessage = OnlineMessage("black", "$winningUsername won!", "Game")
            addToChatHistoryAndNotifyUsers(om)
        }

        initialiseGame()

        // Notify everyone that the game is over now we've finished setting up
        val notification: String = XmlBuilderServer.factoryGameOverNotification(this, winningPlayer)
        notifyAllUsersViaGameSocket(notification, null, false)
    }

    @JvmOverloads
    fun resetCurrentPlayers(fireLobbyChanged: Boolean = true) {
        currentPlayers.clear()
        for (i in 0 until capacity) {
            val username: String? = hmPlayerByPlayerNumber.get(i)
            if (username != null) {
                currentPlayers.add(username)
            }
        }

        hmFormerPlayerByPlayerNumber.clear()
        if (fireLobbyChanged) {
            ServerGlobals.lobbyService.lobbyChanged()
        }
    }

    fun addToObservers(username: String) {
        synchronized(this) {
            if (!observers.contains(username)) {
                observers.add(username)
            }
            removePlayer(username, false)
            ServerGlobals.lobbyService.lobbyChanged()
        }
    }

    fun removeFromObservers(username: String) {
        val removed: Boolean = observers.remove(username)
        if (removed) {
            ServerGlobals.lobbyService.lobbyChanged()
        }

        clearChatIfEmpty()
    }

    fun initialiseGame() {
        try {
            val gameId: String = "G" + System.currentTimeMillis()
            if (currentGame != null) {
                previousGame = currentGame!!.factoryCopy()
            }

            currentGame = GameWrapper(gameId)

            val details = HandDetails()
            val hmHandSizeByPlayerNumber: ExtendedConcurrentHashMap<Int, Int> =
                ExtendedConcurrentHashMap()
            for (i in 0 until capacity) {
                hmHandSizeByPlayerNumber[i] = 5
            }

            val hmHandByPlayerNumber: ConcurrentHashMap<Int, List<String>> =
                dealHandsHashMap(hmHandSizeByPlayerNumber)

            details.hands = hmHandByPlayerNumber
            details.handSizes = hmHandSizeByPlayerNumber
            currentGame!!.setDetailsForRound(1, details)

            val personToStart: Int = Random().nextInt(capacity)

            val history = BidHistory()
            history.personToStart = personToStart
            currentGame!!.setBidHistoryForRound(1, history)
        } catch (t: Throwable) {
            Debug.stackTrace(t)
        }
    }

    fun handleChallenge(
        gameId: String,
        roundNumber: Int,
        playerNumber: Int,
        challengedNumber: Int,
        bid: Bid
    ) {
        val game: GameWrapper? = getGameForId(gameId)
        val details: HandDetails = game!!.getDetailsForRound(roundNumber)
        val hmHandByPlayerNumber: ConcurrentHashMap<Int, List<String>> = details.hands
        if (bid.isOverbid(hmHandByPlayerNumber, settings.jokerValue)) {
            // bidder loses
            setUpNextRound(challengedNumber)
        } else {
            // challenger loses
            setUpNextRound(playerNumber)
        }
    }

    fun handleIllegal(
        gameId: String,
        roundNumber: Int,
        playerNumber: Int,
        bidderNumber: Int,
        bid: Bid
    ) {
        val game: GameWrapper? = getGameForId(gameId)
        val details: HandDetails = game!!.getDetailsForRound(roundNumber)
        val hmHandByPlayerNumber: ConcurrentHashMap<Int, List<String>> = details.hands
        if (
            bid.isPerfect(
                hmHandByPlayerNumber,
                settings.jokerValue,
                settings.includeMoons,
                settings.includeStars
            )
        ) {
            setUpNextRound(bidderNumber)
        } else {
            setUpNextRound(playerNumber)
        }
    }

    fun setUpNextRound(losingPlayerNumber: Int) {
        val currentRoundNumber: Int = currentGame!!.roundNumber

        var nextRoundDetails: HandDetails? =
            currentGame!!.getDetailsForRound(currentRoundNumber + 1)
        if (nextRoundDetails != null) {
            Debug.stackTrace("Trying to set up next round but it's not null. Room $name")
            return
        }

        val currentRoundDetails: HandDetails = currentGame!!.getDetailsForRound(currentRoundNumber)
        val hmHandSizeByPlayerNumber: ExtendedConcurrentHashMap<Int, Int> =
            currentRoundDetails.handSizes
        val handSize: Int? = hmHandSizeByPlayerNumber.get(losingPlayerNumber)
        val newHandSize: Int = max(0.0, (handSize!! - 1).toDouble()).toInt()

        val hmHandSizeByPlayerNumberForNextRound: ExtendedConcurrentHashMap<Int, Int> =
            hmHandSizeByPlayerNumber.factoryCopy()
        hmHandSizeByPlayerNumberForNextRound[losingPlayerNumber] = newHandSize

        val potentialWinner: Int = getWinningPlayer(hmHandSizeByPlayerNumberForNextRound)
        if (potentialWinner > -1) {
            finishCurrentGame(potentialWinner)
        } else {
            nextRoundDetails = HandDetails()
            nextRoundDetails.setHandSizes(hmHandSizeByPlayerNumberForNextRound)

            val hmHandByPlayerNumber: ConcurrentHashMap<Int, List<String>> =
                dealHandsHashMap(hmHandSizeByPlayerNumberForNextRound)

            nextRoundDetails.setHands(hmHandByPlayerNumber)
            currentGame!!.setDetailsForRound(currentRoundNumber + 1, nextRoundDetails)

            val history: BidHistory = BidHistory()
            history.personToStart = losingPlayerNumber
            currentGame!!.setBidHistoryForRound(currentRoundNumber + 1, history)

            currentGame!!.roundNumber = currentRoundNumber + 1

            val newRoundNotification: String =
                XmlBuilderServer.factoryNewRoundNotification(
                    this,
                    nextRoundDetails,
                    losingPlayerNumber
                )
            notifyAllUsersViaGameSocket(newRoundNotification, null, false)
        }
    }

    private fun dealHandsHashMap(
        hmHandSizeByPlayerNumber: ExtendedConcurrentHashMap<Int, Int>
    ): ConcurrentHashMap<Int, List<String>> {
        val hmHandByPlayerNumber: ConcurrentHashMap<Int, List<String>> = ConcurrentHashMap()

        val seed: Long = server.generateSeed()
        val deck =
            CardsUtil.createAndShuffleDeck(
                true,
                settings.jokerQuantity,
                settings.includeMoons,
                settings.includeStars,
                settings.negativeJacks,
                seed
            )

        for (i in 0 ..< capacity) {
            val size: Int = hmHandSizeByPlayerNumber.getValue(i)
            val hand = (0 ..< size).map { deck.removeAt(0) }
            hmHandByPlayerNumber[i] = hand
        }

        return hmHandByPlayerNumber
    }

    private fun getWinningPlayer(hmHandSizeByPlayerNumber: ConcurrentHashMap<Int, Int>): Int {
        var activePlayers: Int = 0
        var potentialWinner: Int = 0

        for (i in 0 until capacity) {
            val handSize: Int = (hmHandSizeByPlayerNumber.get(i))!!
            if (handSize > 0) {
                activePlayers++
                potentialWinner = i
            }
        }

        if (activePlayers > 1) {
            return -1
        } else {
            return potentialWinner
        }
    }

    val allUsersInRoom: HashSet<String>
        /**
         * Returns a HashSet since it's possible for a player to be present as a player AND an
         * observer. This occurs if they've left but the game is still going - we keep the reference
         * as a player so others can't take the seat. They obviously then have the option to join as
         * an observer.
         */
        get() {
            val ret: ArrayList<String> = getCurrentPlayers()
            ret.addAll(getObservers())

            val hs: HashSet<String> = HashSet(ret)
            return hs
        }

    val isGameInProgress: Boolean
        get() {
            // This no longer works
            // return currentGame != null;
            // return currentPlayers.size() == capacity && (waitingForPlayerToSeeResult == null);
            return currentPlayers.size == capacity
        }

    fun getCurrentPlayers(): ArrayList<String> {
        return ArrayList(currentPlayers)
    }

    val currentPlayerCount: Int
        get() {
            return currentPlayers.size
        }

    fun getObservers(): ArrayList<String> {
        return ArrayList(observers)
    }

    val observerCount: Int
        get() {
            return observers.size
        }

    /** HashMap gets/sets */
    fun getPlayer(playerNumber: Int): String? {
        return hmPlayerByPlayerNumber.get(playerNumber)
    }

    fun getFormerPlayer(playerNumber: Int): String? {
        return hmFormerPlayerByPlayerNumber.get(playerNumber)
    }

    fun getGameForId(gameId: String): GameWrapper? {
        val currentId: String = currentGame!!.gameId
        if ((gameId == currentId)) {
            return currentGame
        }

        val previousId: String = previousGame!!.gameId
        if ((gameId == previousId)) {
            return previousGame
        }

        throw RuntimeException("Got a null game for room $name and gameId $gameId")
    }

    fun getNextGameForId(previousGameIdFromClient: String): GameWrapper? {
        if (previousGameIdFromClient.isEmpty()) {
            return currentGame
        }

        if (previousGame == null) {
            Debug.append(
                "Tried to get next game for gameId $previousGameIdFromClient but previous game was null."
            )
            Debug.appendWithoutDate("Current: " + currentGame!!.gameId)
            return currentGame
        }

        val previousGameId: String = previousGame!!.gameId
        if ((previousGameId == previousGameIdFromClient)) {
            return currentGame
        }

        Debug.append(
            "Tried to get next game for gameId $previousGameIdFromClient but this didn't match my previous game."
        )
        Debug.appendWithoutDate("Previous: " + previousGame!!.gameId)
        Debug.appendWithoutDate("Current: " + currentGame!!.gameId)
        return currentGame
    }

    fun getLastBidForPlayer(playerNumber: Int, roundNumber: Int): Bid? {
        if ((currentGame == null || playerNumber == -1)) {
            return null
        }

        val history: BidHistory = currentGame!!.getBidHistoryForRound(roundNumber)
        return history.getLastBidForPlayer(playerNumber)
    }

    fun addBidForPlayer(
        gameId: String,
        playerNumber: Int,
        roundNumber: Int,
        newBid: Bid?
    ): Boolean {
        val game: GameWrapper? = getGameForId(gameId)

        val history: BidHistory = game!!.getBidHistoryForRound(roundNumber)
        val added: Boolean = history.addBidForPlayer(playerNumber, newBid)

        if (added) {
            // Notify all other capacity
            val bidNotification: String =
                XmlBuilderServer.getBidNotification(name, playerNumber, newBid)
            notifyAllUsersViaGameSocket(bidNotification, null, false)
        }

        return added
    }

    fun addToChatHistoryAndNotifyUsers(message: OnlineMessage) {
        if (isEmpty) {
            return
        }

        chatHistory.add(message)

        val chatMessage: String = XmlBuilderServer.getChatNotification(name, message)
        val users: HashSet<String> = allUsersInRoom
        val uscs: List<UserConnection> = uscStore.getAllForNames(users)
        server.sendViaNotificationSocket(uscs, chatMessage, XmlConstants.SOCKET_NAME_CHAT, false)
    }

    override fun toString(): String {
        return name
    }
}
