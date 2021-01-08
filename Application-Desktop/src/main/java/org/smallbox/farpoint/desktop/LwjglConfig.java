package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
import org.smallbox.faraway.util.Constant;

@ApplicationObject
public class LwjglConfig {

    private LwjglApplicationConfiguration lwjglConfig;

    @OnInit
    public void onInit() {
        DependencyManager.getInstance().createAndInit(ApplicationConfigService.class);
        ApplicationConfig applicationConfig = DependencyManager.getInstance().getDependency(ApplicationConfig.class);

        lwjglConfig = new LwjglApplicationConfiguration();

        lwjglConfig.foregroundFPS = applicationConfig.screen.foregroundFPS;
        lwjglConfig.backgroundFPS = applicationConfig.screen.backgroundFPS;

        lwjglConfig.x = applicationConfig.screen.position[0];
        lwjglConfig.y = applicationConfig.screen.position[1];
        lwjglConfig.width = applicationConfig.screen.resolution[0];
        lwjglConfig.height = applicationConfig.screen.resolution[1];
        lwjglConfig.fullscreen = "fullscreen".equals(applicationConfig.screen.mode);
        lwjglConfig.resizable = false;
        lwjglConfig.vSyncEnabled = false;
//        applicationConfig.useGL30 = true;
        lwjglConfig.title = Constant.NAME + " " + Constant.VERSION;

        if ("borderless".equals(applicationConfig.screen.mode)) {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
        }
    }

    public LwjglApplicationConfiguration getLwjglConfig() {
        return lwjglConfig;
    }
}