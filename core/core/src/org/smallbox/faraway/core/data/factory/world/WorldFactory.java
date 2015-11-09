package org.smallbox.faraway.core.data.factory.world;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.Utils;

import java.util.*;

/**
 * Created by Alex on 06/07/2015.
 */
public class WorldFactory {
    public void create(Game game, WorldModule worldModule, RegionInfo regionInfo) {
        ParcelModel[][][] parcels = worldModule.getParcels();

        for (int z = 0; z < game.getInfo().worldFloors - 1; z++) {
            for (int y = 0; y < game.getInfo().worldHeight; y++) {
                for (int x = 0; x < game.getInfo().worldWidth; x++) {
                    ResourceModel resource = new ResourceModel(Data.getData().getItemInfo("base.granite"));
                    if (resource.isRock()) {
                        resource.getRock().setQuantity(10);
                    }
                    parcels[x][y][z].setResource(resource);
                    resource.setParcel(parcels[x][y][z]);
                }
            }
        }

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
                new MidpointDisplacement(WorldFactoryConfig.get(terrain.pattern)).create(game.getInfo(), parcels, parcel ->
                        applyToParcel(worldModule, terrain, parcel));
            }
            else {
                worldModule.getParcelList().forEach(parcel ->
                        applyToParcel(worldModule, terrain, parcel));
            }
        }

        // Clean old
        cleanMap(worldModule);

        // Notify world observers'
        for (int z = 0; z < game.getInfo().worldFloors; z++) {
            for (int y = 0; y < game.getInfo().worldHeight; y++) {
                for (int x = 0; x < game.getInfo().worldWidth; x++) {
                    final ParcelModel parcel = parcels[x][y][z];
                    if (parcel.getStructure() != null) {
                        Application.getInstance().notify(observer -> observer.onAddStructure(parcel.getStructure()));
                        worldModule.getStructures().add(parcel.getStructure());
                    }
                    if (parcel.getResource() != null) {
                        Application.getInstance().notify(observer -> observer.onAddResource(parcel.getResource()));
                        worldModule.getResources().add(parcel.getResource());
                    }
                    if (parcel.getItem() != null) {
                        Application.getInstance().notify(observer -> observer.onAddItem(parcel.getItem()));
                        worldModule.getItems().add(parcel.getItem());
                    }
                    if (parcel.getConsumable() != null) {
                        Application.getInstance().notify(observer -> observer.onAddConsumable(parcel.getConsumable()));
                        worldModule.getConsumables().add(parcel.getConsumable());
                    }
                }
            }
        }
    }

    private void cleanMap(WorldModule worldModule) {
        ModuleHelper.getWorldModule().getParcelList().forEach(parcel -> {
            ParcelModel r = ModuleHelper.getWorldModule().getParcel(parcel.x + 1, parcel.y, parcel.z);
            ParcelModel l = ModuleHelper.getWorldModule().getParcel(parcel.x - 1, parcel.y, parcel.z);
            ParcelModel t = ModuleHelper.getWorldModule().getParcel(parcel.x, parcel.y + 1, parcel.z);
            ParcelModel b = ModuleHelper.getWorldModule().getParcel(parcel.x, parcel.y - 1, parcel.z);

            // Add resource on empty parcel surrounded by resources
            if (parcel.getResource() == null) {
                boolean isSurrounded = true;
                if (l != null && (l.getResource() == null || !l.getResource().isSolid())) isSurrounded = false;
                if (r != null && (r.getResource() == null || !r.getResource().isSolid())) isSurrounded = false;
                if (t != null && (t.getResource() == null || !t.getResource().isSolid())) isSurrounded = false;
                if (b != null && (b.getResource() == null || !b.getResource().isSolid())) isSurrounded = false;

                if (isSurrounded) {
                    ResourceModel resource = null;
                    if (l != null) resource = copyResource(l.getResource());
                    if (r != null) resource = copyResource(r.getResource());
                    if (t != null) resource = copyResource(t.getResource());
                    if (b != null) resource = copyResource(b.getResource());
                    if (resource != null) {
                        parcel.setResource(resource);
                        resource.setParcel(parcel);
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
                    parcel.setResource(null);
                }
            }
        });
    }

    private ResourceModel copyResource(ResourceModel fromResource) {
        ResourceModel resource = new ResourceModel(fromResource.getInfo());

        if (resource.isPlant()) {
            resource.getPlant().setMaturity(fromResource.getPlant().getMaturity());
        }

        if (resource.isRock()) {
            resource.getRock().setQuantity(fromResource.getRock().getQuantity());
        }

        return resource;
    }

    private void applyToParcel(WorldModule worldModule, RegionInfo.RegionTerrain terrain, ParcelModel parcel) {
        if (terrain.condition == null
                || ("rock".equals(terrain.condition) && parcel.getResource() != null && parcel.getResource().isRock())
                || ("ground".equals(terrain.condition) && (parcel.getResource() == null || !parcel.getResource().isRock()))) {

            // Set ground
//            if (terrain.typeId != -1) {
//                parcel.setType(terrain.typeId);
//            }

            // Add resource
            if (terrain.resource != null) {
                ResourceModel resource = new ResourceModel(Data.getData().getItemInfo(terrain.resource));
                if (resource.isRock()) {
                    resource.getRock().setQuantity(terrain.quantity != null ? Utils.getRandom(terrain.quantity) : 10);
                }
                parcel.setResource(resource);
                resource.setParcel(parcel);
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
        ModuleHelper.getCharacterModule().addRandom(freeParcels.poll());
        ModuleHelper.getCharacterModule().addRandom(freeParcels.poll());
        ModuleHelper.getCharacterModule().addRandom(freeParcels.poll());

        // Put resources
        ModuleHelper.getWorldModule().putObject("base.wood", freeParcels.poll(), 50);
        ModuleHelper.getWorldModule().putObject("base.wood", freeParcels.poll(), 50);
        ModuleHelper.getWorldModule().putObject("base.wood", freeParcels.poll(), 50);

        ModuleHelper.getWorldModule().putObject("base.military_meal", freeParcels.poll(), 25);
        ModuleHelper.getWorldModule().putObject("base.military_meal", freeParcels.poll(), 25);
        ModuleHelper.getWorldModule().putObject("base.military_meal", freeParcels.poll(), 25);

        ModuleHelper.getWorldModule().putObject("base.iron", freeParcels.poll(), 25);
        ModuleHelper.getWorldModule().putObject("base.iron", freeParcels.poll(), 25);

        game.getViewport().moveTo(startParcel.x, startParcel.y);
    }

}
