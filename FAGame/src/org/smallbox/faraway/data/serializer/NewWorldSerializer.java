package org.smallbox.faraway.data.serializer;

import com.ximpleware.*;
import com.ximpleware.EOFException;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.WorldManager;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.util.Log;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class NewWorldSerializer implements SerializerInterface {

//    private static void saveParcel(List<WorldSaveParcel> parcels, ParcelModel parcel) {
////		if (parcel.getItem() == null && parcel.getResource() == null && parcel.getStructure() == null) {
////			return;
////		}
//        ItemModel item = parcel.getRootItem();
//        ConsumableModel consumable = parcel.getConsumable();
//        StructureModel structure = parcel.getStructure();
//        ResourceModel resource = parcel.getResource();
//
////		if (item != null || consumable != null || structure != null || resource != null) {
//        WorldSaveParcel parcelSave = new WorldSaveParcel();
//
//        parcelSave.x = parcel.getX();
//        parcelSave.y = parcel.getY();
//        parcelSave.z = parcel.getZ();
//        parcelSave.lightSource = parcel.getLightSource();
//
//        if (item != null) {
//            parcelSave.item = new WorldSaveUserItem();
//            parcelSave.item.id = item.getId();
//            parcelSave.item.name = item.getName();
//            parcelSave.item.progress = item.getProgress();
//        }
//
//        if (consumable != null) {
//            parcelSave.consumable = new WorldSaveConsumableItem();
//            parcelSave.consumable.id = consumable.getId();
//            parcelSave.consumable.name = consumable.getName();
//            parcelSave.consumable.quantity = consumable.getQuantity();
//        }
//
//        if (structure != null) {
//            parcelSave.structure = new WorldSaveStructure();
//            parcelSave.structure.name = structure.getName();
//            parcelSave.structure.progress = structure.getProgress();
//            parcelSave.structure.id = structure.getId();
//            parcelSave.structure.health = structure.getHealth();
//        }
//
//        if (resource != null) {
//            parcelSave.resource = new WorldSaveResource();
//            parcelSave.resource.name = resource.getName();
//            parcelSave.resource.tile = resource.getTile();
//            parcelSave.resource.value = resource.getQuantity();
//            parcelSave.resource.id = resource.getId();
//        }
//
//        parcels.add(parcelSave);
////		}
//    }

    @Override
    public void save(GameSerializer.GameSave save) {

        try {
            File f = new File("data/saves/6.sav");
            FileOutputStream fos = new FileOutputStream(f);

            write(fos, "<?xml version='1.0' encoding='UTF-8'?>");
            write(fos, "<save>");

            write(fos, "<parcels>");
            for (ParcelModel parcel: Game.getWorldManager().getParcelList()) {
                writeParcel(fos, parcel);
            }
            write(fos, "</parcels>");

            write(fos, "</save>");

            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeParcel(FileOutputStream fos, ParcelModel parcel) throws IOException {
        if (parcel.getItem() != null || parcel.getResource() != null || parcel.getStructure() != null || parcel.getConsumable() != null) {
            write(fos, "<parcel x='" + parcel.getX() + "' y='" + parcel.getY() + "' z='" + parcel.getZ() + "' type='" + parcel.getType() + "'>");

            if (parcel.getItem() != null) {
                writeItem(fos, parcel.getItem());
            }

            if (parcel.getStructure() != null) {
                writeStructure(fos, parcel.getStructure());
            }

            if (parcel.getResource() != null) {
                writeResource(fos, parcel.getResource());
            }

            if (parcel.getConsumable() != null) {
                writeConsumable(fos, parcel.getConsumable());
            }

            write(fos, "</parcel>");
        } else {
            write(fos, "<parcel x='" + parcel.getX() + "' y='" + parcel.getY() + "' z='" + parcel.getZ() + "' type='" + parcel.getType() + "' />");
        }
    }

    private void writeItem(FileOutputStream fos, ItemModel item) throws IOException {
        write(fos, "<item id='" + item.getId() + "' name='" + item.getInfo().name + "'>");
        write(fos, "<health>" + item.getHealth() + "</health>");
        write(fos, "<progress>" + item.getProgress() + "</progress>");
        write(fos, "</item>");
    }

    private void writeStructure(FileOutputStream fos, StructureModel structure) throws IOException {
        write(fos, "<structure id='" + structure.getId() + "' name='" + structure.getInfo().name + "'>");
        write(fos, "<health>" + structure.getHealth() + "</health>");
        write(fos, "<progress>" + structure.getProgress() + "</progress>");
        write(fos, "</structure>");
    }

    private void writeResource(FileOutputStream fos, ResourceModel resource) throws IOException {
        write(fos, "<resource id='" + resource.getId() + "' name='" + resource.getInfo().name + "'>");
        write(fos, "<health>" + resource.getHealth() + "</health>");
        write(fos, "<progress>" + resource.getProgress() + "</progress>");
        write(fos, "</resource>");
    }

    private void writeConsumable(FileOutputStream fos, ConsumableModel consumable) throws IOException {
        write(fos, "<consumable id='" + consumable.getId() + "' name='" + consumable.getInfo().name + "'>");
        write(fos, "<quantity>" + consumable.getQuantity() + "</quantity>");
        write(fos, "</consumable>");
    }

    private static void write(FileOutputStream fos, String str) throws IOException {
        fos.write(str.getBytes("UTF-8"));
        fos.write('\n');
        fos.flush();
    }

    public void load(VTDNav vn) throws XPathParseException, NavException, XPathEvalException {
        AutoPilot ap = new AutoPilot(vn);
        ap.selectXPath("/save/parcels/*");

        AutoPilot apItem = new AutoPilot(vn);
        apItem.selectXPath("item|resource|structure|consumable");

        AutoPilot apElement = new AutoPilot(vn);
        apElement.selectXPath("*");

        WorldManager manager = Game.getWorldManager();

        while (ap.evalXPath() != -1) {
            vn.push();

            int x = vn.parseInt(vn.getAttrVal("x"));
            int y = vn.parseInt(vn.getAttrVal("y"));
            int z = vn.parseInt(vn.getAttrVal("z"));
            int type = vn.parseInt(vn.getAttrVal("type"));

            while (apItem.evalXPath() != -1) {
                switch (vn.toString(vn.getCurrentIndex())) {
                    case "item":
                        createItem(apElement, vn, manager, x, y, z);
                        break;
                    case "resource":
                        createResource(apElement, vn, manager, x, y, z);
                        break;
                    case "structure":
                        createStructure(apElement, vn, manager, x, y, z);
                        break;
                    case "consumable":
                        createConsumable(apElement, vn, manager, x, y, z);
                        break;
                }
            }
            apItem.resetXPath();
            vn.pop();
        }
    }

    private void createConsumable(AutoPilot apElement, VTDNav vn, WorldManager manager, int x, int y, int z) throws NavException, XPathEvalException {
        String name = vn.toString(vn.getAttrVal("name"));
        int id = vn.parseInt(vn.getAttrVal("id"));
        int quantity = 0;

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "quantity":
                    quantity = (int)vn.parseDouble(vn.getText());
                    break;
            }
        }
        apElement.resetXPath();

        ConsumableModel consumable = (ConsumableModel)manager.putObject(name, x, y, z, quantity);
        if (consumable != null) {
            consumable.setId(id);
            consumable.setQuantity(quantity);
        }
    }

    private void createStructure(AutoPilot apElement, VTDNav vn, WorldManager manager, int x, int y, int z) throws NavException, XPathEvalException {
        String name = vn.toString(vn.getAttrVal("name"));
        int id = vn.parseInt(vn.getAttrVal("id"));
        int health = 0;
        int progress = 0;

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "health":
                    health = (int)vn.parseDouble(vn.getText());
                    break;

                case "progress":
                    progress = vn.parseInt(vn.getText());
                    break;
            }
        }
        apElement.resetXPath();

        StructureModel structure = (StructureModel)manager.putObject(name, x, y, z, progress);
        if (structure != null) {
            structure.setId(id);
            structure.setHealth(health);
        }
    }

    private void createResource(AutoPilot apElement, VTDNav vn, WorldManager manager, int x, int y, int z) throws NavException, XPathEvalException {
        String name = vn.toString(vn.getAttrVal("name"));
        int id = vn.parseInt(vn.getAttrVal("id"));
        int health = 0;
        int progress = 0;

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "health":
                    health = (int)vn.parseDouble(vn.getText());
                    break;

                case "progress":
                    progress = vn.parseInt(vn.getText());
                    break;
            }
        }
        apElement.resetXPath();

        ResourceModel resource = (ResourceModel)manager.putObject(name, x, y, z, progress);
        if (resource != null) {
//            resource.setTile(tile);
//            resource.setValue(value);
            resource.setId(id);
        }
    }

    private void createItem(AutoPilot apElement, VTDNav vn, WorldManager manager, int x, int y, int z) throws NavException, XPathEvalException {
        String name = vn.toString(vn.getAttrVal("name"));
        int id = vn.parseInt(vn.getAttrVal("id"));
        int health = 0;
        int progress = 0;

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "health":
                    health = (int)vn.parseDouble(vn.getText());
                    break;

                case "progress":
                    progress = vn.parseInt(vn.getText());
                    break;
            }
        }
        apElement.resetXPath();

        ItemModel item = (ItemModel)manager.putObject(name, x, y, z, progress);
        if (item != null) {
            item.setId(id);
            item.setHealth(health);
        }
    }

}
