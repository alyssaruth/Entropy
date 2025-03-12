package server;

import auth.UserConnection;
import logging.LoggerUncaughtExceptionHandler;
import object.ServerRunnable;
import object.ServerThread;
import room.RoomFactory;
import util.Debug;
import util.DebugOutputSystemOut;
import util.OnlineConstants;
import util.ServerGlobals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.CoreGlobals.logger;

public final class EntropyServer implements OnlineConstants {
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
        ServerGlobals.INSTANCE.getRoomStore().reset();

        logger.info("clearedMessages", "Cleared lobby messages");
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
