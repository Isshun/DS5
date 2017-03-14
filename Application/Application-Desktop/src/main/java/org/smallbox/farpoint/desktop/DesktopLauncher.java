package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.CharacterTimetableExtra;
import org.smallbox.faraway.modules.character.model.HumanModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.StorageArea;
import org.smallbox.faraway.modules.dig.DigArea;
import org.smallbox.faraway.modules.characterDisease.DiseaseInfo;
import org.smallbox.faraway.modules.characterDisease.CharacterDiseaseModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.plant.GardenArea;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.util.Arrays;

public class DesktopLauncher {

    public static void main (String[] arg) {

        ApplicationConfig applicationConfig = DependencyInjector.getInstance().registerModel(ApplicationConfig.class, () -> {
            return new ApplicationConfig();
//            Log.info("Load application APPLICATION_CONFIG");
//            try (FileReader fileReader = new FileReader(FileUtils.getFile("data/config.json"))) {
//                return new Gson().fromJson(fileReader, ApplicationConfig.class);
//            }
        });

        FileUtils.createRoamingDirectory();

        // Get native screen resolution
        java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

//        new LwjglApplication(new GDXApplication(() -> {}), LwjglConfig.from(applicationConfig));
        new LwjglApplication(new GDXApplication(testGame()), LwjglConfig.from(applicationConfig));
    }

    private static GDXApplication.GameTestCallback testGame() {
        return () -> Application.gameManager.createGame("base.planet.corrin", "mountain", 12, 16, 2, new GameManager.GameListener() {
            @Override
            public void onGameCreate(Game game) {

//                Application.moduleManager.getModule(CharacterModule.class).addRandom(HumanModel.class);

                CharacterModel character = Application.moduleManager.getModule(CharacterModule.class).addRandom(HumanModel.class);
                character.addInventory("base.consumable.vegetable.rice", 10);
                character.addInventory("base.consumable.vegetable.carrot", 10);
                character.setParcel(WorldHelper.getParcel(1, 1, 1));
//                character.moveTo(WorldHelper.getParcel(5, 3, 1));
                character.moveTo(WorldHelper.getParcel(8, 4, 1));

                for (int i = 0; i < 6; i++) {
                    character.getExtra(CharacterTimetableExtra.class).setState(i, CharacterTimetableExtra.State.SLEEP);
                }

                for (int i = 21; i < 24; i++) {
                    character.getExtra(CharacterTimetableExtra.class).setState(i, CharacterTimetableExtra.State.SLEEP);
                }

                Application.moduleManager.getModule(ConsumableModule.class).addConsumable("base.consumable.easy_meal", 1000, 2, 2, 1);
                Application.moduleManager.getModule(ConsumableModule.class).addConsumable("base.consumable.drink.water", 1000, 2, 2, 1);

                DiseaseInfo diseaseInfo = new DiseaseInfo();
                diseaseInfo.label = "di test";
                Application.moduleManager.getModule(CharacterDiseaseModule.class).addDisease(diseaseInfo, character);

//                Application.moduleManager.getModule(CharacterModule.class).addRandom(HumanModel.class);
//                Application.moduleManager.getModule(CharacterModule.class).addRandom(HumanModel.class);
//                        Application.moduleManager.getModule(CharacterModule.class).addRandom(WorldHelper.getParcel(4, 5, 1));
//                        Application.moduleManager.getModule(CharacterModule.class).addRandom(WorldHelper.getParcel(4, 6, 1));
//                        Application.moduleManager.getModule(CharacterModule.class).addRandom(WorldHelper.getParcel(4, 7, 1));

//                        Application.moduleManager.getModule(ItemModule.class).addItem("base.item.cooker", true, 8, 2, 1);
                Application.moduleManager.getModule(ItemModule.class).addItem("base.item.bed.wood", true, 8, 2, 1)
                        .setHealth(25);

//                        for (int i = 1; i <= 4; i++) {
//                            for (int j = 5; j <= 10; j++) {
//                                Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.rice", i, j, 1);
//                            }
//                        }

                Application.moduleManager.getModule(AreaModule.class).addArea(DigArea.class, Arrays.asList(
                        WorldHelper.getParcel(8, 6, 1),
                        WorldHelper.getParcel(7, 6, 1),
                        WorldHelper.getParcel(8, 7, 1),
                        WorldHelper.getParcel(7, 7, 1)));

                Application.moduleManager.getModule(AreaModule.class).addArea(StorageArea.class, Arrays.asList(
                        WorldHelper.getParcel(8, 10, 1),
                        WorldHelper.getParcel(7, 10, 1),
                        WorldHelper.getParcel(8, 11, 1),
                        WorldHelper.getParcel(7, 11, 1)));

                Application.moduleManager.getModule(AreaModule.class).addArea(GardenArea.class, Arrays.asList(
                        WorldHelper.getParcel(2, 10, 1),
                        WorldHelper.getParcel(3, 10, 1),
                        WorldHelper.getParcel(2, 11, 1),
                        WorldHelper.getParcel(3, 11, 1)));

//                        Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.rice", 8, 6, 1);
//                        Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.rice", 7, 6, 1);
//                        Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.rice", 9, 6, 1);
//                        Application.moduleManager.getModule(ConsumableModule.class).addConsumable("base.consumable.vegetable.rice", 10, 4, 4, 1);
//                        Application.moduleManager.getModule(ConsumableModule.class).addConsumable("base.consumable.vegetable.carrot", 10, 4, 6, 1);

//                for (int i = 7; i <= 9; i++) {
//                    for (int j = 5; j <= 8; j++) {
//                        Application.moduleManager.getModule(WorldModule.class).getParcel(i, j, 1).setRockName("base.granite");
//                    }
//                }
//
//                Application.moduleManager.getModule(WorldModule.class).getParcel(9, 8, 1).setRockName("base.calcite");
//                Application.moduleManager.getModule(WorldModule.class).getParcel(10, 8, 1).setRockName("base.sandstone");

//                        List<ParcelModel> parcels = Application.moduleManager.getModule(WorldModule.class).getParcelList().stream().filter(parcel -> parcel.z == 1).collect(Collectors.toList());
//                        Application.data.consumables.forEach(itemInfo ->
//                                Application.moduleManager.getModule(ConsumableModule.class).addConsumable(itemInfo, 10, parcels.get(Application.data.consumables.indexOf(itemInfo))));
            }

            @Override
            public void onGameUpdate(Game game) {
            }
        });
    }

}
