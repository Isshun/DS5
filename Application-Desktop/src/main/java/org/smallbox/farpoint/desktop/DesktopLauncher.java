package main.java.org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
import org.smallbox.faraway.util.FileUtils;

public class DesktopLauncher {

    public static void main (String[] arg) {
        //            Log.info("Load application applicationConfig");
//            try (FileReader fileReader = new FileReader(FileUtils.getFile("data/config.json"))) {
//                return new Gson().fromJson(fileReader, ApplicationConfig.class);
//            }
        DependencyInjector.getInstance().createAndInit(ApplicationConfigService.class);
        ApplicationConfig applicationConfig = DependencyInjector.getInstance().getDependency(ApplicationConfig.class);

        FileUtils.createRoamingDirectory();

//        // Get native screen resolution
//        java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//        int width = gd.getDisplayMode().getWidth();
//        int height = gd.getDisplayMode().getHeight();
//        double ratio = (double)width / height;
//        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

        new LwjglApplication(new GDXApplication(() -> getGameCallback(applicationConfig)), LwjglConfig.from(applicationConfig));
//        new LwjglApplication(new PerlinTestApp());
    }

    private static void getGameCallback(ApplicationConfig applicationConfig) {
        if (applicationConfig.debug != null && applicationConfig.debug.actionOnLoad != null) {
            switch (applicationConfig.debug.actionOnLoad) {
                case LAST_SAVE:
                    DependencyInjector.getInstance().getDependency(GameManager.class).loadLastGame();
                    break;
                case NEW_GAME:
                    GameFactory factory = DependencyInjector.getInstance().getDependency(GameFactory.class);
                    factory.create(applicationConfig.debug.scenario);
                    break;
            }
        }
    }

}
