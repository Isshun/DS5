package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.config.ApplicationConfigService;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnSettingsUpdate;
import org.smallbox.faraway.util.Constant;

@ApplicationObject
public class LwjglConfig {
    private LwjglApplicationConfiguration lwjglConfig;
    private ApplicationConfig applicationConfig;

    @OnInit
    public void onInit() {
        DependencyManager.getInstance().createAndInit(ApplicationConfigService.class);
        applicationConfig = DependencyManager.getInstance().getDependency(ApplicationConfig.class);

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
        lwjglConfig.samples = 2;
//        applicationConfig.useGL30 = true;
        lwjglConfig.title = Constant.NAME + " " + Constant.VERSION;
    }

    @OnSettingsUpdate
    public void applyConfig() {
        ApplicationConfig applicationConfig = DependencyManager.getInstance().getDependency(ApplicationConfig.class);

        Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();

        if ("fullscreen".equals(applicationConfig.screen.mode)) {
            Gdx.graphics.setFullscreenMode(displayMode);
        }

        if ("borderless".equals(applicationConfig.screen.mode)) {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
            Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width, Gdx.graphics.getDisplayMode().height);
        }

        if ("window".equals(applicationConfig.screen.mode)) {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
            Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public LwjglApplicationConfiguration getLwjglConfig() {
        return lwjglConfig;
    }

}