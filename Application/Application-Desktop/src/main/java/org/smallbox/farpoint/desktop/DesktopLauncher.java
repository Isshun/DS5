package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.flora.PlantModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

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

        new LwjglApplication(new GDXApplication(() ->
                Application.gameManager.createGame("base.planet.corrin", "mountain", 12, 16, 2, new GameManager.GameListener() {
                    @Override
                    public void onGameCreate(Game game) {
                        Application.moduleManager.getModule(CharacterModule.class).addRandom(WorldHelper.getParcel(4, 4, 1));
                        Application.moduleManager.getModule(CharacterModule.class).addRandom(WorldHelper.getParcel(4, 5, 1));
                        Application.moduleManager.getModule(CharacterModule.class).addRandom(WorldHelper.getParcel(4, 6, 1));
                        Application.moduleManager.getModule(CharacterModule.class).addRandom(WorldHelper.getParcel(4, 7, 1));
                        Application.moduleManager.getModule(ItemModule.class).addItem("base.cooker", true, 8, 2, 1);
                        Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.rice", 5, 5, 1);
                        Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.rice", 2, 5, 1);
//                        Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.rice", 8, 6, 1);
//                        Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.rice", 7, 6, 1);
//                        Application.moduleManager.getModule(PlantModule.class).addPlant("base.plant.rice", 9, 6, 1);
//                        Application.moduleManager.getModule(ConsumableModule.class).addConsumable("base.vegetable_rice", 10, 4, 4, 1);
//                        Application.moduleManager.getModule(ConsumableModule.class).addConsumable("base.vegetable_carrot", 10, 4, 6, 1);
//                        Application.moduleManager.getModule(WorldModule.class).getParcel(8, 8, 1).setRockName("base.granite");
//                        Application.moduleManager.getModule(WorldModule.class).getParcel(9, 8, 1).setRockName("base.calcite");
//                        Application.moduleManager.getModule(WorldModule.class).getParcel(10, 8, 1).setRockName("base.sandstone");

//                        List<ParcelModel> parcels = Application.moduleManager.getModule(WorldModule.class).getParcelList().stream().filter(parcel -> parcel.z == 1).collect(Collectors.toList());
//                        Application.data.consumables.forEach(itemInfo ->
//                                Application.moduleManager.getModule(ConsumableModule.class).addConsumable(itemInfo, 10, parcels.get(Application.data.consumables.indexOf(itemInfo))));
                    }

                    @Override
                    public void onGameUpdate(Game game) {
                    }
                })), LwjglConfig.from(applicationConfig));
    }

}
