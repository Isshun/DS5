package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.ConfigChangeListener;
import org.smallbox.faraway.core.GDXApplication;
import org.smallbox.faraway.core.data.loader.ConfigLoader;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.util.Constant;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DesktopLauncher {
    public static class RoomDo {
        public int      id;
        public int      size;
        public double   pressure;
        public List<RoomConnectionDo>   connections = new ArrayList<>();

        public RoomDo(int id, int size, double pressure) {
            this.id = id;
            this.size = size;
            this.pressure = pressure;
        }
    }

    public static class RoomConnectionDo {
        public RoomDo   room1;
        public RoomDo   room2;
        public double   value;

        public RoomConnectionDo(RoomDo r1, RoomDo r2, double v) {
            this.room1 = r1;
            this.room2 = r2;
            this.value = v;
        }
    }

    private static void updateRoomPressure(RoomDo room, double pressure, double connectionValue) {
        room.pressure += (pressure - room.pressure) * connectionValue * 0.1;
    }

    private static void mixPressure(List<RoomDo> rooms) {
        rooms.forEach(room -> {
            for (RoomConnectionDo roomConnectionDo: room.connections) {
                int totalSize = room.size + roomConnectionDo.room2.size;
                double totalPressure = (room.pressure * room.size) + (roomConnectionDo.room2.pressure * roomConnectionDo.room2.size);
                updateRoomPressure(roomConnectionDo.room1, totalPressure / totalSize, roomConnectionDo.value);
                updateRoomPressure(roomConnectionDo.room2, totalPressure / totalSize, roomConnectionDo.value);
            }
        });

        rooms.forEach(room -> System.out.println("r" + room.id + ": " + room.pressure));

        double pressure = 0;
        for (RoomDo room: rooms) {
            pressure += (room.pressure * room.size);
        }
        System.out.println("Total: " + Math.round(pressure));
    }

    public static void main (String[] arg) {
        Data data = new Data();
        new ConfigLoader().load(data);

//
//        RoomDo r1 = new RoomDo(1, 100, 0.2);
//        RoomDo r2 = new RoomDo(2, 500, 0.8);
//        RoomDo r3 = new RoomDo(3, 100, 0.2);
//
//        r1.connections.add(new RoomConnectionDo(r1, r2, 0.5));
//        r2.connections.add(new RoomConnectionDo(r2, r1, 0.5));
//
//        r3.connections.add(new RoomConnectionDo(r3, r2, 1));
//        r2.connections.add(new RoomConnectionDo(r2, r3, 1));
//
//        List<RoomDo> rooms = Arrays.asList(r1, r2, r3);
//
//        rooms.forEach(room -> System.out.println("r" + room.id + ": " + room.pressure));
//        double pressure = 0;
//        for (RoomDo room: rooms) {
//            pressure += (room.pressure * room.size);
//        }
//        System.out.println("Total: " + Math.round(pressure));
//
//        for (int i = 0; i < 100; i++) {
//            mixPressure(rooms);
//        }
//
//        System.exit(1);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        System.out.println("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

        System.loadLibrary("sqlite4java-win32-x64-1.0.392");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        config.samples = 2;
        config.x = 1920 + 0;
        config.y = 0 + 0;
//        config.width = data.config.screen.resolution[0];
//        config.height = data.config.screen.resolution[1];
        config.width = width;
        config.height = height;
        config.fullscreen = false;
        config.foregroundFPS = 60;
        config.backgroundFPS = 30;
//        config.foregroundFPS = 0;
//        config.backgroundFPS = 0;
        config.resizable = false;
        config.vSyncEnabled = false;
//        config.useGL30 = true;
        config.title = Constant.NAME + " " + Constant.VERSION;
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

        Application.getInstance()._configChangeListener = new ConfigChangeListener() {
            @Override
            public void onScreeMode(String mode) {
                switch (mode) {
                    case "window":
                        config.fullscreen = false;
                        System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                        Gdx.graphics.setDisplayMode(1280, 720, false);
                        break;
                    case "borderless":
                        config.fullscreen = false;
                        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                        Gdx.graphics.setDisplayMode(width, height, true);
                        break;
                    case "fullscreen":
                        config.fullscreen = true;
                        Gdx.graphics.setDisplayMode(width, height, true);
                        break;
                }
            }
        };

        new LwjglApplication(new GDXApplication(), config);
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
//        new LwjglApplication(new TestApplication(), config);
//        new LwjglApplication(new ApplicationAdapter() {
//            public Texture _textureGround;
//            public Texture _texture;
//            public SpriteBatch _batch;
//
//            @Override
//            public void create() {
//                _batch = new SpriteBatch();
//                _texture = new Texture("data/graphics/items/resources/granite.png");
//                _textureGround = new Texture("data/graphics/items/ground.png");
//            }
//            @Override
//            public void render() {
//                Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
//                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//                Pixmap.setBlending(Pixmap.Blending.SourceOver);
//
//                Pixmap pixmap1 = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
//                _texture.getTextureData().prepare();
//                pixmap1.drawPixmap(_texture.getTextureData().consumePixmap(), 0, 0);
//                _texture.getTextureData().disposePixmap();
//
//                Pixmap pixmap2 = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
//                _textureGround.getTextureData().prepare();
//                pixmap2.drawPixmap(_textureGround.getTextureData().consumePixmap(), 0, 0);
//                _textureGround.getTextureData().disposePixmap();
//                pixmap2.drawPixmap(pixmap1, 10, 0);
//
//                _batch.begin();
//                _batch.draw(new Texture(pixmap2), 0, 0);
////                _batch.draw(new Texture(pixmap1, Pixmap.Format.RGBA8888, false), 0, 0);
//                _batch.end();
//            }
//        }, config);
    }

}
