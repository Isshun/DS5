package main.java.org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

public class DesktopLauncher {

    public static void main (String[] arg) {
        //            Log.info("Load application applicationConfig");
//            try (FileReader fileReader = new FileReader(FileUtils.getFile("data/config.json"))) {
//                return new Gson().fromJson(fileReader, ApplicationConfig.class);
//            }
        ApplicationConfigService applicationConfigService = DependencyInjector.getInstance().registerModel(ApplicationConfigService.class, ApplicationConfigService::new);

        FileUtils.createRoamingDirectory();

        // Get native screen resolution
        java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

        new LwjglApplication(new GDXApplication(() -> getGameCallback(applicationConfigService.getConfig())), LwjglConfig.from(applicationConfigService.getConfig()));
    }

    private static void getGameCallback(ApplicationConfig applicationConfig) {
        switch (applicationConfig.debug.actionOnLoad) {
            case LAST_SAVE:
                Application.gameManager.loadLastGame();
                break;
            case NEW_GAME:
                GameFactory factory = Application.gameManager.createGameNew();
                factory.setScenario(applicationConfig.debug.scenario);
                factory.create();
                break;
        }
    }

}
