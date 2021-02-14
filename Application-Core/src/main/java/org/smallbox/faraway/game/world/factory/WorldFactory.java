package org.smallbox.faraway.game.world.factory;

import com.badlogic.gdx.math.MathUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.world.FastNoise;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.game.world.factory.old.MapFactoryConfig;

import java.util.ArrayList;
import java.util.List;

@ApplicationObject
public class WorldFactory {
    @Inject private CaveGenerator caveGenerator;
    @Inject private WorldModule worldModule;
    @Inject private Game game;
    @Inject private DataManager dataManager;

    public void buildMap() {
        MathUtils.random.setSeed(42);

        int floors = game.getInfo().worldFloors;
        int width = game.getInfo().worldWidth;
        int height = game.getInfo().worldHeight;

        List<Parcel> parcelList = new ArrayList<>();

        initDefaultRockAndGround(parcelList, width, height, floors);
        initRegionRockAndGround(parcelList, floors);

        if (game.getInfo().generateMountains) {
            computeGroundFloorMountains(parcelList, width, height, floors);
        }
//        cleanMap(parcelList, parcelsMap);

        worldModule.init(parcelList);

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

    private void initDefaultRockAndGround(List<Parcel> parcelList, int width, int height, int floors) {
        ItemInfo defaultRockInfo = dataManager.getItemInfo("base.granite");
        ItemInfo defaultGroundInfo = dataManager.getItemInfo("base.ground.grass");

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int f = 0; f < floors; f++) {
                    Parcel parcel = new Parcel(x + (y * width) + (f * width * height), x, y, f);
//                    if (parcel.z < floors - 1) {
//                        parcel.setRockInfo(defaultRockInfo);
//                    } else {
//                        parcel.setGroundInfo(defaultGroundInfo);
//                    }
                    parcel.setRockInfo(defaultRockInfo);
                    parcel.setGroundInfo(defaultGroundInfo);
                    parcelList.add(parcel);
                }
            }
        }
    }

    // Add region elements
    private void initRegionRockAndGround(List<Parcel> parcelList, int floors) {
        ItemInfo defaultRockInfo = dataManager.getItemInfo("base.granite");
        ItemInfo defaultGroundInfo = dataManager.getItemInfo("base.ground.grass");

        if (game.getInfo().region != null) {
//            ItemInfo regionGroundInfo = dataManager.getItemInfo("base.ground.rock");
            ItemInfo regionGroundInfo = dataManager.getItemInfo(game.getInfo().region.terrains.get(0).ground);

            parcelList.forEach(parcel -> {
//                if (parcel.z < floors - 1) {
//                    parcel.setRockInfo(defaultRockInfo);
//                    parcel.setGroundInfo(defaultRockInfo);
//                } else {
//                    parcel.setGroundInfo(regionGroundInfo);
//                }
                parcel.setGroundInfo(regionGroundInfo);
            });
        }
    }

    private void computeGroundFloorMountains(List<Parcel> parcels, int width, int height, int floors) {
        ItemInfo defaultRockInfo = dataManager.getItemInfo("base.granite");

        MapFactoryConfig config = MapFactoryConfig.createMountains();

        // Create and configure FastNoise object
        FastNoise noise = new FastNoise();
        noise.SetSeed(1337);
        noise.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        noise.SetFrequency(0.004f);
        noise.SetFractalType(FastNoise.FractalType.FBM);
        noise.SetFractalOctaves(7);
        noise.SetFractalLacunarity(2);
        noise.SetFractalGain(0.5f);
//        noise.SetNoiseType(FastNoise.NoiseType.Cellular);
//        noise.SetFrequency(0.35f);
//        noise.SetFractalOctaves(1);
//        noise.SetFractalType(FastNoise.FractalType.RigidMulti);
//
//        float[][] image = PerlingGenerator.GenerateWhiteNoise(width * 10, height * 10);
//        float[][] perlinNoise = PerlingGenerator.GeneratePerlinNoise(image, config.perlinOctave);
//        for (MapFactoryConfig.AdjustmentValue adjustment : config.adjustments) {
//            perlinNoise = PerlingGenerator.AdjustLevels(perlinNoise, adjustment.min, adjustment.max);
//        }

        parcels.stream().filter(parcel -> parcel.z == floors - 1).forEach(parcel ->
                parcel.setRockInfo(noise.GetNoise(parcel.x, parcel.y) + 0.5f > 0.45 ? defaultRockInfo : null));

        caveGenerator.addCave(parcels, 20, floors, 10, 10, 0);
        caveGenerator.addCave(parcels, 35, floors, 30, 30, 4);
    }

    private void cleanMap(List<Parcel> parcels) {
        parcels.forEach(parcel -> {
            Parcel r = safeParcel(parcels, parcel.x + 1, parcel.y, parcel.z);
            Parcel l = safeParcel(parcels, parcel.x - 1, parcel.y, parcel.z);
            Parcel t = safeParcel(parcels, parcel.x, parcel.y + 1, parcel.z);
            Parcel b = safeParcel(parcels, parcel.x, parcel.y - 1, parcel.z);

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

    public Parcel safeParcel(List<Parcel> parcels, int x, int y, int z) {
        return parcels.stream().filter(parcel -> parcel.x == x && parcel.y == y && parcel.z == z).findFirst().orElse(null);
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
