package org.smallbox.faraway.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.reflections.Reflections;
import org.smallbox.faraway.GameSerializer;
import org.smallbox.faraway.common.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameServerKyro {

    private List<Connection> _connections = new ArrayList<>();
    private Map<Class, ModelSerializer> _serializers = new ConcurrentHashMap<>();

    public GameServerKyro() throws IOException {

//        _serializers.put(HumanModel.class, new CharacterSerializer());
//        _serializers.put(RandomMoveTask.class, new GameTaskSerializer());
//        _serializers.put(PlantGrowTask.class, new GameTaskSerializer());

        // Add serializers to map
        new Reflections("org.smallbox.faraway").getTypesAnnotatedWith(GameSerializer.class)
                .forEach(cls -> {
                    try {
                        _serializers.put(cls, cls.getAnnotation(GameSerializer.class).value().newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        Server server = new Server();
        server.start();

        Kryo kryo = server.getKryo();
        kryo.register(SomeRequest.class);
        kryo.register(SomeResponse.class);
        kryo.register(ParcelCommon.class);
        kryo.register(CharacterPositionCommon.class);
        kryo.register(KryoMessage.class);
//        kryo.register(ItemInfo.class);
//        kryo.register(ItemInfo.ItemInfoAction.class);
//        kryo.register(ItemInfo.ItemProductInfo.class);
//        kryo.register(ArrayList.class);

        server.bind(54555, 54777);

        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                System.out.println("[SERVER] RECEIVE");

                if (object instanceof SomeRequest) {
                    SomeRequest request = (SomeRequest)object;
                    System.out.println("[SERVER] " + request.text);

                    switch (request.text) {
                        case "hello":
                            System.out.println("[SERVER] NEW CONNECTION");
                            _connections.add(connection);
                            Application.gameManager.getGame().getModules().forEach(module -> module.onClientConnect(connection));
                            break;
                    }

                    SomeResponse response = new SomeResponse();
                    response.text = "Thanks";
                    connection.sendTCP(response);
                }

                if (object instanceof FrameworkMessage) {
                    FrameworkMessage message = (FrameworkMessage)object;
                    System.out.println("[SERVER] " + message);
                }

            }
        });
    }

    public void writeObject(Connection connection, Object object) {
        connection.sendTCP(object);
    }

    public void write(Object object) {
        _connections.forEach(connection -> connection.sendTCP(object));
    }

    public void serialize(String action, String type, long id, Object object) {
        KryoMessage message = new KryoMessage();
        message.id = id;
        message.action = action;
        message.type = type;

        ModelSerializer serializer = _serializers.get(object.getClass());
        if (serializer == null) {
            throw new RuntimeException("No serializer for class: " + object.getClass());
        }

        message.data = serializer.serialize(object).toString();

//        System.out.println("[Send data] " + message.data);

        write(message);
    }

}
