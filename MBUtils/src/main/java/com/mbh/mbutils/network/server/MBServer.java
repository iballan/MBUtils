package com.mbh.mbutils.network.server;

import com.mbh.mbutils.logging.MBLogger;
import com.mbh.mbutils.thread.MBThreadUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created By MBH on 2016-06-11.
 */
public class MBServer {
    // region ---------->Example<----------
    /*
    mbServer = new MBServer.Builder()
            .serverPort(8080)
    .receiveOnBackgroundWorker(true)
    .showLogMessages(true)
    .onPacketReceivedHandler(new MBServer.OnPacketReceived() {
        @Override
        public void OnPacketReceived(String packet) {
        }
    })
            .onServerError(new MBServer.OnServerError() {
        @Override
        public void OnServerError(Throwable exception) {
        }
    })
            .build();
    mbServer.start();
    if(mbServer.isAlive())
            mbServer.stop();
     **/
    // endregion

    volatile boolean running = false;
    ServerSocket serverSocket;
    Thread mThread;
    // Private for singelton
    private MBLogger logger;
    private boolean stopOnError = false;
    private int serverPort = 8080;
    private boolean receiveOnDifferentThread = true;
    private boolean showLogMessages = true;
    //    private ServerRecievedMessageHandler recievedMessageHandler;
    private OnPacketReceived onPacketReceivedHandler;
    private OnServerError onServerError;
    //    private final String ReplyMessage = "Client#";
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private MBServer(Builder builder) {
        logger = new MBLogger.Builder()
                .setTag(MBServer.class)
                .createLogger();

        serverPort = builder.serverPort;
        receiveOnDifferentThread = builder.receiveOnDifferentThread;
        showLogMessages = builder.showLogMessages;
        onPacketReceivedHandler = builder.onPacketReceivedHandler;
        onServerError = builder.onServerError;
    }


    public boolean isAlive() {
        return isRunning != null && isRunning.get() && mThread != null && mThread.isAlive();
    }

    public void start() {
        if (isRunning.get()) return;
        if (mThread != null) {
            mThread = null;
        }

        isRunning.set(true);
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(serverPort);
                    serverSocket.setReuseAddress(true);
//                    logger.debug("ServerSocker initialized--InetAddress=" + serverSocket.getInetAddress());

                    while (isRunning.get()) {
                        try {
                            Socket clientSocket = serverSocket.accept();
                            handleClient(clientSocket);
                        } catch (Exception e) {
                            MBThreadUtils.TryToSleepFor(500);
                            errorReceived(e);
                        }
                    }

                    isRunning.set(false);
                } catch (Exception e) {
                    isRunning.set(false);
                    logger.error(e);
                }
            }
        });
        mThread.start();
    }

    private void errorReceived(Throwable error) {
        if (onServerError != null) {
            onServerError.OnServerError(error);
        }
    }

    public void stop() {
        isRunning.set(false);
        if (mThread != null) {
            mThread = null;
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger.debug("Server Has Been Stopped");
    }

    private void handleClient(Socket socket) {
//        if (receiveOnDifferentThread) {
//            MBThreadUtils.DoOnBackground(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        handleSocket(socket);
//                    } catch (Exception exception) {
//                        errorReceived(exception);
//                    }
//                }
//            });
//        } else {
            try {
                handleSocket(socket);
            } catch (Exception exception) {
                errorReceived(exception);
            }
//        }
    }

    private void handleSocket(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //final String incomingMsg = in.readLine() + System.getProperty("line.separator");
        final String rec_str = in.readLine() + System.getProperty("line.separator"); // incoming message
        final String paramString = rec_str.replace("\n", "").replace("\r", "");
        handleReceivedPacket(paramString);
        try {
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.close();
    }

    private void handleReceivedPacket(final String receivedPacket) {
        if (receiveOnDifferentThread) {
            MBThreadUtils.DoOnBackground(new Runnable() {
                @Override
                public void run() {
                if (onPacketReceivedHandler != null)
                    onPacketReceivedHandler.OnPacketReceived(receivedPacket);
                }
            });
        } else {
            if (onPacketReceivedHandler != null)
                onPacketReceivedHandler.OnPacketReceived(receivedPacket);
        }
    }

    public interface OnPacketReceived {
        void OnPacketReceived(String packet);
    }

    public interface OnServerError {
        void OnServerError(Throwable exception);
    }


    public static final class Builder {
        private int serverPort;
        private boolean receiveOnDifferentThread;
        private boolean showLogMessages;
        private OnPacketReceived onPacketReceivedHandler;
        private OnServerError onServerError;

        public Builder() {
        }

        public Builder serverPort(int val) {
            serverPort = val;
            return this;
        }

        public Builder receiveOnBackgroundWorker(boolean val) {
            receiveOnDifferentThread = val;
            return this;
        }

        public Builder showLogMessages(boolean val) {
            showLogMessages = val;
            return this;
        }

        public Builder onPacketReceivedHandler(OnPacketReceived val) {
            onPacketReceivedHandler = val;
            return this;
        }

        public Builder onServerError(OnServerError val) {
            onServerError = val;
            return this;
        }

        public MBServer build() {
            return new MBServer(this);
        }
    }
}
