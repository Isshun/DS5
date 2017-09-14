package org.smallbox.faraway.core.engine.module;

import com.esotericsoftware.kryo.Kryo;

import java.nio.channels.SocketChannel;

public class ClientModel {

    private final SocketChannel sc;

    private Kryo kryo = new Kryo();

    public ClientModel(SocketChannel sc) {
        this.sc = sc;
    }

    public void send(Object object) {

//        Application.gameServer.writeObject(sc, object);

    }

}
