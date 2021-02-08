package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GameApplication;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;

import java.util.Optional;

public class DesktopLauncher {

    public static void main(String[] arg) {
//        new LwjglApplication(new TestLightApplication());
        Optional.of(DependencyManager.getInstance().createAndInit(LwjglConfig.class)).ifPresent(lwjglConfig ->
                new LwjglApplication(new GameApplication(lwjglConfig::applyConfig), lwjglConfig.getLwjglConfig()));
    }

}
