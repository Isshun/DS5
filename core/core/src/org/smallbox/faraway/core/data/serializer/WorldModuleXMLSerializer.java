//package org.smallbox.faraway.core.data.serializer;
//
//import com.ximpleware.*;
//import org.smallbox.faraway.core.data.ItemInfo;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.GameInfo;
//import org.smallbox.faraway.core.game.helper.WorldHelper;
//import org.smallbox.faraway.core.game.model.Data;
//import org.smallbox.faraway.core.game.module.world.WorldModule;
//import org.smallbox.faraway.core.game.module.world.model.BuildableMapObject;
//import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
//import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
//import org.smallbox.faraway.core.game.module.world.model.StructureModel;
//import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
//import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
//import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
//import org.smallbox.faraway.core.util.FileUtils;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Alex on 14/11/2015.
// */
//public class WorldModuleXMLSerializer implements SerializerInterface {
//
//    @Override
//    public void save(FileOutputStream fos) throws IOException {
//        FileUtils.write(fos, "<parcels>");
//        int width = Game.getInstance().getInfo().worldWidth;
//        int height = Game.getInstance().getInfo().worldHeight;
//        int floors = Game.getInstance().getInfo().worldFloors;
//        ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
//
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                for (int z = 0; z < floors; z++) {
//                    writeParcel(fos, parcels[x][y][z]);
//                }
//            }
//        }
//        FileUtils.write(fos, "</parcels>");
//    }
//
//    private void writeParcel(FileOutputStream fos, ParcelModel parcel) throws IOException {
//        if (parcel.getItem() != null || parcel.hasPlant() || parcel.hasStructure() || parcel.hasConsumable() || parcel.hasRock()) {
//            FileUtils.write(fos, "<parcel x='" + parcel.x + "' y='" + parcel.y + "' z='" + parcel.z + "'>");
//
//            if (parcel.getStructure() != null) {
//                writeStructure(fos, parcel.getStructure());
//            }
//
//            if (parcel.hasPlant()) {
//                writePlant(fos, parcel.getPlant());
//            }
//
//            if (parcel.hasRock()) {
//                writeRock(fos, parcel.getRockInfo());
//            }
//
//            if (parcel.getItem() != null && parcel.getItem().getParcel() == parcel) {
//                writeItem(fos, parcel.getItem());
//            }
//
//            if (parcel.getConsumable() != null) {
//                writeConsumable(fos, parcel.getConsumable());
//            }
//
//            FileUtils.write(fos, "</parcel>");
//        } else {
//            FileUtils.write(fos, "<parcel x='" + parcel.x + "' y='" + parcel.y + "' z='" + parcel.z + "'/>");
//        }
//    }
//
//    private void writeItem(FileOutputStream fos, ItemModel item) throws IOException {
//        FileUtils.write(fos, "<item id='" + item.getId() + "' name='" + item.getInfo().name + "'>");
//        FileUtils.write(fos, "<health>" + item.getHealth() + "</health>");
//        FileUtils.write(fos, "<progress>" + item.getProgress() + "</progress>");
//        if (!item.isComplete()) {
//            writeBuildingInfo(fos, item);
//        }
//        FileUtils.write(fos, "</item>");
//    }
//
//    private void writeBuildingInfo(FileOutputStream fos, BuildableMapObject object) throws IOException {
//        FileUtils.write(fos, "<building currentBuilding='" + object.getCurrentBuild() + "'>");
//        FileUtils.write(fos, "<components>");
//        for (BuildableMapObject.ComponentModel component: object.getComponents()) {
//            FileUtils.write(fos, "<component currentQuantity = '" + component.currentQuantity + "' neededQuantity = '" + component.neededQuantity + "'>" + component.info.name + "</component>");
//        }
//        FileUtils.write(fos, "</components>");
//        FileUtils.write(fos, "</building>");
//    }
//
//    private void writeStructure(FileOutputStream fos, StructureModel structure) throws IOException {
//        FileUtils.write(fos, "<structure id='" + structure.getId() + "' name='" + structure.getInfo().name + "'>");
//        FileUtils.write(fos, "<health>" + structure.getHealth() + "</health>");
//        FileUtils.write(fos, "<progress>" + structure.getProgress() + "</progress>");
//        if (!structure.isComplete()) {
//            writeBuildingInfo(fos, structure);
//        }
//        FileUtils.write(fos, "</structure>");
//    }
//
//    private void writePlant(FileOutputStream fos, PlantModel resource) throws IOException {
//        FileUtils.write(fos, "<plant id='" + resource.getId() + "' name='" + resource.getInfo().name + "'>");
//        FileUtils.write(fos, "<health>" + resource.getHealth() + "</health>");
//        FileUtils.write(fos, "<progress>" + resource.getProgress() + "</progress>");
//        FileUtils.write(fos, "<maturity>" + resource.getMaturity() + "</maturity>");
//        FileUtils.write(fos, "</plant>");
//    }
//
//    private void writeRock(FileOutputStream fos, ItemInfo rockInfo) throws IOException {
//        FileUtils.write(fos, "<rock id='" + 0 + "' name='" + rockInfo.name + "'>");
//        FileUtils.write(fos, "</rock>");
//    }
//
//    private void writeConsumable(FileOutputStream fos, ConsumableModel consumable) throws IOException {
//        FileUtils.write(fos, "<consumable id='" + consumable.getId() + "' name='" + consumable.getInfo().name + "'>");
//        FileUtils.write(fos, "<quantity>" + consumable.getQuantity() + "</quantity>");
//        FileUtils.write(fos, "</consumable>");
//    }
//
//    public void load(GameInfo gameInfo, VTDNav vn, GameSaveManager.GameSerializerInterface listener) throws XPathParseException, NavException, XPathEvalException {
//        ParcelModel[][][] parcels = new ParcelModel[gameInfo.worldWidth][gameInfo.worldHeight][gameInfo.worldFloors];
//        List<ParcelModel> parcelsList = new ArrayList<>();
//        int width = gameInfo.worldWidth;
//        int height = gameInfo.worldHeight;
//
//        AutoPilot ap = new AutoPilot(vn);
//        ap.selectXPath("/save/parcels/*");
//
//        AutoPilot apItem = new AutoPilot(vn);
//        apItem.selectXPath("item|resource|structure|consumable");
//
//        AutoPilot apElement = new AutoPilot(vn);
//        apElement.selectXPath("*");
//
//        while (ap.evalXPath() != -1) {
//            vn.push();
//
//            int x = vn.parseInt(vn.getAttrVal("x"));
//            int y = vn.parseInt(vn.getAttrVal("y"));
//            int z = vn.parseInt(vn.getAttrVal("z"));
//            int type = vn.parseInt(vn.getAttrVal("type"));
//
//            ParcelModel parcel = new ParcelModel(x + (y * width) + (z * width * height), x, y, z);
//            parcelsList.add(parcel);
//            parcels[x][y][z] = parcel;
//
//            while (apItem.evalXPath() != -1) {
//                switch (vn.toString(vn.getCurrentIndex())) {
//                    case "item":
//                        readItem(apElement, vn, parcel, x, y, z);
//                        break;
//                    case "plant":
//                        readPlant(apElement, vn, parcel, x, y, z);
//                        break;
//                    case "rock":
//                        readRock(apElement, vn, parcel, x, y, z);
//                        break;
//                    case "structure":
//                        readStructure(apElement, vn, parcel, x, y, z);
//                        break;
//                    case "consumable":
//                        readConsumable(apElement, vn, parcel, x, y, z);
//                        break;
//                }
//            }
//            apItem.resetXPath();
//            vn.pop();
//        }
//
//        WorldHelper.init(parcels, gameInfo.worldFloors - 1);
//        ModuleHelper.getWorldModule().setParcels(parcels, parcelsList);
//
//        listener.onSerializerComplete();
//    }
//
//    private void readConsumable(AutoPilot apElement, VTDNav vn, ParcelModel parcel, int x, int y, int z) throws NavException, XPathEvalException {
//        String name = vn.toString(vn.getAttrVal("name"));
//        int id = vn.parseInt(vn.getAttrVal("id"));
//        int quantity = 0;
//
//        while (apElement.evalXPath() != -1) {
//            switch (vn.toString(vn.getCurrentIndex())) {
//                case "quantity":
//                    quantity = (int)vn.parseDouble(vn.getText());
//                    break;
//            }
//        }
//        apElement.resetXPath();
//
//        ConsumableModel consumable = new ConsumableModel(Data.getData().getItemInfo(name));
//        consumable.setId(id);
//        consumable.setQuantity(quantity);
//        consumable.setParcel(parcel);
//        parcel.setConsumable(consumable);
//    }
//
//    private void readStructure(AutoPilot apElement, VTDNav vn, ParcelModel parcel, int x, int y, int z) throws NavException, XPathEvalException, XPathParseException {
//        String name = vn.toString(vn.getAttrVal("name"));
//        int id = vn.parseInt(vn.getAttrVal("id"));
//        int health = 0;
//        int progress = 0;
//        int currentBuild = 0;
//        boolean complete = true;
//        List<BuildableMapObject.ComponentModel> components = null;
//
//        while (apElement.evalXPath() != -1) {
//            switch (vn.toString(vn.getCurrentIndex())) {
//                case "health":
//                    health = (int)vn.parseDouble(vn.getText());
//                    break;
//                case "progress":
//                    progress = vn.parseInt(vn.getText());
//                    break;
//                case "building":
//                    complete = false;
//                    currentBuild = vn.parseInt(vn.getAttrVal("currentBuilding"));
//                    components = readBuilding(vn);
//                    break;
//            }
//        }
//        apElement.resetXPath();
//
//        StructureModel structure = new StructureModel(Data.getData().getItemInfo(name));
//        structure.setId(id);
//        structure.setHealth(health);
//        structure.setComplete(complete);
//        structure.setBuild(currentBuild, 10);
//        structure.setParcel(parcel);
//        parcel.setStructure(structure);
//        if (components != null) {
//            structure.setComponents(components);
//        }
//    }
//
//    private void readPlant(AutoPilot apElement, VTDNav vn, ParcelModel parcel, int x, int y, int z) throws NavException, XPathEvalException {
//        String name = vn.toString(vn.getAttrVal("name"));
//        int id = vn.parseInt(vn.getAttrVal("id"));
//        int health = 0;
//        int quantity = 0;
//        double maturity = 0;
//        int progress = 0;
//
//        while (apElement.evalXPath() != -1) {
//            switch (vn.toString(vn.getCurrentIndex())) {
//                case "health":
//                    health = (int)vn.parseDouble(vn.getText());
//                    break;
//
//                case "progress":
//                    progress = vn.parseInt(vn.getText());
//                    break;
//
//                case "quantity":
//                    quantity = vn.parseInt(vn.getText());
////                    quantity = 1;
//                    break;
//
//                case "maturity":
//                    maturity = vn.parseDouble(vn.getText());
////                    maturity = 1;
//                    break;
//            }
//        }
//        apElement.resetXPath();
//
//        PlantModel plant = new PlantModel(Data.getData().getItemInfo(name));
//        plant.setId(id);
//        plant.setMaturity(maturity);
//        plant.setParcel(parcel);
//        parcel.setPlant(plant);
//    }
//
//    private void readRock(AutoPilot apElement, VTDNav vn, ParcelModel parcel, int x, int y, int z) throws NavException, XPathEvalException {
//        String name = vn.toString(vn.getAttrVal("name"));
//        int id = vn.parseInt(vn.getAttrVal("id"));
//        int health = 0;
//        int quantity = 0;
//        double maturity = 0;
//        int progress = 0;
//
//        while (apElement.evalXPath() != -1) {
//            switch (vn.toString(vn.getCurrentIndex())) {
//                case "health":
//                    health = (int)vn.parseDouble(vn.getText());
//                    break;
//
//                case "progress":
//                    progress = vn.parseInt(vn.getText());
//                    break;
//
//                case "quantity":
//                    quantity = vn.parseInt(vn.getText());
////                    quantity = 1;
//                    break;
//
//                case "maturity":
//                    maturity = vn.parseDouble(vn.getText());
////                    maturity = 1;
//                    break;
//            }
//        }
//        apElement.resetXPath();
//
//        parcel.setRockInfo(Data.getData().getItemInfo(name));
//    }
//
//    private void readItem(AutoPilot apElement, VTDNav vn, ParcelModel parcel, int x, int y, int z) throws NavException, XPathEvalException, XPathParseException {
//        String name = vn.toString(vn.getAttrVal("name"));
//        int id = vn.parseInt(vn.getAttrVal("id"));
//        int health = 0;
//        int progress = 0;
//        int currentBuild = 0;
//        boolean complete = true;
//        List<BuildableMapObject.ComponentModel> components = null;
//
//        vn.push();
//        while (apElement.evalXPath() != -1) {
//            switch (vn.toString(vn.getCurrentIndex())) {
//                case "health":
//                    health = (int)vn.parseDouble(vn.getText());
//                    break;
//                case "progress":
//                    progress = vn.parseInt(vn.getText());
//                    break;
//                case "building":
//                    complete = false;
//                    currentBuild = vn.parseInt(vn.getAttrVal("currentBuilding"));
//                    components = readBuilding(vn);
//                    break;
//            }
//        }
//        apElement.resetXPath();
//        vn.pop();
//
//        ItemModel item = new ItemModel(Data.getData().getItemInfo(name), parcel);
//        item.setId(id);
//        item.setHealth(health);
//        item.setComplete(complete);
//        item.setBuild(currentBuild, 10);
//        item.setParcel(parcel);
//        parcel.setItem(item);
//        if (components != null) {
//            item.setComponents(components);
//        }
//    }
//
//    private List<BuildableMapObject.ComponentModel> readBuilding(VTDNav vn) throws NavException, XPathEvalException, XPathParseException {
//        List<BuildableMapObject.ComponentModel> components = null;
//        vn.push();
//
//        AutoPilot apElement = new AutoPilot(vn);
//        apElement.selectXPath("*");
//
//        while (apElement.evalXPath() != -1) {
//            switch (vn.toString(vn.getCurrentIndex())) {
//                case "components":
//                    components = readBuildingComponents(vn);
//                    break;
//            }
//        }
////        apElement.resetXPath();
//        vn.pop();
//        return components;
//    }
//
//    private List<BuildableMapObject.ComponentModel> readBuildingComponents(VTDNav vn) throws NavException, XPathEvalException, XPathParseException {
//        List<BuildableMapObject.ComponentModel> components = new ArrayList<>();
//
//        vn.push();
//
//        AutoPilot apElement = new AutoPilot(vn);
//        apElement.selectXPath("*");
//
//        while (apElement.evalXPath() != -1) {
//            switch (vn.toString(vn.getCurrentIndex())) {
//                case "component":
//                    components.add(new BuildableMapObject.ComponentModel(
//                            Data.getData().getItemInfo(vn.toString(vn.getText())),
//                            vn.parseInt(vn.getAttrVal("neededQuantity")),
//                            vn.parseInt(vn.getAttrVal("currentQuantity"))));
//                    break;
//            }
//        }
//
//        vn.pop();
//        return components;
//    }
//
//}
