package org.smallbox.farpoint.desktop;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GDXApplication;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.data.loader.ConfigLoader;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

import java.io.File;

public class DesktopLauncher {
    public static void main (String[] arg) {
        Data data = new Data();
        new ConfigLoader().load(data);

        System.loadLibrary("sqlite4java-win32-x64-1.0.392");

//        System.out.println(new File("sqlite4java-win32-x64-1.0.392.dll").getAbsolutePath());
//        SQLite.setLibraryPath(new File("sqlite4java-win32-x86-1.0.392.dll").getAbsolutePath());
//
//        try {
//            SQLiteConnection db = new SQLiteConnection(new File("test.db"));
//            db.open(true);
//            db.exec("CREATE TABLE parcel (x INTEGER, y INTEGER, z INTEGER, ground INTEGER)");
//            db.dispose();
//        } catch (SQLiteException e) {
//            e.printStackTrace();
//        }
//
//        System.exit(0);

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        config.samples = 2;
        config.x = 0 + 40;
        config.y = 0 + 40;
        config.width = data.config.screen.resolution[0];
        config.height = data.config.screen.resolution[1];
//        config.width = 100;
//        config.height = 80;
        config.fullscreen = false;
        config.foregroundFPS = 60;
        config.backgroundFPS = 30;
//        config.foregroundFPS = 0;
//        config.backgroundFPS = 0;
        config.resizable = true;
        config.vSyncEnabled = false;
//        config.useGL30 = true;
        config.title = Constant.NAME + " " + Constant.VERSION;
        new LwjglApplication(new GDXApplication(), config);
//        new LwjglApplication(new TestApplication(), config);

        switch (data.config.screen.mode) {
            case "window":
                config.fullscreen = false;
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                break;
            case "borderless":
                config.fullscreen = false;
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                break;
            case "fullscreen":
                config.fullscreen = true;
                break;
        }
    }
}
