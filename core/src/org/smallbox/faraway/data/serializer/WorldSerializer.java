package org.smallbox.faraway.data.serializer;

import com.ximpleware.*;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.world.WorldModule;
import org.smallbox.faraway.util.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;

public class WorldSerializer implements SerializerInterface {

    @Override
    public void save(FileOutputStream fos) throws IOException {
        FileUtils.write(fos, "<parcels>");
        for (ParcelModel parcel: Game.getWorldManager().getParcelList()) {
            if (parcel.z == 0) {
                writeParcel(fos, parcel);
            }
        }
        FileUtils.write(fos, "</parcels>");
    }

    private void writeParcel(FileOutputStream fos, ParcelModel parcel) throws IOException {
        if (parcel.getItem() != null || parcel.getResource() != null || parcel.getStructure() != null || parcel.getConsumable() != null) {
            FileUtils.write(fos, "<parcel x='" + parcel.x + "' y='" + parcel.y + "' z='" + parcel.z + "' type='" + parcel.getType() + "'>");

            if (parcel.getStructure() != null) {
                writeStructure(fos, parcel.getStructure());
            }

            if (parcel.getResource() != null) {
                writeResource(fos, parcel.getResource());
            }

            if (parcel.getItem() != null && parcel.getItem().getX() == parcel.x && parcel.getItem().getY() == parcel.y) {
                writeItem(fos, parcel.getItem());
            }

            if (parcel.getConsumable() != null) {
                writeConsumable(fos, parcel.getConsumable());
            }

            FileUtils.write(fos, "</parcel>");
        } else {
            FileUtils.write(fos, "<parcel x='" + parcel.x + "' y='" + parcel.y + "' z='" + parcel.z + "' type='" + parcel.getType() + "' />");
        }
    }

    private void writeItem(FileOutputStream fos, ItemModel item) throws IOException {
        FileUtils.write(fos, "<item id='" + item.getId() + "' name='" + item.getInfo().name + "'>");
        FileUtils.write(fos, "<health>" + item.getHealth() + "</health>");
        FileUtils.write(fos, "<progress>" + item.getProgress() + "</progress>");
        FileUtils.write(fos, "</item>");
    }

    private void writeStructure(FileOutputStream fos, StructureModel structure) throws IOException {
        FileUtils.write(fos, "<structure id='" + structure.getId() + "' name='" + structure.getInfo().name + "'>");
        FileUtils.write(fos, "<health>" + structure.getHealth() + "</health>");
        FileUtils.write(fos, "<progress>" + structure.getProgress() + "</progress>");
        FileUtils.write(fos, "</structure>");
    }

    private void writeResource(FileOutputStream fos, ResourceModel resource) throws IOException {
        FileUtils.write(fos, "<resource id='" + resource.getId() + "' name='" + resource.getInfo().name + "'>");
        FileUtils.write(fos, "<health>" + resource.getHealth() + "</health>");
        FileUtils.write(fos, "<progress>" + resource.getProgress() + "</progress>");
        FileUtils.write(fos, "<quantity>" + resource.getQuantity() + "</quantity>");
        FileUtils.write(fos, "</resource>");
    }

    private void writeConsumable(FileOutputStream fos, ConsumableModel consumable) throws IOException {
        FileUtils.write(fos, "<consumable id='" + consumable.getId() + "' name='" + consumable.getInfo().name + "'>");
        FileUtils.write(fos, "<quantity>" + consumable.getQuantity() + "</quantity>");
        FileUtils.write(fos, "</consumable>");
    }

    public void load(VTDNav vn) throws XPathParseException, NavException, XPathEvalException {
        AutoPilot ap = new AutoPilot(vn);
        ap.selectXPath("/save/parcels/*");

        AutoPilot apItem = new AutoPilot(vn);
        apItem.selectXPath("item|resource|structure|consumable");

        AutoPilot apElement = new AutoPilot(vn);
        apElement.selectXPath("*");

        WorldModule manager = Game.getWorldManager();

        while (ap.evalXPath() != -1) {
            vn.push();

            int x = vn.parseInt(vn.getAttrVal("x"));
            int y = vn.parseInt(vn.getAttrVal("y"));
            int z = vn.parseInt(vn.getAttrVal("z"));
            int type = vn.parseInt(vn.getAttrVal("type"));

            while (apItem.evalXPath() != -1) {
                switch (vn.toString(vn.getCurrentIndex())) {
                    case "item":
                        readItem(apElement, vn, manager, x, y, z);
                        break;
                    case "resource":
                        readResource(apElement, vn, manager, x, y, z);
                        break;
                    case "structure":
                        readStructure(apElement, vn, manager, x, y, z);
                        break;
                    case "consumable":
                        readConsumable(apElement, vn, manager, x, y, z);
                        break;
                }
            }
            apItem.resetXPath();
            vn.pop();
        }
    }

    private void readConsumable(AutoPilot apElement, VTDNav vn, WorldModule manager, int x, int y, int z) throws NavException, XPathEvalException {
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

    private void readStructure(AutoPilot apElement, VTDNav vn, WorldModule manager, int x, int y, int z) throws NavException, XPathEvalException {
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

    private void readResource(AutoPilot apElement, VTDNav vn, WorldModule manager, int x, int y, int z) throws NavException, XPathEvalException {
        String name = vn.toString(vn.getAttrVal("name"));
        int id = vn.parseInt(vn.getAttrVal("id"));
        int health = 0;
        int quantity = 10;
        int progress = 0;

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "health":
                    health = (int)vn.parseDouble(vn.getText());
                    break;

                case "progress":
                    progress = vn.parseInt(vn.getText());
                    break;

                case "quantity":
                    quantity = vn.parseInt(vn.getText());
                    break;
            }
        }
        apElement.resetXPath();

        ResourceModel resource = (ResourceModel)manager.putObject(name, x, y, z, quantity);
        if (resource != null) {
//            resource.setTile(tile);
//            resource.setValue(value);
            resource.setId(id);
        }
    }

    private void readItem(AutoPilot apElement, VTDNav vn, WorldModule manager, int x, int y, int z) throws NavException, XPathEvalException {
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
