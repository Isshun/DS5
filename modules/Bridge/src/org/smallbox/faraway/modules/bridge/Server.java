package org.smallbox.faraway.module.bridge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public abstract class Server {
    private final static int BUF_SIZE = 65536;
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final int               mPort;
    private final ByteBuffer        mReadBuffer;
    private final ByteBuffer        mWriteBuffer;
    protected boolean               _run;

    public Server(int port) {
        mPort = port;
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
//                    Log.info( "Listening on mPort "+ mPort);

                    while (_run) {
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
                            if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
//                                if (Settings.DEBUG) {
                                    Log.info("accept");
//                                }
                                // It's an incoming connection.
                                // Register this socket with the Selector
                                // so we can listen for input on it

                                Socket s = ss.accept();
//                                if (Settings.DEBUG) {
                                    Log.info("Got connection from " + s);
//                                }

                                // Make sure to make it non-blocking, so we can
                                // use a selector on it.
                                SocketChannel sc = s.getChannel();
                                sc.configureBlocking( false );

                                // Register it with the selector, for reading
                                sc.register(selector, SelectionKey.OP_READ );

                                onAccept(sc);
                            }

                            // Read
                            else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
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
                                            System.err.println( "Error closing socket "+s+": "+ie );
                                        }
                                    }

                                } catch( IOException ie ) {
                                    // Remove this channel from the selector
                                    key.cancel();
                                    try { sc.close(); } catch( IOException ie2 ) { Log.info( ie2 ); }
//                                    if (Settings.DEBUG) {
                                        Log.info("Closed " + sc);
//                                    }

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

                onMessage(sc, new String(mReadBuffer.array(), 0, bytesRead, "UTF-8"));

                return true;
            }
        }).start();
    }

    protected abstract void onSocketChannelClosed(SocketChannel sc);

    /**
     *
     * @param sc
     * @param message
     *
     * @return true if the message was write on the socketChannel
     */
    protected boolean sendMessage(SocketChannel sc, String message) {
        Log.info("Send message: " + message);

        try {
            mWriteBuffer.clear();
            mWriteBuffer.put((message + "\n").getBytes(CHARSET));
            mWriteBuffer.flip();
            sc.write(mWriteBuffer);
            return true;
        } catch (ClosedChannelException e) {
        } catch (IOException e) {
            try { sc.close(); }
            catch (IOException e1) { e1.printStackTrace(); }
        }
        return false;
    }

    protected abstract void onMessage(SocketChannel sc, String message);

    protected abstract void onAccept(SocketChannel sc);
}
