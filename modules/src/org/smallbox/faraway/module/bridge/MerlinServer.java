package org.smallbox.faraway.module.bridge;

import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
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

    public void close() {
        _run = false;
    }

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

        switch (message) {
            case "hello":
                for (ParcelModel parcel: ModuleHelper.getWorldModule().getParcelList()) {
                    if (parcel.z == 0 && (parcel.getResource() != null || parcel.getItem() != null || parcel.getConsumable() != null || parcel.getStructure() != null)) {
                        sendEvent("ParcelInfo", (doc, rootElement) -> {
                            Element parcelElement = doc.createElement("parcel");
                            appendChild(doc, parcelElement, "x", parcel.x);
                            appendChild(doc, parcelElement, "y", parcel.y);
                            appendChild(doc, parcelElement, "z", parcel.z);
                            rootElement.appendChild(parcelElement);

                            if (parcel.getStructure() != null) {
                                Element structureElement = doc.createElement("structure");
                                appendChild(doc, structureElement, "id", parcel.getStructure().getId());
                                appendChild(doc, structureElement, "name", parcel.getStructure().getName());
                                rootElement.appendChild(structureElement);
                            }

                            if (parcel.getResource() != null) {
                                Element resourceElement = doc.createElement("resource");
                                appendChild(doc, resourceElement, "id", parcel.getResource().getId());
                                appendChild(doc, resourceElement, "name", parcel.getResource().getName());
                                rootElement.appendChild(resourceElement);
                            }

                            if (parcel.getItem() != null) {
                                Element itemElement = doc.createElement("item");
                                appendChild(doc, itemElement, "id", parcel.getItem().getId());
                                appendChild(doc, itemElement, "name", parcel.getItem().getName());
                                rootElement.appendChild(itemElement);
                            }

                            if (parcel.getConsumable() != null) {
                                Element consumableElement = doc.createElement("consumable");
                                appendChild(doc, consumableElement, "id", parcel.getConsumable().getId());
                                appendChild(doc, consumableElement, "name", parcel.getConsumable().getName());
                                rootElement.appendChild(consumableElement);
                            }
                        });
                    }
                }
                break;
        }
    }

    public void sendMessage(String message) {
        mClients.forEach(c -> sendMessage(c, message));
    }

    public void sendMessage(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            transformer.transform(source, result);
            sendMessage(sw.toString());
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private interface EventBuilder {
        void onBuildEvent(Document doc, Element rootElement);
    }

    private void sendEvent(String cmd, EventBuilder builder) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("event");
            rootElement.setAttribute("cmd", cmd);
            doc.appendChild(rootElement);

            builder.onBuildEvent(doc, rootElement);

            sendMessage(doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void appendChild(Document doc, Element characterElement, String name, int data) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(String.valueOf(data)));
        characterElement.appendChild(element);
    }

    private void appendChild(Document doc, Element characterElement, String name, String data) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(data));
        characterElement.appendChild(element);
    }
}
