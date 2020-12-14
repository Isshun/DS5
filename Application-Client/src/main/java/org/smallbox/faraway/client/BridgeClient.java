package org.smallbox.faraway.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BridgeClient {

//    public static interface ServerManagerListener {
//        void onStart();
//        void onUpdate();
//    }
//
//    public void register(ServerManagerListener listener) {
//
//        try {
//            Socket socket = new Socket("localhost", 4242);
//            OutputStream os = socket.getOutputStream();
//            InputStream is = socket.getInputStream();
////            DataInputStream is = new DataInputStream(socket.getInputStream());
//
//            IOUtils.write("hello server", os);
//
//            System.out.println("[BRIDGE] FROM SERVER: " + IOUtils.toString(is));
//
//            os.close();
//            is.close();
//            socket.close();
//        } catch (Exception e) {
//            System.err.println("[BRIDGE] Exception:  " + e);
//        }
//
//    }


    //    private static final String  SERVER_ADDRESS = "212.71.252.180";
    public static final String  SERVER_ADDRESS = "localhost";
    private static final int    SERVER_PORT = 4242;
    private static final long   TIMEOUT_DELAY = 15000;

    private long                mLastReceive;

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final int maxWriteSize = 65536;

    private List<String>                mMessages;
    private boolean                     mRunning = true;
    private final StringBuffer          sb = new StringBuffer();
    public boolean isActive = false;

    public void register() {
        mMessages = new ArrayList<>();
        mLastReceive = System.currentTimeMillis();

        new Thread(new Runnable() {

            @Override
            public void run() {
                SocketChannel socketChannel = null;
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));
                    socketChannel.configureBlocking(false);

                    ByteBuffer buffer = ByteBuffer.allocate(maxWriteSize);
                    byte[] array = new byte[maxWriteSize];

                    System.out.println("[BRIDGE CLIENT] ON CONNECT");

                    while (mRunning) {
                        if (System.currentTimeMillis() > mLastReceive + TIMEOUT_DELAY) {
                            System.out.println("[BRIDGE CLIENT] timeout");
                            break;
                        }

                        if (isActive) {
                        synchronized (this) {
                            for (String message : mMessages) {
                                System.out.println("[BRIDGE CLIENT] send: " + message);

                                buffer.clear();
                                buffer.put(message.getBytes(CHARSET));
                                buffer.flip();
                                while (buffer.hasRemaining()) {
                                    socketChannel.write(buffer);
                                }
                            }
                            mMessages.clear();
                        }
                        }

                        buffer.clear();
                        int bytesRead;
                        while ((bytesRead = socketChannel.read(buffer)) > 0) {
                            buffer.flip();
                            buffer.get(array, 0, bytesRead);
                            sb.append(new String(array, 0, bytesRead, "UTF-8"));
                        }

                        if (sb.length() > 0) {
                            while (sb.indexOf("\n") != -1) {
                                int index = sb.indexOf("\n");
                                final String string = sb.substring(0, index);
                                System.out.println("[BRIDGE CLIENT] receive: " + string);
                                onReceive(string);
                                sb.replace(0, index + 1, "");
                            }
                        }

                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    System.err.println("unknown_host");
                } catch (ConnectException e) {
                    e.printStackTrace();
                    System.err.println("connect_error");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("timeout");
                } finally {
                    if (socketChannel != null && socketChannel.isOpen()) {
                        try { socketChannel.close(); } catch (IOException e) { e.printStackTrace(); }
                    }
                }
            }
        }).start();
    }

    private void onReceive(String message) {
        System.out.println("[BRIDGE CLIENT] RECEIVE: " + message);

        isActive = true;
    }

    public void disconnect() {
        mRunning = false;
    }

    public boolean isConnected() {
        return mRunning;
    }

    public void sendMessage(String message) {
        synchronized(this) {
            mMessages.add(message);
        }
    }

}
