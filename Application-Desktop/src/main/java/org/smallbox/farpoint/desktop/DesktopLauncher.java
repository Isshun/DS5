package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;

public class DesktopLauncher {

    public static void main (String[] arg) {
        new LwjglApplication(new GDXApplication(), DependencyInjector.getInstance().createAndInit(LwjglConfig.class).getLwjglConfig());
    }

}
