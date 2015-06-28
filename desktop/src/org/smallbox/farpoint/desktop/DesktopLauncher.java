package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.smallbox.faraway.data.loader.ConfigLoader;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.util.Constant;
import org.smallbox.farpoint.GDXApplication;

public class DesktopLauncher {
	public static void main (String[] arg) {
		GameData data = new GameData();
		new ConfigLoader().load(data);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 2;
		config.x = 1920;
		config.y = 0;
		config.width = data.config.resolution[0];
		config.height = data.config.resolution[1];
		config.title = Constant.NAME + " " + Constant.VERSION;
		new LwjglApplication(new GDXApplication(), config);
	}
}
