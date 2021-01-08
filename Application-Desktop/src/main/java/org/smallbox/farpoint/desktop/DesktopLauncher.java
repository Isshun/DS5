package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;

public class DesktopLauncher {

    public static void main (String[] arg) {
        new LwjglApplication(new GDXApplication(), DependencyManager.getInstance().createAndInit(LwjglConfig.class).getLwjglConfig());
    }

}
