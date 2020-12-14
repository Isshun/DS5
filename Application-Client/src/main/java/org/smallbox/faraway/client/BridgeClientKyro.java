package org.smallbox.faraway.client;

import org.json.JSONObject;
import org.smallbox.faraway.client.module.CharacterClientModule;
import org.smallbox.faraway.client.module.PlantClientModule;
import org.smallbox.faraway.client.module.TaskClientModule;
import org.smallbox.faraway.common.*;
import org.smallbox.faraway.core.bridge.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BridgeClientKyro {
    private Client client;
    private List<ServerListener> _listeners = new ArrayList<>();
    private List<ModelDeserializer> _deserializer = new ArrayList<>();
    private CharacterClientModule characterClientModule = ApplicationClient.dependencyInjector.create(CharacterClientModule.class);
    private PlantClientModule plantClientModule = ApplicationClient.dependencyInjector.create(PlantClientModule.class);
    private TaskClientModule taskClientModule = ApplicationClient.dependencyInjector.create(TaskClientModule.class);

    public void register(ServerListener listener) {
        System.out.println("[CLIENT] REGISTER");

//        _listeners.add(listener);
//
//        try {
//            client = new Client();
//            client.start();
//            client.connect(5000, "localhost", 54555, 54777);
//
//            Kryo kryo = client.getKryo();
//            kryo.register(SomeRequest.class);
//            kryo.register(SomeResponse.class);
//            kryo.register(ParcelCommon.class);
//            kryo.register(CharacterPositionCommon.class);
//            kryo.register(KryoMessage.class);
//
//            client.addListener(new Listener() {
//                public void received (Connection connection, Object object) {
//                    if (object instanceof KryoMessage) {
//                        KryoMessage message = (KryoMessage)object;
//                        System.out.println("[CLIENT] " + message.type + " - " + message.data);
//
//                        if ("PLANT".equals(message.type)) {
//                            PlantCommon plant = new PlantCommonDeserializer().deserialize(new JSONObject(message.data));
//                            plantClientModule.update(plant);
//                        }
//
//                        if ("CHARACTER".equals(message.type)) {
//                            CharacterCommon character = new CharacterCommonDeserializer().deserialize(new JSONObject(message.data));
//                            characterClientModule.update(character);
//                        }
//
////                        characterClientModule.onReceiveCharacter(message.data);
//                    }
//                    if (object instanceof SomeResponse) {
//                        SomeResponse response = (SomeResponse)object;
//                        System.out.println("[CLIENT] " + response.text);
//                    }
//                    if (object instanceof ParcelCommon) {
//                        ParcelCommon parcel = (ParcelCommon)object;
//                        _listeners.forEach(l -> l.onUpdate(parcel));
////                        System.out.println("[CLIENT] " + parcel.x + "x" + parcel.y + "x" + parcel.z);
//                    }
//                }
//            });
//
//            sendMessage("hello");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void sendMessage(String message) {
//        SomeRequest request = new SomeRequest();
//        request.text = message;
//        client.sendTCP(request);
    }
}
