//package org.smallbox.faraway.module.bridge;
//
//import org.smallbox.faraway.core.module.world.item.*;
//import org.smallbox.faraway.core.module.org.smallbox.faraway.core.module.room.model.org.smallbox.faraway.core.module.room.model.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.*;
//import org.smallbox.faraway.core.engine.module.GameModule;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
///**
// * Created by Alex on 22/08/2015.
// */
//public class BridgeModule extends GameModule {
//    private MerlinServer mServer;
//
//    @Override
//    protected void onGameStart() {
//        mServer = new MerlinServer(4242);
//    }
//
//    protected void onDestroy() {
//        mServer.close();
//    }
//
//    @Override
//    protected void onGameUpdate(int tick) {
//    }
//
//    @Override
//    public void onAddCharacter(CharacterModel model) {
//        sendEvent("AddCharacter", (doc, rootElement) -> {
//            Element characterElement = doc.createElement("model");
//            rootElement.appendChild(characterElement);
//
//            appendChild(doc, characterElement, "id", model.getId());
//            appendChild(doc, characterElement, "name", model.getName());
//            appendChild(doc, characterElement, "x", model.getX());
//            appendChild(doc, characterElement, "y", model.getY());
//            appendChild(doc, characterElement, "z", model.getZ());
//        });
//    }
//
//    @Override
//    public void onAddStructure(StructureItem structure) {
//        sendEvent("AddStructure", (doc, rootElement) -> {
//            Element characterElement = doc.createElement("structure");
//            rootElement.appendChild(characterElement);
//
//            appendChild(doc, characterElement, "id", structure.getId());
//            appendChild(doc, characterElement, "name", structure.getName());
//            appendChild(doc, characterElement, "x", structure.getX());
//            appendChild(doc, characterElement, "y", structure.getY());
//            appendChild(doc, characterElement, "z", 0);
//        });
//    }
//
//    @Override
//    public void onAddItem(UsableItem item) {
//        sendEvent("AddItem", (doc, rootElement) -> {
//            Element characterElement = doc.createElement("item");
//            rootElement.appendChild(characterElement);
//
//            appendChild(doc, characterElement, "id", item.getId());
//            appendChild(doc, characterElement, "name", item.getName());
//            appendChild(doc, characterElement, "x", item.getX());
//            appendChild(doc, characterElement, "y", item.getY());
//            appendChild(doc, characterElement, "z", 0);
//        });
//    }
//
//    @Override
//    public void onAddConsumable(ConsumableItem consumable) {
//        sendEvent("AddConsumable", (doc, rootElement) -> {
//            Element characterElement = doc.createElement("consumable");
//            rootElement.appendChild(characterElement);
//
//            appendChild(doc, characterElement, "id", consumable.getId());
//            appendChild(doc, characterElement, "name", consumable.getName());
//            appendChild(doc, characterElement, "x", consumable.getX());
//            appendChild(doc, characterElement, "y", consumable.getY());
//            appendChild(doc, characterElement, "z", 0);
//        });
//    }
//
//    @Override
//    public void onAddResource(ResourceModel resource) {
//        sendEvent("AddResource", (doc, rootElement) -> {
//            Element characterElement = doc.createElement("resource");
//            rootElement.appendChild(characterElement);
//
//            appendChild(doc, characterElement, "id", resource.getId());
//            appendChild(doc, characterElement, "name", resource.getName());
//            appendChild(doc, characterElement, "x", resource.getX());
//            appendChild(doc, characterElement, "y", resource.getY());
//            appendChild(doc, characterElement, "z", 0);
//        });
//    }
//
//    @Override
//    public void onRemoveItem(UsableItem item) {
//        sendEvent("RemoveItem", (doc, rootElement) -> {
//            Element characterElement = doc.createElement("item");
//            rootElement.appendChild(characterElement);
//
//            appendChild(doc, characterElement, "id", item.getId());
//            appendChild(doc, characterElement, "name", item.getName());
//            appendChild(doc, characterElement, "x", item.getX());
//            appendChild(doc, characterElement, "y", item.getY());
//            appendChild(doc, characterElement, "z", 0);
//        });
//    }
//
//    @Override
//    public void onRemoveConsumable(ConsumableItem consumable) {
//        sendEvent("RemoveConsumable", (doc, rootElement) -> {
//            Element characterElement = doc.createElement("consumable");
//            rootElement.appendChild(characterElement);
//
//            appendChild(doc, characterElement, "id", consumable.getId());
//            appendChild(doc, characterElement, "name", consumable.getName());
//            appendChild(doc, characterElement, "x", consumable.getX());
//            appendChild(doc, characterElement, "y", consumable.getY());
//            appendChild(doc, characterElement, "z", 0);
//        });
//    }
//
//    @Override
//    public void onRemoveStructure(StructureItem structure) {
//        sendEvent("RemoveStructure", (doc, rootElement) -> {
//            Element characterElement = doc.createElement("structure");
//            rootElement.appendChild(characterElement);
//
//            appendChild(doc, characterElement, "id", structure.getId());
//            appendChild(doc, characterElement, "name", structure.getName());
//            appendChild(doc, characterElement, "x", structure.getX());
//            appendChild(doc, characterElement, "y", structure.getY());
//            appendChild(doc, characterElement, "z", 0);
//        });
//    }
//
//    @Override
//    public void onRemovePlant(ResourceModel resource) {
//        sendEvent("RemoveResource", (doc, rootElement) -> {
//            Element characterElement = doc.createElement("resource");
//            rootElement.appendChild(characterElement);
//
//            appendChild(doc, characterElement, "id", resource.getId());
//            appendChild(doc, characterElement, "name", resource.getName());
//            appendChild(doc, characterElement, "x", resource.getX());
//            appendChild(doc, characterElement, "y", resource.getY());
//            appendChild(doc, characterElement, "z", 0);
//        });
//    }
//
//    @Override
//    public void onRefreshItem(UsableItem item) {
//
//    }
//
//    @Override
//    public void onRefreshStructure(StructureItem structure) {
//
//    }
//
//    @Override
//    public void onHourChange(int hour) {
//        sendEvent("HourChange", (doc, rootElement) -> {
//            appendChild(doc, rootElement, "hour", hour);
//        });
//    }
//
//    @Override
//    public void onDayChange(int day) {
//        sendEvent("DayChange", (doc, rootElement) -> {
//            appendChild(doc, rootElement, "day", day);
//        });
//    }
//
//    @Override
//    public void onYearChange(int year) {
//        sendEvent("YearChange", (doc, rootElement) -> {
//            appendChild(doc, rootElement, "year", year);
//        });
//    }
//
//    @Override
//    public void onSelectCharacter(CharacterModel model) {
//
//    }
//
//    @Override
//    public void onSelectParcel(ParcelModel parcel) {
//
//    }
//
//    @Override
//    public void onSelectItem(UsableItem item) {
//
//    }
//
//    @Override
//    public void onSelectResource(ResourceModel resource) {
//
//    }
//
//    @Override
//    public void onSelectConsumable(ConsumableItem consumable) {
//
//    }
//
//    @Override
//    public void onSelectStructure(StructureItem structure) {
//
//    }
//
//    @Override
//    public void onDeselect() {
//
//    }
//
//    @Override
//    public void onGameStart() {
//
//    }
//
//    private interface EventBuilder {
//        void onBuildEvent(Document doc, Element rootElement);
//    }
//
//    private void sendEvent(String cmd, EventBuilder builder) {
//        try {
//            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//
//            // root elements
//            Document doc = docBuilder.newDocument();
//            Element rootElement = doc.createElement("event");
//            rootElement.setAttribute("cmd", cmd);
//            doc.appendChild(rootElement);
//
//            builder.onBuildEvent(doc, rootElement);
//
//            mServer.sendMessage(doc);
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void appendChild(Document doc, Element characterElement, String name, int data) {
//        Element element = doc.createElement(name);
//        element.appendChild(doc.createTextNode(String.valueOf(data)));
//        characterElement.appendChild(element);
//    }
//
//    private void appendChild(Document doc, Element characterElement, String name, String data) {
//        Element element = doc.createElement(name);
//        element.appendChild(doc.createTextNode(data));
//        characterElement.appendChild(element);
//    }
//
//}
