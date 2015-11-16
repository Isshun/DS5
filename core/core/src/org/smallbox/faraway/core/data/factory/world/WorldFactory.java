package org.smallbox.faraway.core.data.factory.world;

import com.badlogic.gdx.math.MathUtils;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Log;

import java.util.*;

/**
 * Created by Alex on 06/07/2015.
 */
public class WorldFactory {
    private ParcelModel[][][]   _parcels;
    private int                 _floors;
    private int                 _width;
    private int                 _height;

    public void create(Game game, WorldModule worldModule, RegionInfo regionInfo) {
        MathUtils.random.setSeed(42);

        _floors = game.getInfo().worldFloors;
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
        _parcels = new ParcelModel[_width][_height][_floors];
        List<ParcelModel> parcelList = new ArrayList<>();
        Map<Integer, List<ParcelModel>> parcelListFloors = new HashMap<>();
        for (int f = 0; f < _floors; f++) {
            parcelListFloors.put(f, new ArrayList<>());
        }

        // Create parcels
        ItemInfo groundInfo = Data.getData().getItemInfo("base.ground.sand");
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                for (int f = 0; f < _floors; f++) {
                    ParcelModel parcel = new ParcelModel(x + (y * _width) + (f * _width * _height), x, y, f);
                    parcel.setGroundInfo(groundInfo);
                    parcelList.add(parcel);
                    parcelListFloors.get(f).add(parcel);
                    _parcels[x][y][f] = parcel;
                }
            }
        }

        // Add underground rock
        ItemInfo graniteInfo = Data.getData().getItemInfo("base.granite");
        for (int z = 0; z < _floors - 1; z++) {
            for (int y = 0; y < _height; y++) {
                for (int x = 0; x < _width; x++) {
                    _parcels[x][y][z].setRockInfo(graniteInfo);
                }
            }
        }

        // Add region terrains
        for (RegionInfo.RegionTerrain terrain: regionInfo.terrains) {
            if ("random_light".equals(terrain.pattern) || "random_large".equals(terrain.pattern)) {
                Log.notice("Create old with random pattern: " + terrain.pattern);
                parcelList.stream()
                        .filter(parcel -> MathUtils.random() < ("random_light".equals(terrain.pattern) ? 0.05f : 0.1f))
                        .forEach(parcel -> applyToParcel(terrain, parcel));
            }
            else if (terrain.pattern != null) {
                Log.notice("Create resources with pattern: " + terrain.pattern);
                for (int z = 0; z < _floors; z++) {
                    if (z == _floors - 1 || "rock".equals(terrain.condition)) {
                        new MidpointDisplacement(WorldFactoryConfig.get(terrain.pattern)).create(game.getInfo(), _parcels, z, parcel -> applyToParcel(terrain, parcel));
                    }
                }
            }
            else {
                parcelList.forEach(parcel -> applyToParcel(terrain, parcel));
            }
        }

        // Clean old
        cleanMap(parcelList, _parcels);

        WorldHelper.init(game.getInfo(), _parcels);
        ModuleHelper.getWorldModule().init(game, _parcels, parcelList);
        ((PathManager)ModuleManager.getInstance().getModule(PathManager.class)).init(game.getInfo());
    }

    private void cleanMap(List<ParcelModel> parcelList, ParcelModel[][][] parcels) {
        parcelList.forEach(parcel -> {
            ParcelModel r = getParcel(parcels, parcel.x + 1, parcel.y, parcel.z);
            ParcelModel l = getParcel(parcels, parcel.x - 1, parcel.y, parcel.z);
            ParcelModel t = getParcel(parcels, parcel.x, parcel.y + 1, parcel.z);
            ParcelModel b = getParcel(parcels, parcel.x, parcel.y - 1, parcel.z);

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
                }
            }
        });
    }

    private ParcelModel getParcel(ParcelModel[][][] parcels, int x, int y, int z) {
        return (x < 0 || x >= _width || y < 0 || y >= _height || z < 0 || z >= _floors) ? null : parcels[x][y][z];
    }

    private void applyToParcel(RegionInfo.RegionTerrain terrain, ParcelModel parcel) {
        if (terrain.condition == null
                || ("rock".equals(terrain.condition) && parcel.hasRock())
                || ("ground".equals(terrain.condition) && !parcel.hasRock())) {

            // Set ground
//            if (terrain.typeId != -1) {
//                parcel.setType(terrain.typeId);
//            }

            // Add resource
            if (terrain.resource != null) {
                ItemInfo resourceInfo = Data.getData().getItemInfo(terrain.resource);
                if (resourceInfo.isRock) {
                    parcel.setRockInfo(resourceInfo);
                }
                if (resourceInfo.isPlant) {
                    PlantModel resource = new PlantModel(resourceInfo);
                    parcel.setPlant(resource);
                    resource.setParcel(parcel);
                }
//                if (resource.isRock()) {
//                    resource.getRock().setQuantity(terrain.quantity != null ? Utils.getRandom(terrain.quantity) : 10);
//                }
            }
        }
    }

    public Queue<ParcelModel> getFreeParcels(ParcelModel startParcel) {
        List<ParcelModel> freeParcels = new ArrayList<>();
        for (int x = startParcel.x - 5; x < startParcel.x + 5; x++) {
            for (int y = startParcel.y - 5; y < startParcel.y + 5; y++) {
                ParcelModel parcel = WorldHelper.getParcel(x, y);
                if (parcel != null && parcel.isWalkable() && !parcel.hasPlant()) {
                    freeParcels.add(parcel);
                }
            }
        }
        Collections.shuffle(freeParcels);

        return new LinkedList<>(freeParcels);
    }

    public void createLandSite(Game game) {
        // Get free parcels
        ParcelModel startParcel = null;
        Queue<ParcelModel> freeParcels = null;
        for (int i = 0; i < 50; i++) {
            startParcel = WorldHelper.getRandomFreeSpace(false, true);
            freeParcels = getFreeParcels(startParcel);
            if (freeParcels.size() > 15) {
                break;
            }
        }
        assert freeParcels != null;
        assert startParcel != null;

        // Put characters
        ModuleHelper.getCharacterModule().addRandom(freeParcels.poll());
        ModuleHelper.getCharacterModule().addRandom(freeParcels.poll());
        ModuleHelper.getCharacterModule().addRandom(freeParcels.poll());

        // Put resources
        ModuleHelper.getWorldModule().putObject("base.wood_log", freeParcels.poll(), 500);
        ModuleHelper.getWorldModule().putObject("base.wood_log", freeParcels.poll(), 500);
        ModuleHelper.getWorldModule().putObject("base.wood_log", freeParcels.poll(), 500);
        ModuleHelper.getWorldModule().putObject("base.wood_log", freeParcels.poll(), 500);
        ModuleHelper.getWorldModule().putObject("base.wood_log", freeParcels.poll(), 500);

        ModuleHelper.getWorldModule().putObject("base.military_meal", freeParcels.poll(), 25);
        ModuleHelper.getWorldModule().putObject("base.military_meal", freeParcels.poll(), 25);
        ModuleHelper.getWorldModule().putObject("base.military_meal", freeParcels.poll(), 25);

        ModuleHelper.getWorldModule().putObject("base.iron_plate", freeParcels.poll(), 25);
        ModuleHelper.getWorldModule().putObject("base.iron_plate", freeParcels.poll(), 25);

        game.getViewport().moveTo(startParcel.x, startParcel.y);
    }

    public ParcelModel[][][] getParcels() {
        return _parcels;
    }
}
