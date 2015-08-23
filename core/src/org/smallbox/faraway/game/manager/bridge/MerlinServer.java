package org.smallbox.faraway.game.manager.bridge;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 25/02/2015.
 */
public class MerlinServer extends Server {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private long                mCurrentTime;
    private SocketChannel       mMonitorSc;
    private List<SocketChannel> mClients = new ArrayList<>();

    public MerlinServer(int port) {
        super(port);

        long sleepTime;

//        while (true) {
//            mCurrentTime = System.currentTimeMillis();
//
//            synchronized (this) {
////                toRemoveMessage.clear();
////                for (DelayedMessage delayed : mDelayed) {
////                    if (mCurrentTime > delayed.delay) {
////                        toRemoveMessage.add(delayed);
////                    }
////                }
////                for (DelayedMessage delayed : toRemoveMessage) {
////                    if (delayed.isNotice) {
////                        sendNotice(delayed.party, delayed.message);
////                    } else {
////                        sendMessage(delayed.party, delayed.message);
////                    }
////                    if (delayed.loopBack) {
////                        delayed.party.onReceive(delayed.message);
////                    }
////                }
////                mDelayed.removeAll(toRemoveMessage);
//            }
//
////            try {
////                sleepTime = 100 - (System.currentTimeMillis() - mCurrentTime);
////                if (sleepTime > 0) {
////                    Thread.sleep(sleepTime);
////                }
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
    }

//    private void sendHeartbeat(PartyModel party) {
//        for (PlayerModel player: party.getTotalPlayers()) {
//            if (!player.isFake && player.isConnected) {
//                sendMessage(player, "{\"cmd\": \"ping\"}");
//            }
//        }
//    }

    @Override
    protected void onSocketChannelClosed(SocketChannel sc) {
//        PlayerModel player = mPlayers.get(sc);
//        if (player != null && player.isConnected && player.party != null) {
//            player.isConnected = false;
//            sendMessage(player.party, RequestFactory.bye(player, "connection reset"), 0, true);
//        }
    }

    @Override
    protected void onAccept(SocketChannel sc) {
        mClients.add(sc);
        sendMessage(sc, "Hello");
    }

    @Override
    protected void onMessage(SocketChannel sc, String message) {
        System.out.println("Receive: " + message);
    }

    public void sendMessage(String message) {
        mClients.forEach(c -> sendMessage(c, message));
    }
}
