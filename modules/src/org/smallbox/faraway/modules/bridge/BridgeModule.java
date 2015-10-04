package org.smallbox.faraway.modules.bridge;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.modules.quest.QuestModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Alex on 22/08/2015.
 */
public class BridgeModule extends GameModule {
    private MerlinServer mServer;

    @Override
    protected void onLoaded() {
        mServer = new MerlinServer(4242);
    }

    protected void onDestroy() {
        mServer.close();
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        sendEvent("AddCharacter", (doc, rootElement) -> {
            Element characterElement = doc.createElement("character");
            rootElement.appendChild(characterElement);

            appendChild(doc, characterElement, "id", character.getId());
            appendChild(doc, characterElement, "name", character.getName());
            appendChild(doc, characterElement, "x", character.getX());
            appendChild(doc, characterElement, "y", character.getY());
            appendChild(doc, characterElement, "z", character.getZ());
        });
    }

    @Override
    public void onAddStructure(StructureModel structure) {
        sendEvent("AddStructure", (doc, rootElement) -> {
            Element characterElement = doc.createElement("structure");
            rootElement.appendChild(characterElement);

            appendChild(doc, characterElement, "id", structure.getId());
            appendChild(doc, characterElement, "name", structure.getName());
            appendChild(doc, characterElement, "x", structure.getX());
            appendChild(doc, characterElement, "y", structure.getY());
            appendChild(doc, characterElement, "z", 0);
        });
    }

    @Override
    public void onAddItem(ItemModel item) {
        sendEvent("AddItem", (doc, rootElement) -> {
            Element characterElement = doc.createElement("item");
            rootElement.appendChild(characterElement);

            appendChild(doc, characterElement, "id", item.getId());
            appendChild(doc, characterElement, "name", item.getName());
            appendChild(doc, characterElement, "x", item.getX());
            appendChild(doc, characterElement, "y", item.getY());
            appendChild(doc, characterElement, "z", 0);
        });
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable) {
        sendEvent("AddConsumable", (doc, rootElement) -> {
            Element characterElement = doc.createElement("consumable");
            rootElement.appendChild(characterElement);

            appendChild(doc, characterElement, "id", consumable.getId());
            appendChild(doc, characterElement, "name", consumable.getName());
            appendChild(doc, characterElement, "x", consumable.getX());
            appendChild(doc, characterElement, "y", consumable.getY());
            appendChild(doc, characterElement, "z", 0);
        });
    }

    @Override
    public void onAddResource(ResourceModel resource) {
        sendEvent("AddResource", (doc, rootElement) -> {
            Element characterElement = doc.createElement("resource");
            rootElement.appendChild(characterElement);

            appendChild(doc, characterElement, "id", resource.getId());
            appendChild(doc, characterElement, "name", resource.getName());
            appendChild(doc, characterElement, "x", resource.getX());
            appendChild(doc, characterElement, "y", resource.getY());
            appendChild(doc, characterElement, "z", 0);
        });
    }

    @Override
    public void onRemoveItem(ItemModel item) {
        sendEvent("RemoveItem", (doc, rootElement) -> {
            Element characterElement = doc.createElement("item");
            rootElement.appendChild(characterElement);

            appendChild(doc, characterElement, "id", item.getId());
            appendChild(doc, characterElement, "name", item.getName());
            appendChild(doc, characterElement, "x", item.getX());
            appendChild(doc, characterElement, "y", item.getY());
            appendChild(doc, characterElement, "z", 0);
        });
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable) {
        sendEvent("RemoveConsumable", (doc, rootElement) -> {
            Element characterElement = doc.createElement("consumable");
            rootElement.appendChild(characterElement);

            appendChild(doc, characterElement, "id", consumable.getId());
            appendChild(doc, characterElement, "name", consumable.getName());
            appendChild(doc, characterElement, "x", consumable.getX());
            appendChild(doc, characterElement, "y", consumable.getY());
            appendChild(doc, characterElement, "z", 0);
        });
    }

    @Override
    public void onRemoveStructure(StructureModel structure) {
        sendEvent("RemoveStructure", (doc, rootElement) -> {
            Element characterElement = doc.createElement("structure");
            rootElement.appendChild(characterElement);

            appendChild(doc, characterElement, "id", structure.getId());
            appendChild(doc, characterElement, "name", structure.getName());
            appendChild(doc, characterElement, "x", structure.getX());
            appendChild(doc, characterElement, "y", structure.getY());
            appendChild(doc, characterElement, "z", 0);
        });
    }

    @Override
    public void onRemoveResource(ResourceModel resource) {
        sendEvent("RemoveResource", (doc, rootElement) -> {
            Element characterElement = doc.createElement("resource");
            rootElement.appendChild(characterElement);

            appendChild(doc, characterElement, "id", resource.getId());
            appendChild(doc, characterElement, "name", resource.getName());
            appendChild(doc, characterElement, "x", resource.getX());
            appendChild(doc, characterElement, "y", resource.getY());
            appendChild(doc, characterElement, "z", 0);
        });
    }

    @Override
    public void onRefreshItem(ItemModel item) {

    }

    @Override
    public void onRefreshStructure(StructureModel structure) {

    }

    @Override
    public void onHourChange(int hour) {
        sendEvent("HourChange", (doc, rootElement) -> {
            appendChild(doc, rootElement, "hour", hour);
        });
    }

    @Override
    public void onDayChange(int day) {
        sendEvent("DayChange", (doc, rootElement) -> {
            appendChild(doc, rootElement, "day", day);
        });
    }

    @Override
    public void onYearChange(int year) {
        sendEvent("YearChange", (doc, rootElement) -> {
            appendChild(doc, rootElement, "year", year);
        });
    }

    @Override
    public void onSelectCharacter(CharacterModel character) {

    }

    @Override
    public void onSelectParcel(ParcelModel parcel) {

    }

    @Override
    public void onSelectItem(ItemModel item) {

    }

    @Override
    public void onSelectResource(ResourceModel resource) {

    }

    @Override
    public void onSelectConsumable(ConsumableModel consumable) {

    }

    @Override
    public void onSelectStructure(StructureModel structure) {

    }

    @Override
    public void onDeselect() {

    }

    @Override
    public void onStartGame() {

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

            mServer.sendMessage(doc);
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
