package org.smallbox.faraway.data.factory.world;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.planet.RegionInfo;
import org.smallbox.faraway.game.module.world.WorldModule;
import org.smallbox.faraway.util.Log;

import java.util.*;

/**
 * Created by Alex on 06/07/2015.
 */
public class WorldFactory {

    public void create(WorldModule worldModule, RegionInfo regionInfo) {
        int mapWidth = Game.getInstance().getInfo().worldWidth;
        int mapHeight = Game.getInstance().getInfo().worldHeight;

//        // Initialize game old
//        worldModule.getParcelList().forEach(parcel -> {
//            parcel.setType(0);
//            parcel.setResource(null);
//        });
        ParcelModel[][][] parcels = worldModule.getParcels();

        // Add region terrains
        for (RegionInfo.RegionTerrain terrain: regionInfo.terrains) {
            if ("random_light".equals(terrain.pattern) || "random_large".equals(terrain.pattern)) {
                Log.notice("Create old with random pattern: " + terrain.pattern);
                worldModule.getParcelList().stream()
                        .filter(parcel -> Math.random() < ("random_light".equals(terrain.pattern) ? 0.05f : 0.1f))
                        .forEach(parcel -> applyToParcel(worldModule, terrain, parcel));
            }
            else if (terrain.pattern != null) {
                Log.notice("Create old with pattern: " + terrain.pattern);
                new MidpointDisplacement(WorldFactoryConfig.get(terrain.pattern)).create(parcel ->
                        applyToParcel(worldModule, terrain, parcel));
            } else {
                worldModule.getParcelList().forEach(parcel ->
                        applyToParcel(worldModule, terrain, parcel));
            }
        }

        // Clean old
        cleanMap(worldModule);

        // Notify world observers'
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                final ParcelModel parcel = parcels[x][y][0];
                if (parcel.getStructure() != null) {
                    Game.getInstance().notify(observer -> observer.onAddStructure(parcel.getStructure()));
                }
                if (parcel.getResource() != null) {
                    Game.getInstance().notify(observer -> observer.onAddResource(parcel.getResource()));
                    worldModule.getResources().add(parcel.getResource());
                }
                if (parcel.getItem() != null) {
                    Game.getInstance().notify(observer -> observer.onAddItem(parcel.getItem()));
                    worldModule.getItems().add(parcel.getItem());
                }
                if (parcel.getConsumable() != null) {
                    Game.getInstance().notify(observer -> observer.onAddConsumable(parcel.getConsumable()));
                    worldModule.getConsumables().add(parcel.getConsumable());
                }
            }
        }
    }

    private void cleanMap(WorldModule worldModule) {
        Game.getWorldManager().getParcelList().forEach(parcel -> {
            ParcelModel r = Game.getWorldManager().getParcel(parcel.x + 1, parcel.y);
            ParcelModel l = Game.getWorldManager().getParcel(parcel.x - 1, parcel.y);
            ParcelModel t = Game.getWorldManager().getParcel(parcel.x, parcel.y + 1);
            ParcelModel b = Game.getWorldManager().getParcel(parcel.x, parcel.y - 1);

            // Add resource on empty parcel surrounded by resources
            if (parcel.getResource() == null) {
                boolean isSurrounded = true;
                if (l != null && (l.getResource() == null || !l.getResource().isSolid())) isSurrounded = false;
                if (r != null && (r.getResource() == null || !r.getResource().isSolid())) isSurrounded = false;
                if (t != null && (t.getResource() == null || !t.getResource().isSolid())) isSurrounded = false;
                if (b != null && (b.getResource() == null || !b.getResource().isSolid())) isSurrounded = false;

                if (isSurrounded) {
                    ResourceModel resource = null;
                    if (l != null) {
                        resource = new ResourceModel(l.getResource().getInfo());
                        resource.addQuantity(l.getResource().getQuantity());
                    }
                    if (r != null) {
                        resource = new ResourceModel(r.getResource().getInfo());
                        resource.addQuantity(r.getResource().getQuantity());
                    }
                    if (t != null) {
                        resource = new ResourceModel(t.getResource().getInfo());
                        resource.addQuantity(t.getResource().getQuantity());
                    }
                    if (b != null) {
                        resource = new ResourceModel(b.getResource().getInfo());
                        resource.addQuantity(b.getResource().getQuantity());
                    }
                    if (resource != null) {
                        parcel.setResource(resource);
                    }
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
                    worldModule.getParcelContent(parcel).resource = null;
                }
            }
        });
    }

    private void applyToParcel(WorldModule worldModule, RegionInfo.RegionTerrain terrain, ParcelModel parcel) {
        if (terrain.condition == null
                || ("rock".equals(terrain.condition) && parcel.getResource() != null && parcel.getResource().isRock())
                || ("ground".equals(terrain.condition) && (parcel.getResource() == null || !parcel.getResource().isRock()))) {

            // Set type
            if (terrain.typeId != -1) {
                parcel.setType(terrain.typeId);
            }

            // Add resource
            if (terrain.resource != null) {
                ResourceModel resource = new ResourceModel(GameData.getData().getItemInfo(terrain.resource));
                if (terrain.quantity != null && terrain.quantity[1] != terrain.quantity[0]) {
                    resource.setValue(new Random().nextInt(terrain.quantity[1] - terrain.quantity[0]) + terrain.quantity[0]);
                } else if (terrain.quantity != null) {
                    resource.setValue(terrain.quantity[0]);
                } else {
                    resource.setValue(10);
                }
                worldModule.getParcelContent(parcel).resource = resource;
            }
        }
    }

    public Queue<ParcelModel> getFreeParcels(ParcelModel startParcel) {
        List<ParcelModel> freeParcels = new ArrayList<>();
        for (int x = startParcel.x - 5; x < startParcel.x + 5; x++) {
            for (int y = startParcel.y - 5; y < startParcel.y + 5; y++) {
                ParcelModel parcel = WorldHelper.getParcel(x, y);
                if (parcel != null && parcel.getResource() == null) {
                    freeParcels.add(parcel);
                }
            }
        }
        Collections.shuffle(freeParcels);

        Queue<ParcelModel> queue = new LinkedList<>(freeParcels);
        return queue;
    }

    public void createLandSite(Game game) {
        // Get free parcels
        ParcelModel startParcel;
        Queue<ParcelModel> freeParcels;
        do {
            startParcel = WorldHelper.getRandomFreeSpace(false, true);
            freeParcels = getFreeParcels(startParcel);
        } while (freeParcels.size() < 15);

        // Put characters
        Game.getCharacterManager().addRandom(freeParcels.poll());
        Game.getCharacterManager().addRandom(freeParcels.poll());
        Game.getCharacterManager().addRandom(freeParcels.poll());

        // Put resources
        Game.getWorldManager().putObject("base.wood", freeParcels.poll(), 50);
        Game.getWorldManager().putObject("base.wood", freeParcels.poll(), 50);
        Game.getWorldManager().putObject("base.wood", freeParcels.poll(), 50);

        Game.getWorldManager().putObject("base.military_meal", freeParcels.poll(), 25);
        Game.getWorldManager().putObject("base.military_meal", freeParcels.poll(), 25);
        Game.getWorldManager().putObject("base.military_meal", freeParcels.poll(), 25);

        Game.getWorldManager().putObject("base.iron", freeParcels.poll(), 25);
        Game.getWorldManager().putObject("base.iron", freeParcels.poll(), 25);

        game.getViewport().moveTo(startParcel.x, startParcel.y);
    }

}
