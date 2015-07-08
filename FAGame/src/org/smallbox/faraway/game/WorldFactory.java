package org.smallbox.faraway.game;

import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.game.manager.BaseManager;
import org.smallbox.faraway.game.manager.WorldManager;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.planet.RegionInfo;
import org.smallbox.faraway.util.Log;

import java.util.Random;

/**
 * Created by Alex on 06/07/2015.
 */
public class WorldFactory {

    public void create(WorldManager worldManager) {
        int mapWidth = worldManager.getWidth();
        int mapHeight = worldManager.getHeight();

        // Initialize game map
        worldManager.getParcelList().forEach(parcel -> {
            parcel.setType(0);
            parcel.setResource(null);
        });
        ParcelModel[][][] parcels = worldManager.getParcels();

        // Add region terrains
        for (RegionInfo.RegionTerrain terrain: Game.getInstance().getRegion().getInfo().terrains) {
            if (terrain.pattern != null) {
                Log.notice("Create map with pattern: " + terrain.pattern);
                new MidpointDisplacement(MapGenConfig.get(terrain.pattern)).create(parcel ->
                        applyToParcel(terrain, parcel));
            } else {
                worldManager.getParcelList().forEach(parcel ->
                        applyToParcel(terrain, parcel));
            }
        }

        // Clean map
        cleanMap();

        // Notify world observers'
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                final ParcelModel parcel = parcels[x][y][0];
                if (parcel.getStructure() != null) {
                    Game.getInstance().notify(observer -> observer.onAddStructure(parcel.getStructure()));
                }
                if (parcel.getResource() != null) {
                    Game.getInstance().notify(observer -> observer.onAddResource(parcel.getResource()));
                    worldManager.getResources().add(parcel.getResource());
                }
                if (parcel.getItem() != null) {
                    Game.getInstance().notify(observer -> observer.onAddItem(parcel.getItem()));
                    worldManager.getItems().add(parcel.getItem());
                }
                if (parcel.getConsumable() != null) {
                    Game.getInstance().notify(observer -> observer.onAddConsumable(parcel.getConsumable()));
                    worldManager.getConsumables().add(parcel.getConsumable());
                }
            }
        }
    }

    private void cleanMap() {
        Game.getWorldManager().getParcelList().forEach(parcel -> {
            ParcelModel r = Game.getWorldManager().getParcel(parcel.getX() + 1, parcel.getY());
            ParcelModel l = Game.getWorldManager().getParcel(parcel.getX() - 1, parcel.getY());
            ParcelModel t = Game.getWorldManager().getParcel(parcel.getX(), parcel.getY() + 1);
            ParcelModel b = Game.getWorldManager().getParcel(parcel.getX(), parcel.getY() - 1);

            // Add resource on empty parcel surrounded by resources
            if (parcel.getResource() == null) {
                boolean isSurrounded = true;
                if (l != null && (l.getResource() == null || !l.getResource().isSolid())) isSurrounded = false;
                if (r != null && (r.getResource() == null || !r.getResource().isSolid())) isSurrounded = false;
                if (t != null && (t.getResource() == null || !t.getResource().isSolid())) isSurrounded = false;
                if (b != null && (b.getResource() == null || !b.getResource().isSolid())) isSurrounded = false;

                if (isSurrounded) {
                    ResourceModel resourceModel = null;
                    if (l != null) {
                        resourceModel = new ResourceModel(l.getResource().getInfo());
                        resourceModel.addQuantity(l.getResource().getQuantity());
                    }
                    if (r != null) {
                        resourceModel = new ResourceModel(r.getResource().getInfo());
                        resourceModel.addQuantity(r.getResource().getQuantity());
                    }
                    if (t != null) {
                        resourceModel = new ResourceModel(t.getResource().getInfo());
                        resourceModel.addQuantity(t.getResource().getQuantity());
                    }
                    if (b != null) {
                        resourceModel = new ResourceModel(b.getResource().getInfo());
                        resourceModel.addQuantity(b.getResource().getQuantity());
                    }
                    parcel.setResource(resourceModel);
                }
            }

            // Remove resource if neighbors parcels has no resources
            if (parcel.getResource() != null) {
                boolean isAlone = true;
                if (l != null && l.getResource() != null) isAlone = false;
                if (r != null && r.getResource() != null) isAlone = false;
                if (t != null && t.getResource() != null) isAlone = false;
                if (b != null && b.getResource() != null) isAlone = false;

                if (isAlone) {
                    parcel.setResource(null);
                }
            }
        });
    }

    private void applyToParcel(RegionInfo.RegionTerrain terrain, ParcelModel parcel) {
        if (terrain.condition == null
                || ("rock".equals(terrain.condition) && parcel.getResource() != null && parcel.getResource().isRock())
                || ("ground".equals(terrain.condition) && (parcel.getResource() == null || !parcel.getResource().isRock()))) {

            // Set type
            if (terrain.typeId != -1) {
                parcel.setType(terrain.typeId);
            }

            // Add resource
            if (terrain.resource != null) {
                ResourceModel resourceModel = new ResourceModel(GameData.getData().getItemInfo(terrain.resource));
                if (terrain.quantity != null && terrain.quantity[1] != terrain.quantity[0]) {
                    resourceModel.setValue(new Random().nextInt(terrain.quantity[1] - terrain.quantity[0]) + terrain.quantity[0]);
                } else if (terrain.quantity != null) {
                    resourceModel.setValue(terrain.quantity[0]);
                } else {
                    resourceModel.setValue(10);
                }
                parcel.setResource(resourceModel);
            }
        }
    }
}
