package org.smallbox.faraway.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServer {

    private final static int        BUF_SIZE = 16384;
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final int               mPort;
    private final ByteBuffer mReadBuffer;
    private final ByteBuffer        mWriteBuffer;
    protected LinkedBlockingQueue<QueueElement>   mQueue;
    private List<SocketChannel> _clients = new ArrayList<>();

    public GameServer() throws IOException {

        mQueue = new LinkedBlockingQueue<>();
        mPort = 4242;
        mReadBuffer = ByteBuffer.allocate(BUF_SIZE);
        mWriteBuffer = ByteBuffer.allocate(BUF_SIZE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Instead of creating a ServerSocket,
                    // create a ServerSocketChannel
                    ServerSocketChannel ssc = ServerSocketChannel.open();

                    // Set it to non-blocking, so we can use select
                    ssc.configureBlocking( false );

                    // Get the Socket connected to this channel, and bind it
                    // to the listening mPort
                    ServerSocket ss = ssc.socket();
                    InetSocketAddress isa = new InetSocketAddress(mPort);
                    ss.bind( isa );

                    // Create a engine Selector for selecting
                    Selector selector = Selector.open();

                    // Register the ServerSocketChannel, so we can
                    // listen for incoming connections
                    ssc.register( selector, SelectionKey.OP_ACCEPT );
//                    System.out.println( "Listening on mPort "+ mPort);

                    while (true) {
                        // See if we've had any activity -- either
                        // an incoming connection, or incoming data on an
                        // existing connection
                        int num = selector.select();

                        // If we don't have any activity, loop around and wait
                        // again
                        if (num == 0) {
                            continue;
                        }

                        // Get the keys corresponding to the activity
                        // that has been detected, and process them
                        // one by one
                        Set keys = selector.selectedKeys();
                        Iterator it = keys.iterator();
                        while (it.hasNext()) {
                            // Get a key representing one of bits of I/O
                            // activity
                            SelectionKey key = (SelectionKey)it.next();

                            // Accept
                            if (key.isValid() && key.isAcceptable()) {
                                System.out.println("[BRIDGE SERVER] accept");
                                // It's an incoming connection.
                                // Register this socket with the Selector
                                // so we can listen for input on it

                                Socket s = ss.accept();
                                System.out.println("[BRIDGE SERVER] Got connection from " + s);

                                // Make sure to make it non-blocking, so we can
                                // use a selector on it.
                                SocketChannel sc = s.getChannel();
                                sc.configureBlocking( false );

                                // Register it with the selector, for reading
                                sc.register(selector, SelectionKey.OP_READ );

                                onAccept(sc);
                            }

                            // Read
                            else if (key.isValid() && key.isReadable()) {
                                SocketChannel sc = null;

                                try {
                                    // It's incoming data on a connection, so
                                    // process it
                                    sc = (SocketChannel)key.channel();
                                    boolean ok = processInput( sc );

                                    // If the connection is dead, then remove it
                                    // from the selector and close it
                                    if (!ok) {
                                        key.cancel();

                                        Socket s = null;
                                        try {
                                            s = sc.socket();
                                            s.close();
                                        } catch( IOException ie ) {
                                            System.err.println("[BRIDGE SERVER] Error closing socket "+s+": "+ie );
                                        }
                                    }

                                } catch( IOException ie ) {
                                    // Remove this channel from the selector
                                    key.cancel();
                                    try { sc.close(); } catch( IOException ie2 ) { System.out.println( ie2 ); }
                                    System.out.println("[BRIDGE SERVER] Closed " + sc);

                                    onSocketChannelClosed(sc);
                                }
                            }
                        }

                        // We remove the selected keys, because we've dealt
                        // with them.
                        keys.clear();
                    }
                } catch( IOException ie ) {
                    System.err.println( ie );
                }
            }

            // Do some cheesy encryption on the incoming data,
            // and send it back out
            private boolean processInput( SocketChannel sc ) throws IOException {
                mReadBuffer.clear();
                int bytesRead = sc.read(mReadBuffer);
                mReadBuffer.flip();

                // If no data, close the connection
                if (mReadBuffer.limit()==0) {
                    return false;
                }

                System.out.println("[BRIDGE SERVER] RECEIVE: " + new String(mReadBuffer.array(), 0, bytesRead, "UTF-8"));

                mQueue.add(new QueueElement(sc, new String(mReadBuffer.array(), 0, bytesRead, "UTF-8")));

                return true;
            }
        }).start();
    }

    protected void onSocketChannelClosed(SocketChannel sc) {

    }

    /**
     *
     * @param message
     *
     * @return true if the message was write on the socketChannel
     */
    public boolean write(String message) {
        _clients.forEach(socketChannel -> write(socketChannel, message));
        return true;
    }

    public void writeObject(SocketChannel sc, Object object) {
//        try {
//            Output output = new Output(new FileOutputStream("file.bin"));
//            kryo.writeObject(output, object);
//            output.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public boolean write(SocketChannel sc, String message) {
        try {
            byte[] bytes = (message + "\n").getBytes(CHARSET);
//            System.out.println("message: " + message);
//            System.out.println("limit: " + mWriteBuffer.limit());
//            System.out.println("position: " + mWriteBuffer.position());
//            System.out.println("capacity: " + mWriteBuffer.capacity());
//            System.out.println("remaining: " + mWriteBuffer.remaining());
            mWriteBuffer.clear();
            mWriteBuffer.put(bytes);
            mWriteBuffer.flip();
            sc.write(mWriteBuffer);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("[BRIDGE SERVER] message: " + message);
//            System.out.println("position: " + mWriteBuffer.position());
//            System.out.println("capacity: " + mWriteBuffer.capacity());
//            System.out.println("limit6: " + mWriteBuffer.limit());
//            System.out.println(message);
            e.printStackTrace();
//            throw new RuntimeException("boom");
        } catch (ClosedChannelException e) {
            System.out.println("[BRIDGE SERVER] message: " + message);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[BRIDGE SERVER] message: " + message);
            e.printStackTrace();
            try { sc.close(); }
            catch (IOException e1) { e1.printStackTrace(); }
        } finally {
            mWriteBuffer.clear();
        }
        return false;
    }

    protected void onAccept(SocketChannel sc) {
        _clients.add(sc);
//
//        Application.gameManager.getGame().getModules().forEach(module -> module.onClientConnect(new ClientModel(sc)));

        write(sc, "Hello from server");
    }
}
