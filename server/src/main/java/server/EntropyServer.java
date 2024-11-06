package server;

import auth.UserConnection;
import game.GameMode;
import game.GameSettings;
import logging.LoggerUncaughtExceptionHandler;
import object.OnlineMessage;
import object.Room;
import object.ServerRunnable;
import object.ServerThread;
import util.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.CoreGlobals.logger;

public final class EntropyServer implements OnlineConstants {
    //Caches
    private ConcurrentHashMap<String, Room> hmRoomByName = new ConcurrentHashMap<>();
    private ArrayList<OnlineMessage> lobbyMessages = new ArrayList<>();

    //Seed
    private static long currentSeedLong = 4613352884640512L;

    public static void main() {
        Thread.setDefaultUncaughtExceptionHandler(new LoggerUncaughtExceptionHandler());

        //Initialise interfaces etc
        Debug.initialise(new DebugOutputSystemOut());

        ServerGlobals.INSTANCE.getServer().onStart();
    }

    private void onStart() {
        Debug.appendBanner("Start-Up");

        registerDefaultRooms();

        Debug.append("Starting permanent threads");

        startListenerThreads();

        Debug.appendBanner("Server is ready - accepting connections");
    }

    private void registerDefaultRooms() {
        Debug.append("Creating rooms...");

        //Entropy
        //2 player
        registerNewRoom("Potassium 1", GameMode.Entropy, 2, 0, 2);
        registerNewRoom("Zinc 1", GameMode.Entropy, 2, 2, 2);
        registerNewRoom("Helium 1", GameMode.Entropy, 2, 4, 3);
        registerNewRoom("Magnesium 1", GameMode.Entropy, 2, 2, 2, true, true, true, false, false);
        registerNewRoom("Cobalt 1", GameMode.Entropy, 2, 0, 2, true, true, false, false, false);
        registerNewRoom("Chlorine 1", GameMode.Entropy, 2, 0, 2, true, true, false, true, false);
        registerNewRoom("Gold 1", GameMode.Entropy, 2, 0, 2, false, false, true, false, false);
        registerNewRoom("Lithium 1", GameMode.Entropy, 2, 2, 2, false, false, false, true, true);
        registerNewRoom("Beryllium 1", GameMode.Entropy, 2, 0, 2, true, true, false, false, true);

        //3 player
        registerNewRoom("Bromine 1", GameMode.Entropy, 3, 0, 2);
        registerNewRoom("Argon 1", GameMode.Entropy, 3, 2, 2);
        registerNewRoom("Hydrogen 1", GameMode.Entropy, 3, 4, 3);
        registerNewRoom("Zirconium 1", GameMode.Entropy, 3, 2, 2, true, true, true, false, false);
        registerNewRoom("Calcium 1", GameMode.Entropy, 3, 0, 2, true, true, false, false, false);
        registerNewRoom("Iron 1", GameMode.Entropy, 3, 2, 2, true, false, false, true, false);
        registerNewRoom("Palladium 1", GameMode.Entropy, 3, 3, 3, true, true, true, false, true);

        //4 player
        registerNewRoom("Nickel 1", GameMode.Entropy, 4, 0, 2);
        registerNewRoom("Sodium 1", GameMode.Entropy, 4, 2, 2);
        registerNewRoom("Phosphorus 1", GameMode.Entropy, 4, 4, 3);
        registerNewRoom("Titanium 1", GameMode.Entropy, 4, 0, 2, true, true, false, true, false);
        registerNewRoom("Gallium 1", GameMode.Entropy, 4, 2, 2, true, true, false, false, true);

        //Vectropy
        //2 player
        registerNewRoom("Oxygen 1", GameMode.Vectropy, 2, 0, 2);
        registerNewRoom("Neon 1", GameMode.Vectropy, 2, 2, 2);
        registerNewRoom("Copper 1", GameMode.Vectropy, 2, 4, 3);
        registerNewRoom("Manganese 1", GameMode.Vectropy, 2, 2, 2, true, true, true, false, false);
        registerNewRoom("Selenium 1", GameMode.Vectropy, 2, 0, 2, true, true, false, false, false);
        registerNewRoom("Chromium 1", GameMode.Vectropy, 2, 0, 2, true, true, false, true, false);
        registerNewRoom("Silver 1", GameMode.Vectropy, 2, 0, 2, false, false, true, false, false);
        registerNewRoom("Antimony 1", GameMode.Vectropy, 2, 2, 2, false, false, false, true, true);
        registerNewRoom("Tungsten 1", GameMode.Vectropy, 2, 0, 2, true, true, false, false, true);

        //3 player
        registerNewRoom("Carbon 1", GameMode.Vectropy, 3, 0, 2);
        registerNewRoom("Silicon 1", GameMode.Vectropy, 3, 2, 2);
        registerNewRoom("Nitrogen 1", GameMode.Vectropy, 3, 4, 3);
        registerNewRoom("Sulphur 1", GameMode.Vectropy, 3, 2, 2, true, true, true, false, false);
        registerNewRoom("Fluorine 1", GameMode.Vectropy, 3, 0, 2, true, true, false, false, false);
        registerNewRoom("Tin 1", GameMode.Vectropy, 3, 2, 2, true, false, false, true, false);
        registerNewRoom("Indium 1", GameMode.Vectropy, 3, 3, 3, true, true, true, false, true);

        //4 player
        registerNewRoom("Iodine 1", GameMode.Vectropy, 4, 0, 2);
        registerNewRoom("Lead 1", GameMode.Vectropy, 4, 2, 2);
        registerNewRoom("Uranium 1", GameMode.Vectropy, 4, 4, 3);
        registerNewRoom("Vanadium 1", GameMode.Vectropy, 4, 0, 2, true, true, false, true, false);
        registerNewRoom("Xenon 1", GameMode.Vectropy, 4, 2, 2, true, true, false, false, true);

        Debug.append("Finished creating " + hmRoomByName.size() + " rooms");
    }

    private void startListenerThreads() {
        try {
            ServerThread listenerThread = new ServerThread(new MessageListener(this, SERVER_PORT_NUMBER));
            listenerThread.setName("Listener-" + SERVER_PORT_NUMBER);
            listenerThread.start();
        } catch (Throwable t) {
            logger.error("listenerError", "Unable to start listener thread on port " + SERVER_PORT_NUMBER, t);
        }
    }

    public void executeInWorkerPool(ServerRunnable runnable) {
        ServerGlobals.workerPool.executeServerRunnable(runnable);
    }

    public void resetLobby() {
        int countRemoved = 0;

        List<Room> rooms = getRooms();
        int size = rooms.size();
        for (int i = 0; i < size; i++) {
            Room room = rooms.get(i);
            if (room.getIsCopy()) {
                String roomName = room.getName();
                hmRoomByName.remove(roomName);
                countRemoved++;
            }
        }

        //Log out if we've actually removed some rooms
        if (countRemoved > 0) {
            logger.info("roomsRemoved", "Removed " + countRemoved + " excess rooms");
        }

        logger.info("clearedMessages", "Cleared lobby messages");
        lobbyMessages.clear();
    }

    public void addToChatHistory(String id, String message, String colour, String username) {
        OnlineMessage messageObj = new OnlineMessage(colour, message, username);
        addToChatHistory(id, messageObj);
    }

    public void addAdminMessage(String message) {
        OnlineMessage messageObj = new OnlineMessage("black", message, "Admin");
        Iterator<String> it = hmRoomByName.keySet().iterator();
        for (; it.hasNext(); ) {
            String name = it.next();
            addToChatHistory(name, messageObj);
        }

        addToChatHistory(LOBBY_ID, messageObj);
    }

    private void addToChatHistory(String name, OnlineMessage message) {
        if (name.equals(LOBBY_ID)) {
            lobbyMessages.add(message);
            List<UserConnection> usersToNotify = ServerGlobals.INSTANCE.getUscStore().getAll();

            String chatMessage = XmlBuilderServer.getChatNotification(name, message);
            sendViaNotificationSocket(usersToNotify, chatMessage, XmlConstants.SOCKET_NAME_CHAT);
        } else {
            Room room = hmRoomByName.get(name);
            room.addToChatHistoryAndNotifyUsers(message);
        }
    }

    public void sendViaNotificationSocket(List<UserConnection> uscs, String message, String socketName) {
        sendViaNotificationSocket(uscs, message, socketName, false);
    }

    public void sendViaNotificationSocket(List<UserConnection> uscs, String message, String socketName, boolean blocking) {
        AtomicInteger counter = null;
        if (blocking) {
            int size = uscs.size();
            counter = new AtomicInteger(size);
        }

        sendViaNotificationSocket(uscs, message, socketName, counter);
    }

    private void sendViaNotificationSocket(List<UserConnection> uscs, String message, String socketName, AtomicInteger counter) {
        int size = uscs.size();
        for (int i = 0; i < size; i++) {
            UserConnection usc = uscs.get(i);
            usc.sendNotificationInWorkerPool(message, this, socketName, counter);
        }

        if (counter != null) {
            while (counter.get() > 0) {
                //So you wait. You waiiiit. Yoouuu waiiiiiiit.
                try {
                    Thread.sleep(500);
                } catch (Throwable t) {
                }
            }
        }
    }

    public List<OnlineMessage> getChatHistory(String id) {
        if (id.equals(LOBBY_ID)) {
            return lobbyMessages;
        }

        Room room = hmRoomByName.get(id);
        return room.getChatHistory();
    }

    public Room registerNewRoom(String roomName, int capacity, GameSettings settings) {
        return registerNewRoom(roomName, settings.getMode(), capacity, settings.getJokerQuantity(), settings.getJokerValue(),
                settings.getIncludeMoons(), settings.getIncludeStars(), settings.getIllegalAllowed(), settings.getNegativeJacks(),
                settings.getCardReveal());
    }

    private void registerNewRoom(String roomName, GameMode mode, int players, int jokerQuantity, int jokerValue) {
        registerNewRoom(roomName, mode, players, jokerQuantity, jokerValue, false, false, false, false, false);
    }

    private Room registerNewRoom(String roomName, GameMode mode, int players, int jokerQuantity, int jokerValue,
                                 boolean includeMoons, boolean includeStars, boolean illegalAllowed, boolean negativeJacks,
                                 boolean cardReveal) {
        Iterator<String> it = hmRoomByName.keySet().iterator();
        for (; it.hasNext(); ) {
            String id = it.next();
            Room room = hmRoomByName.get(id);

            String nameToCheck = room.getName();
            if (nameToCheck.equals(roomName)) {
                Debug.append("Not creating room " + nameToCheck + " as a room with that name already exists.");
                return null;
            }
        }

        GameSettings settings = new GameSettings(mode, jokerQuantity, jokerValue, includeMoons, includeStars,
                negativeJacks, cardReveal, illegalAllowed);

        Room room = new Room(roomName, settings, players, this);
        room.initialiseGame();
        hmRoomByName.put(roomName, room);

        Debug.append("Room created: " + roomName);
        return room;
    }

    public Room registerCopy(Room room) {
        String roomName = room.getName();
        int capacity = room.getCapacity();
        GameSettings settings = room.getSettings();

        int index = roomName.indexOf(' ') + 1;
        String roomNumberStr = roomName.substring(index);
        int roomNumber = Integer.parseInt(roomNumberStr);

        String newRoomName = roomName.substring(0, index) + (roomNumber + 1);

        Room newRoom = registerNewRoom(newRoomName, capacity, settings);

        if (newRoom != null) {
            newRoom.setIsCopy(true);
            ServerGlobals.lobbyService.lobbyChanged();
        }

        return newRoom;
    }

    public ArrayList<Room> getRooms() {
        ArrayList<Room> list = new ArrayList<>();
        Iterator<String> it = hmRoomByName.keySet().iterator();

        for (; it.hasNext(); ) {
            String id = it.next();
            Room room = hmRoomByName.get(id);
            list.add(room);
        }

        return list;
    }

    public Room getRoomForName(String name) {
        return hmRoomByName.get(name);
    }

    /**
     * Generate a secure seed based on:
     * - Current time in nanos
     * - A private seed known to the Server which is incremented on each hit
     */
    public long generateSeed() {
        currentSeedLong++;

        return System.nanoTime() + currentSeedLong;
    }
}
