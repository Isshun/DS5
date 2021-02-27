package org.smallbox.faraway.game.world.factory;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;

@ApplicationObject
public class LandSiteGenerator {
    @Inject private WorldFactoryDebug worldFactoryDebug;
    @Inject private WorldFactory worldFactory;
    @Inject private AssetManager assetManager;
    @Inject private DataManager dataManager;

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
