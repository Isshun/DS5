package org.smallbox.faraway.game.world.factory;

import com.badlogic.gdx.math.MathUtils;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.ArrayList;
import java.util.List;

@ApplicationObject
public class WorldFactory {
    @Inject private MountainsGenerator mountainsGenerator;
    @Inject private WorldModule worldModule;
    @Inject private DataManager dataManager;
    @Inject private Game game;

    public void buildMap() {
        MathUtils.random.setSeed(42);

        int floors = game.getInfo().worldFloors;
        int width = game.getInfo().worldWidth;
        int height = game.getInfo().worldHeight;

        List<Parcel> parcelList = new ArrayList<>();

        initDefaultRockAndGround(parcelList, width, height, floors);
        initRegionRockAndGround(parcelList, floors);

        if (game.getInfo().generateMountains) {
            mountainsGenerator.computeGroundFloorMountains(parcelList, width, height, floors);
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

}
