package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;

import java.util.Optional;

public class DesktopLauncher {

    public static void main (String[] arg) {
        Optional.of(DependencyManager.getInstance().createAndInit(LwjglConfig.class)).ifPresent(lwjglConfig ->
                new LwjglApplication(new GDXApplication(lwjglConfig::applyConfig), lwjglConfig.getLwjglConfig()));
    }

}
