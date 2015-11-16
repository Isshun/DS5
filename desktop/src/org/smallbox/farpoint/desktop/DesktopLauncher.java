package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.smallbox.faraway.core.GDXApplication;
import org.smallbox.faraway.core.data.loader.ConfigLoader;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.util.Constant;

public class DesktopLauncher {
    public static void main (String[] arg) {
        Data data = new Data();
        new ConfigLoader().load(data);

        System.loadLibrary("sqlite4java-win32-x64-1.0.392");

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

//        switch (data.config.screen.mode) {
//            case "window":
//                config.fullscreen = false;
//                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
//                break;
//            case "borderless":
//                config.fullscreen = false;
//                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
//                break;
//            case "fullscreen":
//                config.fullscreen = true;
//                break;
//        }
    }
}
