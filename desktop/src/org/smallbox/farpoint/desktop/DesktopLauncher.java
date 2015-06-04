package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.farpoint.GDXApplication;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Constant.WINDOW_WIDTH;
		config.height = Constant.WINDOW_HEIGHT;
		new LwjglApplication(new GDXApplication(), config);
	}
}
