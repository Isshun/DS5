package org.smallbox.faraway.core.engine.module;

import java.nio.channels.SocketChannel;

public class ClientModel {

    private final SocketChannel sc;

    public ClientModel(SocketChannel sc) {
        this.sc = sc;
    }

    public void send(Object object) {

//        Application.gameServer.writeObject(sc, object);

    }

}
