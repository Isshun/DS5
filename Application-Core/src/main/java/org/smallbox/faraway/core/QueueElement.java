package org.smallbox.faraway.core;

import java.nio.channels.SocketChannel;

public class QueueElement {
    public final SocketChannel  sc;
    public final String         message;

    public QueueElement(SocketChannel sc, String message) {
        this.sc = sc;
        this.message = message;
    }
}