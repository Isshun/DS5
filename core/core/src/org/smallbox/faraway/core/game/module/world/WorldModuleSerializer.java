package org.smallbox.faraway.core.game.module.world;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.ximpleware.*;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldModuleSerializer implements SerializerInterface {

    @Override
    public void save(FileOutputStream fos) {
        SQLHelper.getInstance().post(db -> {
            int width = Game.getInstance().getInfo().worldWidth;
            int height = Game.getInstance().getInfo().worldHeight;
            int floors = Game.getInstance().getInfo().worldFloors;
            ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();

            try {
                db.exec("CREATE TABLE WorldModule (x INTEGER, y INTEGER, z INTEGER, ground TEXT, rock TEXT, plant TEXT, item TEXT, structure TEXT)");
                SQLiteStatement st = db.prepare("INSERT INTO WorldModule (x, y, z, ground, rock, plant, item, structure) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            for (int z = 0; z < floors; z++) {
                                ParcelModel parcel = parcels[x][y][z];
                                st.bind(1, x);
                                st.bind(2, y);
                                st.bind(3, z);
                                st.bind(4, 1);

                                // Rock
                                st.bind(5, parcel.hasRock() ? parcel.getRockInfo().name : null);

                                // Plant
                                st.bind(6, parcel.hasPlant() ? parcel.getPlant().getInfo().name : null);

                                // Item
                                st.bind(7, parcel.hasItem() ? parcel.getItem().getInfo().name : null);

                                // Structure
                                st.bind(8, parcel.hasStructure() ? parcel.getStructure().getInfo().name : null);

                                st.step();
                                st.reset(false);
                            }
                        }
                    }
                    db.exec("end transaction");
                } finally {
                    st.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
//        FileUtils.write(fos, "<parcels>");
//        for (ParcelModel parcel: ModuleHelper.getWorldModule().getParcelList()) {
//            if (parcel.z == 0) {
//                writeParcel(fos, parcel);
//            }
//        }
//        FileUtils.write(fos, "</parcels>");
    }

    private void writeParcel(FileOutputStream fos, ParcelModel parcel) throws IOException {
        if (parcel.getItem() != null || parcel.hasPlant() || parcel.getStructure() != null || parcel.getConsumable() != null) {
            FileUtils.write(fos, "<parcel x='" + parcel.x + "' y='" + parcel.y + "' z='" + parcel.z + "' type='" + parcel.getType() + "'>");

            if (parcel.getStructure() != null) {
                writeStructure(fos, parcel.getStructure());
            }

            if (parcel.hasPlant()) {
                writePlant(fos, parcel.getPlant());
            }

            if (parcel.getItem() != null && parcel.getItem().getParcel() == parcel) {
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
        if (!item.isComplete()) {
            writeBuildingInfo(fos, item);
        }
        FileUtils.write(fos, "</item>");
    }

    private void writeBuildingInfo(FileOutputStream fos, BuildableMapObject object) throws IOException {
        FileUtils.write(fos, "<building currentBuilding='" + object.getCurrentBuild() + "'>");
        FileUtils.write(fos, "<components>");
        for (BuildableMapObject.ComponentModel component: object.getComponents()) {
            FileUtils.write(fos, "<component currentQuantity = '" + component.currentQuantity + "' neededQuantity = '" + component.neededQuantity + "'>" + component.info.name + "</component>");
        }
        FileUtils.write(fos, "</components>");
        FileUtils.write(fos, "</building>");
    }

    private void writeStructure(FileOutputStream fos, StructureModel structure) throws IOException {
        FileUtils.write(fos, "<structure id='" + structure.getId() + "' name='" + structure.getInfo().name + "'>");
        FileUtils.write(fos, "<health>" + structure.getHealth() + "</health>");
        FileUtils.write(fos, "<progress>" + structure.getProgress() + "</progress>");
        if (!structure.isComplete()) {
            writeBuildingInfo(fos, structure);
        }
        FileUtils.write(fos, "</structure>");
    }

    private void writePlant(FileOutputStream fos, PlantModel resource) throws IOException {
        FileUtils.write(fos, "<resource id='" + resource.getId() + "' name='" + resource.getInfo().name + "'>");
        FileUtils.write(fos, "<health>" + resource.getHealth() + "</health>");
        FileUtils.write(fos, "<progress>" + resource.getProgress() + "</progress>");
        FileUtils.write(fos, "<maturity>" + resource.getMaturity() + "</maturity>");
        FileUtils.write(fos, "</resource>");
    }

    private void writeConsumable(FileOutputStream fos, ConsumableModel consumable) throws IOException {
        FileUtils.write(fos, "<consumable id='" + consumable.getId() + "' name='" + consumable.getInfo().name + "'>");
        FileUtils.write(fos, "<quantity>" + consumable.getQuantity() + "</quantity>");
        FileUtils.write(fos, "</consumable>");
    }

    public void load(GameInfo gameInfo, VTDNav vn) throws XPathParseException, NavException, XPathEvalException {
        SQLHelper.getInstance().post(db -> {
            WeatherModule weatherModule = (WeatherModule) ModuleManager.getInstance().getModule(WeatherModule.class);
            ParcelModel[][][] parcels = new ParcelModel[gameInfo.worldWidth][gameInfo.worldHeight][gameInfo.worldFloors];
            List<ParcelModel> parcelsList = new ArrayList<>();

            try {
                SQLiteStatement st = db.prepare("SELECT x, y, z, ground, rock, plant, item, structure FROM WorldModule");
                try {
                    while (st.step()) {
                        int x = st.columnInt(0);
                        int y = st.columnInt(1);
                        int z = st.columnInt(2);

                        ParcelModel parcel = new ParcelModel(x * y * z, weatherModule, x, y, z);
                        parcelsList.add(parcel);
                        parcels[x][y][z] = parcel;

                        // Rock
                        if (!st.columnNull(4)) {
                            parcel.setRockInfo(Data.getData().getItemInfo(st.columnString(4)));
                        }

                        // Plant
                        if (!st.columnNull(5)) {
                            parcel.setPlant(new PlantModel(Data.getData().getItemInfo(st.columnString(5))));
                        }

                        // Item
                        if (!st.columnNull(6)) {
                            parcel.setItem(new ItemModel(Data.getData().getItemInfo(st.columnString(6)), parcel));
                        }

                        // Structure
                        if (!st.columnNull(7)) {
                            parcel.setStructure(new StructureModel(Data.getData().getItemInfo(st.columnString(7))));
                        }
                    }
                } finally {
                    st.dispose();
                }

                WorldHelper.init(parcels, gameInfo.worldFloors - 1);
                ModuleHelper.getWorldModule().setParcels(parcels, parcelsList);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
//        AutoPilot ap = new AutoPilot(vn);
//        ap.selectXPath("/save/parcels/*");
//
//        AutoPilot apItem = new AutoPilot(vn);
//        apItem.selectXPath("item|resource|structure|consumable");
//
//        AutoPilot apElement = new AutoPilot(vn);
//        apElement.selectXPath("*");
//
//        WorldModule manager = ModuleHelper.getWorldModule();
//
//        while (ap.evalXPath() != -1) {
//            vn.push();
//
//            int x = vn.parseInt(vn.getAttrVal("x"));
//            int y = vn.parseInt(vn.getAttrVal("y"));
//            int z = vn.parseInt(vn.getAttrVal("z"));
//            int type = vn.parseInt(vn.getAttrVal("type"));
//
//            while (apItem.evalXPath() != -1) {
//                switch (vn.toString(vn.getCurrentIndex())) {
//                    case "item":
//                        readItem(apElement, vn, manager, x, y, z);
//                        break;
//                    case "resource":
//                        readResource(apElement, vn, manager, x, y, z);
//                        break;
//                    case "structure":
//                        readStructure(apElement, vn, manager, x, y, z);
//                        break;
//                    case "consumable":
//                        readConsumable(apElement, vn, manager, x, y, z);
//                        break;
//                }
//            }
//            apItem.resetXPath();
//            vn.pop();
//        }
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

        ConsumableModel consumable = (ConsumableModel)manager.putObject(name, x, y, z, quantity, true);
        if (consumable != null) {
            consumable.setId(id);
            consumable.setQuantity(quantity);
        }
    }

    private void readStructure(AutoPilot apElement, VTDNav vn, WorldModule manager, int x, int y, int z) throws NavException, XPathEvalException, XPathParseException {
        String name = vn.toString(vn.getAttrVal("name"));
        int id = vn.parseInt(vn.getAttrVal("id"));
        int health = 0;
        int progress = 0;
        int currentBuild = 0;
        boolean complete = true;
        List<BuildableMapObject.ComponentModel> components = null;

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "health":
                    health = (int)vn.parseDouble(vn.getText());
                    break;
                case "progress":
                    progress = vn.parseInt(vn.getText());
                    break;
                case "building":
                    complete = false;
                    currentBuild = vn.parseInt(vn.getAttrVal("currentBuilding"));
                    components = readBuilding(vn);
                    break;
            }
        }
        apElement.resetXPath();

        StructureModel structure = (StructureModel)manager.putObject(name, x, y, z, progress, complete);
        if (structure != null) {
            structure.setId(id);
            structure.setHealth(health);
            structure.setComplete(complete);
            structure.setBuild(currentBuild, 10);
            if (components != null) {
                structure.setComponents(components);
            }
        }
    }

    private void readResource(AutoPilot apElement, VTDNav vn, WorldModule manager, int x, int y, int z) throws NavException, XPathEvalException {
        String name = vn.toString(vn.getAttrVal("name"));
        int id = vn.parseInt(vn.getAttrVal("id"));
        int health = 0;
        int quantity = 0;
        double maturity = 0;
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
//                    quantity = 1;
                    break;

                case "maturity":
                    maturity = vn.parseDouble(vn.getText());
//                    maturity = 1;
                    break;
            }
        }
        apElement.resetXPath();

        PlantModel resource = (PlantModel)manager.putObject(name, x, y, z, quantity, true);
        if (resource != null) {
            resource.setId(id);
            resource.setMaturity(maturity);
        }
    }

    private void readItem(AutoPilot apElement, VTDNav vn, WorldModule manager, int x, int y, int z) throws NavException, XPathEvalException, XPathParseException {
        String name = vn.toString(vn.getAttrVal("name"));
        int id = vn.parseInt(vn.getAttrVal("id"));
        int health = 0;
        int progress = 0;
        int currentBuild = 0;
        boolean complete = true;
        List<BuildableMapObject.ComponentModel> components = null;

        vn.push();
        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "health":
                    health = (int)vn.parseDouble(vn.getText());
                    break;
                case "progress":
                    progress = vn.parseInt(vn.getText());
                    break;
                case "building":
                    complete = false;
                    currentBuild = vn.parseInt(vn.getAttrVal("currentBuilding"));
                    components = readBuilding(vn);
                    break;
            }
        }
        apElement.resetXPath();
        vn.pop();

        ItemModel item = (ItemModel)manager.putObject(name, x, y, z, progress, complete);
        if (item != null) {
            item.setId(id);
            item.setHealth(health);
            item.setComplete(complete);
            item.setBuild(currentBuild, 10);
            if (components != null) {
                item.setComponents(components);
            }
        }
    }

    private List<BuildableMapObject.ComponentModel> readBuilding(VTDNav vn) throws NavException, XPathEvalException, XPathParseException {
        List<BuildableMapObject.ComponentModel> components = null;
        vn.push();

        AutoPilot apElement = new AutoPilot(vn);
        apElement.selectXPath("*");

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "components":
                    components = readBuildingComponents(vn);
                    break;
            }
        }
//        apElement.resetXPath();
        vn.pop();
        return components;
    }

    private List<BuildableMapObject.ComponentModel> readBuildingComponents(VTDNav vn) throws NavException, XPathEvalException, XPathParseException {
        List<BuildableMapObject.ComponentModel> components = new ArrayList<>();

        vn.push();

        AutoPilot apElement = new AutoPilot(vn);
        apElement.selectXPath("*");

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "component":
                    components.add(new BuildableMapObject.ComponentModel(
                            Data.getData().getItemInfo(vn.toString(vn.getText())),
                            vn.parseInt(vn.getAttrVal("neededQuantity")),
                            vn.parseInt(vn.getAttrVal("currentQuantity"))));
                    break;
            }
        }

        vn.pop();
        return components;
    }

}
