package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.GameModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Alex on 26/06/2015.
 */
public class FaunaModule extends GameModule {
    private List<RegionInfo.RegionFauna>    FAUNA_COMMON_POOL = new ArrayList<>();
    private List<RegionInfo.RegionFauna>    FAUNA_UNUSUAL_POOL = new ArrayList<>();

//    private List<AnimalModel>               _animals = new ArrayList<>();

    @Override
    protected void onLoaded() {
        printInfo("[FaunaModule] loads");

        RegionInfo region = Game.getInstance().getInfo().region;

        double commonTotalFrequency = 0;
        double unusualTotalFrequency = 0;
        for (RegionInfo.RegionFauna faunaInfo: region.fauna) {
            if ("common".equals(faunaInfo.group)) {
                commonTotalFrequency += faunaInfo.frequency;
            } else {
                unusualTotalFrequency += faunaInfo.frequency;
            }
        }

        for (RegionInfo.RegionFauna faunaInfo: region.fauna) {
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
    protected boolean loadOnStart() {
        return Data.config.manager.fauna;
    }

    @Override
    protected void onUpdate(int tick) {
        // Drop unusual
        if (tick % 800 == 0) {
            Random r = new Random();
            RegionInfo.RegionFauna faunaInfo = FAUNA_UNUSUAL_POOL.get(r.nextInt(100));
            int count = faunaInfo.number[0] == faunaInfo.number[1] ? faunaInfo.number[1] : faunaInfo.number[0] + r.nextInt(faunaInfo.number[1] - faunaInfo.number[0]);
            printInfo("[FaunaModule] Add unusual " + faunaInfo.name + " x " + count);
            addFauna(faunaInfo, count);
        }

        // Drop common
        else if (tick % 100 == 0) {
            Random r = new Random();
            RegionInfo.RegionFauna faunaInfo = FAUNA_COMMON_POOL.get(r.nextInt(100));
            int count = faunaInfo.number[0] == faunaInfo.number[1] ? faunaInfo.number[1] : faunaInfo.number[0] + r.nextInt(faunaInfo.number[1] - faunaInfo.number[0]);
            printInfo("[FaunaModule] Add common " + faunaInfo.name + " x " + count);
            addFauna(faunaInfo, count);
        }

//        for (AnimalModel animal: _animals) {
//            if (!animal.isMoving()) {
//                ParcelModel parcel = WorldHelper.getRandomFreeSpace(false, true);
//                animal.moveTo(parcel.x, parcel.y);
//            }
//            animal.move();
//            animal.action();
//        }
    }

    private void addFauna(RegionInfo.RegionFauna faunaInfo, int count) {
        ParcelModel parcel = WorldHelper.getRandomFreeSpace(false, true);
        if (parcel != null) {
//            _animals.add(new AnimalModel(Utils.getUUID(), faunaInfo, parcel.x, parcel.y));
        } else {
            printError("[FaunaModule] No space to proc fauna");
        }
    }

//    public List<AnimalModel> getAnimals() {
//        return _animals;
//    }
}
