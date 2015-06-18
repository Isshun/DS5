package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.smallbox.faraway.util.Constant;
import org.smallbox.farpoint.GDXApplication;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.x = 1920;
		config.y = 0;
		config.width = Constant.WINDOW_WIDTH;
		config.height = Constant.WINDOW_HEIGHT;
		config.title = Constant.NAME + " " + Constant.VERSION;
		new LwjglApplication(new GDXApplication(), config);
	}
}
