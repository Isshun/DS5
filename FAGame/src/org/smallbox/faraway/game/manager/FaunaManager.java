package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.AnimalModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.planet.RegionInfo;
import org.smallbox.faraway.game.model.planet.RegionModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Alex on 26/06/2015.
 */
public class FaunaManager extends BaseManager {
    private List<RegionInfo.RegionFauna>    FAUNA_COMMON_POOL = new ArrayList<>();
    private List<RegionInfo.RegionFauna>    FAUNA_UNUSUAL_POOL = new ArrayList<>();

    private List<AnimalModel>               _animals = new ArrayList<>();
    private WorldManager                    _worldManager;

    @Override
    protected void onCreate() {
        _worldManager = Game.getWorldManager();
        RegionModel region = Game.getInstance().getRegion();

        double commonTotalFrequency = 0;
        double unusualTotalFrequency = 0;
        for (RegionInfo.RegionFauna faunaInfo: region.getInfo().fauna) {
            if ("common".equals(faunaInfo.group)) {
                commonTotalFrequency += faunaInfo.frequency;
            } else {
                unusualTotalFrequency += faunaInfo.frequency;
            }
        }

        for (RegionInfo.RegionFauna faunaInfo: region.getInfo().fauna) {
            if ("common".equals(faunaInfo.group)) {
                for (int i = 0; i < faunaInfo.frequency * 100 / commonTotalFrequency; i++) {
                    FAUNA_COMMON_POOL.add(faunaInfo);
                }
            } else {
                for (int i = 0; i < faunaInfo.frequency * 100 / unusualTotalFrequency; i++) {
                    FAUNA_UNUSUAL_POOL.add(faunaInfo);
                }
            }
        }
    }

    @Override
    protected void onUpdate(int tick) {
        // Drop unusual
        if (tick % 800 == 0) {
            Random r = new Random();
            RegionInfo.RegionFauna faunaInfo = FAUNA_UNUSUAL_POOL.get(r.nextInt(100));
            int count = faunaInfo.number[0] == faunaInfo.number[1] ? faunaInfo.number[1] : faunaInfo.number[0] + r.nextInt(faunaInfo.number[1] - faunaInfo.number[0]);
            Log.info("[FaunaManager] Add unusual " + faunaInfo.name + " x " + count);
            addFauna(faunaInfo, count);
        }

        // Drop common
        else if (tick % 100 == 0) {
            Random r = new Random();
            RegionInfo.RegionFauna faunaInfo = FAUNA_COMMON_POOL.get(r.nextInt(100));
            int count = faunaInfo.number[0] == faunaInfo.number[1] ? faunaInfo.number[1] : faunaInfo.number[0] + r.nextInt(faunaInfo.number[1] - faunaInfo.number[0]);
            Log.info("[FaunaManager] Add common " + faunaInfo.name + " x " + count);
            addFauna(faunaInfo, count);
        }

        for (AnimalModel animal: _animals) {
            if (!animal.isMoving()) {
                ParcelModel parcel = _worldManager.getRandomFreeSpace(false, true);
                animal.moveTo(parcel.getX(), parcel.getY());
            }
            animal.move();
            animal.action();
        }

    }

    private void addFauna(RegionInfo.RegionFauna faunaInfo, int count) {
        ParcelModel parcel = _worldManager.getRandomFreeSpace(false, true);
        if (parcel != null) {
            _animals.add(new AnimalModel(Utils.getUUID(), faunaInfo, parcel.getX(), parcel.getY()));
        } else {
            Log.error("[FaunaManager] No space to proc fauna");
        }
    }

    public List<AnimalModel> getAnimals() {
        return _animals;
    }
}
