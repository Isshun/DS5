package org.smallbox.faraway.modules.world.factory;

import com.badlogic.gdx.math.MathUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.modules.world.factory.old.MapFactoryConfig;
import org.smallbox.faraway.modules.world.factory.old.PerlingGenerator;

import java.util.ArrayList;
import java.util.List;

@ApplicationObject
public class WorldFactory {

    @Inject
    private WorldModule worldModule;

    @Inject
    private Game game;

    @Inject
    private Data data;

    public void buildMap() {
        MathUtils.random.setSeed(42);

        int floors = game.getInfo().worldFloors;
        int width = game.getInfo().worldWidth;
        int height = game.getInfo().worldHeight;

        List<ParcelModel> parcelList = new ArrayList<>();
        ParcelModel[][][] parcelsMap = new ParcelModel[width][height][floors];

        initDefaultRockAndGround(parcelList, parcelsMap, width, height, floors);
        initRegionRockAndGround(parcelList, floors);

        if (game.getInfo().generateMountains) {
            computeGroundFloorMountains(parcelsMap, width, height);
        }
        cleanMap(parcelList, parcelsMap);

        worldModule.init(game, parcelsMap, parcelList);

//        // Add region terrains
//        for (RegionInfo.RegionTerrain terrain: regionInfo.terrains) {
//            if ("random_light".equals(terrain.pattern) || "random_large".equals(terrain.pattern)) {
//                Log.notice("Create old with random pattern: " + terrain.pattern);
//                parcelList.stream()
//                        .filter(parcel -> MathUtils.random() < ("random_light".equals(terrain.pattern) ? 0.05f : 0.1f))
//                        .forEach(parcel -> applyToParcel(terrain, parcel));
//            }
//            else if (WorldFactoryConfig.has(terrain.pattern)) {
//                Log.notice("Create resources with pattern: " + terrain.pattern);
//                for (int z = 0; z < _floors; z++) {
//                    new MidpointDisplacement(WorldFactoryConfig.get(terrain.pattern)).onCreateJob(game.getInfo(), _parcels, z, parcel -> applyToParcel(terrain, parcel));
//                }
//            }
//            else {
//                parcelList.forEach(parcel -> applyToParcel(terrain, parcel));
//            }
//        }
//
//        // Clean old
//        cleanMap(parcelList, _parcels);
//
//        WorldHelper.init(game.getInfo(), _parcels);
//
//        ModuleHelper.getWorldModule().init(game, _parcels, parcelList);
//
//        Application.pathManager.init(parcelList);
    }

    private void initDefaultRockAndGround(List<ParcelModel> parcelList, ParcelModel[][][] parcelsMap, int width, int height, int floors) {
        ItemInfo defaultRockInfo = data.getItemInfo("base.granite");
        ItemInfo defaultGroundInfo = data.getItemInfo("base.ground.grass");

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int f = 0; f < floors; f++) {
                    ParcelModel parcel = new ParcelModel(x + (y * width) + (f * width * height), x, y, f);
                    if (parcel.z < floors - 1) {
                        parcel.setRockInfo(defaultRockInfo);
                    } else {
                        parcel.setGroundInfo(defaultGroundInfo);
                    }
                    parcelsMap[x][y][f] = parcel;
                    parcelList.add(parcel);
                }
            }
        }
    }

    // Add region elements
    private void initRegionRockAndGround(List<ParcelModel> parcelList, int floors) {
        ItemInfo defaultRockInfo = data.getItemInfo("base.granite");
        ItemInfo defaultGroundInfo = data.getItemInfo("base.ground.grass");

        if (game.getInfo().region != null) {
            ItemInfo regionGroundInfo = data.getItemInfo(game.getInfo().region.terrains.get(0).ground);

            parcelList.forEach(parcel -> {
                if (parcel.z < floors - 1) {
                    parcel.setRockInfo(defaultRockInfo);
                    parcel.setGroundInfo(defaultRockInfo);
                } else {
                    parcel.setGroundInfo(regionGroundInfo);
                }
            });
        }
    }

    private void computeGroundFloorMountains(ParcelModel[][][] _parcels, int width, int height) {
        ItemInfo defaultRockInfo = data.getItemInfo("base.granite");

        MapFactoryConfig config = MapFactoryConfig.createMountains();

        float[][] image = PerlingGenerator.GenerateWhiteNoise(width * 10, height * 10);
        float[][] perlinNoise = PerlingGenerator.GeneratePerlinNoise(image, config.perlinOctave);
        for (MapFactoryConfig.AdjustmentValue adjustment : config.adjustments) {
            perlinNoise = PerlingGenerator.AdjustLevels(perlinNoise, adjustment.min, adjustment.max);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (perlinNoise[x * 10][y * 10] < 0.7) {
                    _parcels[x][y][1].setRockInfo(defaultRockInfo);
                }
            }
        }
    }

    private void cleanMap(List<ParcelModel> parcelList, ParcelModel[][][] parcels) {
        parcelList.forEach(parcel -> {
            ParcelModel r = safeParcel(parcels, parcel.x + 1, parcel.y, parcel.z);
            ParcelModel l = safeParcel(parcels, parcel.x - 1, parcel.y, parcel.z);
            ParcelModel t = safeParcel(parcels, parcel.x, parcel.y + 1, parcel.z);
            ParcelModel b = safeParcel(parcels, parcel.x, parcel.y - 1, parcel.z);

            // Add resource on empty parcel surrounded by rock
            if (!parcel.hasRock()) {
                boolean isSurrounded = true;
                if (l != null && !l.hasRock()) isSurrounded = false;
                if (r != null && !r.hasRock()) isSurrounded = false;
                if (t != null && !t.hasRock()) isSurrounded = false;
                if (b != null && !b.hasRock()) isSurrounded = false;

                if (isSurrounded) {
                    if (l != null) parcel.setRockInfo(l.getRockInfo());
                    if (r != null) parcel.setRockInfo(r.getRockInfo());
                    if (t != null) parcel.setRockInfo(t.getRockInfo());
                    if (b != null) parcel.setRockInfo(b.getRockInfo());
                }
            }

            // Remove resource if neighbors parcels has no resources
            if (parcel.hasRock()) {
                boolean isAlone = true;
                if (l != null && l.hasRock()) isAlone = false;
                if (r != null && r.hasRock()) isAlone = false;
                if (t != null && t.hasRock()) isAlone = false;
                if (b != null && b.hasRock()) isAlone = false;

                if (isAlone) {
                    parcel.setRockInfo(null);
                    if (l != null) parcel.setGroundInfo(l.getGroundInfo());
                    if (r != null) parcel.setGroundInfo(r.getGroundInfo());
                    if (t != null) parcel.setGroundInfo(t.getGroundInfo());
                    if (b != null) parcel.setGroundInfo(b.getGroundInfo());
                }
            }
        });
    }

    private ParcelModel safeParcel(ParcelModel[][][] parcels, int x, int y, int z) {
        return (x < 0 || x >= game.getInfo().worldWidth || y < 0 || y >= game.getInfo().worldHeight || z < 0 || z >= game.getInfo().worldFloors) ? null : parcels[x][y][z];
    }
//
//    private void applyToParcel(Data data, RegionInfo.RegionTerrain terrain, ParcelModel parcel) {
//        if (terrain.condition == null
//                || ("rock".equals(terrain.condition) && parcel.hasRock())
//                || ("ground".equals(terrain.condition) && !parcel.hasRock() && parcel.hasGround())) {
//
//            // Set liquid
//            if (terrain.liquid != null) {
//                if (parcel.z == _floors - 1) {
//                    ItemInfo liquidInfo = data.getItemInfo(terrain.liquid);
//
//                    // Replace ground on current parcel by liquid surface
//                    parcel.setGroundInfo(liquidInfo.surface);
//
//                    // Put ground on z-1 parcel
//                    if (terrain.ground != null) {
//                        _parcels[parcel.x][parcel.y][parcel.z - 1].setGroundInfo(data.getItemInfo(terrain.ground));
//                        _parcels[parcel.x][parcel.y][parcel.z - 1].setRockInfo(null);
//                    }
//
//                    // Put liquid on z-1 parcel
//                    _parcels[parcel.x][parcel.y][parcel.z - 1].setLiquidInfo(liquidInfo, 0.5);
//                }
//                return;
//            }
//
//            // Set ground
//            if (terrain.ground != null) {
//                parcel.setGroundInfo(data.getItemInfo(terrain.ground));
//            }
//
//            // Add resource
//            if (terrain.resource != null) {
//                ItemInfo resourceInfo = data.getItemInfo(terrain.resource);
//                if (resourceInfo.isRock) {
//                    parcel.setRockInfo(resourceInfo);
//                }
////                if (resourceInfo.isPlant) {
////                    PlantItem resource = new PlantItem(resourceInfo);
////                    parcel.setPlant(resource);
////                    resource.setParcel(parcel);
////                }
////                if (resource.isRock()) {
////                    resource.getRock().setQuantity(terrain.quantity != null ? Utils.getRandom(terrain.quantity) : 10);
////                }
//            }
//        }
//    }

//    public Queue<ParcelModel> getFreeParcels(ParcelModel startParcel) {
//        List<ParcelModel> freeParcels = new ArrayList<>();
//        for (int x = startParcel.x - 5; x < startParcel.x + 5; x++) {
//            for (int y = startParcel.y - 5; y < startParcel.y + 5; y++) {
//                ParcelModel parcel = WorldHelper.getParcel(x, y, startParcel.z);
//                if (parcel != null && parcel.isWalkable() && !parcel.hasPlant()) {
//                    freeParcels.add(parcel);
//                }
//            }
//        }
//        Collections.shuffle(freeParcels);
//
//        return new LinkedList<>(freeParcels);
//    }

    public void createLandSite(Game game) {
        throw new NotImplementedException("");

//        // Get free parcels
//        ParcelModel startParcel = null;
//        Queue<ParcelModel> freeParcels = null;
//        for (int i = 0; i < 50; i++) {
//            startParcel = WorldHelper.getRandomFreeSpace(game.getInfo().worldFloors - 1, false, true);
//            freeParcels = getFreeParcels(startParcel);
//            if (freeParcels.size() > 15) {
//                break;
//            }
//        }
//        assert freeParcels != null;
//        assert startParcel != null;
//
//        // Put characters
//        ModuleHelper.getCharacterModule().addRandom(freeParcels.poll());
//        ModuleHelper.getCharacterModule().addRandom(freeParcels.poll());
//        ModuleHelper.getCharacterModule().addRandom(freeParcels.poll());
//
//        // Put resources
//        ModuleHelper.getWorldModule().putObject("base.consumable.wood_log", freeParcels.poll(), 500);
//        ModuleHelper.getWorldModule().putObject("base.consumable.wood_log", freeParcels.poll(), 500);
//        ModuleHelper.getWorldModule().putObject("base.consumable.wood_log", freeParcels.poll(), 500);
//        ModuleHelper.getWorldModule().putObject("base.consumable.wood_log", freeParcels.poll(), 500);
//        ModuleHelper.getWorldModule().putObject("base.consumable.wood_log", freeParcels.poll(), 500);
//
//        ModuleHelper.getWorldModule().putObject("base.military_meal", freeParcels.poll(), 25);
//        ModuleHelper.getWorldModule().putObject("base.military_meal", freeParcels.poll(), 25);
//        ModuleHelper.getWorldModule().putObject("base.military_meal", freeParcels.poll(), 25);
//
//        ModuleHelper.getWorldModule().putObject("base.iron_plate", freeParcels.poll(), 25);
//        ModuleHelper.getWorldModule().putObject("base.iron_plate", freeParcels.poll(), 25);
//
//        game.getViewport().moveTo(startParcel.x, startParcel.y);
    }

}
