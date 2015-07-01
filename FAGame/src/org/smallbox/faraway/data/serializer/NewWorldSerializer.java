package org.smallbox.faraway.data.serializer;

import com.ximpleware.*;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.WorldManager;
import org.smallbox.faraway.game.model.item.*;

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
//        WorldManager manager = Game.getWorldManager();
//
//        save.width = manager.getWidth();
//        save.height = manager.getHeight();
//
//        save.parcels = new ArrayList<>();
//        Map<Integer, List<ParcelModel>> types = new HashMap<>();
//        for (int t = 0; t < 10; t++) {
//            types.put(t, new ArrayList<>());
//        }
//        for (int z = 0; z < 1; z++) {
//            for (int x = 0; x < manager.getWidth(); x++) {
//                for (int y = 0; y < manager.getHeight(); y++) {
//                    ParcelModel parcel = manager.getParcel(z, x, y);
//                    saveParcel(save.parcels, parcel);
//                    types.get(parcel.getType()).add(parcel);
//                }
//            }
//        }
//
////		for (Map.Entry<Integer, List<ParcelModel>> entry: types.entrySet()) {
////			save.types.add()
////		}
    }

    public void load(VTDNav vn) throws XPathParseException, NavException, XPathEvalException {
        AutoPilot ap = new AutoPilot(vn);
        ap.selectXPath("/org.smallbox.faraway.data.serializer.GameSerializer_-GameSave/parcels/*");

        AutoPilot apItem = new AutoPilot(vn);
        apItem.selectXPath("item|resource|structure|consumable");

        AutoPilot apElement = new AutoPilot(vn);
        apElement.selectXPath("*");

        AutoPilot apPosition = new AutoPilot(vn);
        apPosition.selectXPath("x|y|z");


        WorldManager manager = Game.getWorldManager();

        while (ap.evalXPath() != -1) {
            int x = -1;
            int y = -1;
            int z = -1;

            vn.push();
            int j;
            while ((j = apPosition.evalXPath()) != -1) {
                switch (vn.toString(vn.getCurrentIndex())) {
                    case "x": x = vn.parseInt(vn.getText()); break;
                    case "y": y = vn.parseInt(vn.getText()); break;
                    case "z": z = vn.parseInt(vn.getText()); break;
                }
            }
            apPosition.resetXPath();

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
        String name = null;
        int id = 0;
        int quantity = 0;

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "name":
                    name = vn.toString(vn.getText());
                    break;

                case "quantity":
                    quantity = (int)vn.parseDouble(vn.getText());
                    break;

                case "id":
                    id = vn.parseInt(vn.getText());
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
        String name = null;
        int id = 0;
        int health = 0;
        int progress = 0;

        while (apElement.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "name":
                    name = vn.toString(vn.getText());
                    break;

                case "health":
                    health = (int)vn.parseDouble(vn.getText());
                    break;

                case "id":
                    id = vn.parseInt(vn.getText());
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
        String name = null;
        int id = 0;
        int health = 0;
        int progress = 0;

        while (apElement.evalXPath() != -1) {
//            System.out.println(vn.toString(vn.getCurrentIndex()) + " ==> " + vn.toString(vn.getText()));

            switch (vn.toString(vn.getCurrentIndex())) {
                case "name":
                    name = vn.toString(vn.getText());
                    break;

                case "health":
                    health = (int)vn.parseDouble(vn.getText());
                    break;

                case "id":
                    id = vn.parseInt(vn.getText());
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
        String name = null;
        int id = 0;
        int health = 0;
        int progress = 0;

        while (apElement.evalXPath() != -1) {
//            System.out.println(vn.toString(vn.getCurrentIndex()) + " ==> " + vn.toString(vn.getText()));

            switch (vn.toString(vn.getCurrentIndex())) {
                case "name":
                    name = vn.toString(vn.getText());
                    break;

                case "health":
                    health = (int)vn.parseDouble(vn.getText());
                    break;

                case "id":
                    id = vn.parseInt(vn.getText());
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
