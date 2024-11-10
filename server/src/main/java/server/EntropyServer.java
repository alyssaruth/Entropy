package server;

import auth.UserConnection;
import logging.LoggerUncaughtExceptionHandler;
import object.OnlineMessage;
import object.ServerRunnable;
import object.ServerThread;
import room.Room;
import room.RoomFactory;
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

        RoomFactory.INSTANCE.registerStarterRooms();

        Debug.append("Starting permanent threads");

        startListenerThreads();

        Debug.appendBanner("Server is ready - accepting connections");
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
            if (room.isCopy()) {
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

    public Room registerNewRoom(Room room) {
        Iterator<String> it = hmRoomByName.keySet().iterator();
        for (; it.hasNext(); ) {
            String id = it.next();
            Room existingRoom = hmRoomByName.get(id);
            String nameToCheck = existingRoom.getName();
            if (nameToCheck.equals(room.getName())) {
                logger.warn("duplicateRoom", "Not creating room " + nameToCheck + " as a room with that name already exists.");
                return null;
            }
        }

        hmRoomByName.put(room.getName(), room);

        if (room.isCopy()) {
            ServerGlobals.lobbyService.lobbyChanged();
        }

        return room;
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
