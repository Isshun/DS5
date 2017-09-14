package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.common.ApplicationConfig;
import org.smallbox.faraway.common.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.common.util.FileUtils;
import org.smallbox.faraway.common.util.Log;

public class DesktopLauncher {

    public static void main (String[] arg) {
        //            Log.info("Load application applicationConfig");
//            try (FileReader fileReader = new FileReader(FileUtils.getFile("data/config.json"))) {
//                return new Gson().fromJson(fileReader, ApplicationConfig.class);
//            }
        ApplicationConfig applicationConfig = DependencyInjector.getInstance().registerModel(ApplicationConfig.class, ApplicationConfig::new);

        FileUtils.createRoamingDirectory();

        // Get native screen resolution
        java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

        new LwjglApplication(new GDXApplication(() -> getGameCallback(applicationConfig)), LwjglConfig.from(applicationConfig));
    }

    private static void getGameCallback(ApplicationConfig applicationConfig) {
        switch (applicationConfig.debug.actionOnLoad) {
//            case LAST_SAVE:
//                Application.gameManager.loadLastGame();
//                break;
//            case NEW_GAME:
//                GameFactory factory = Application.gameManager.createGameNew();
//                factory.setScenario(applicationConfig.debug.scenario);
//                factory.create();
//                break;
        }
    }

}
