package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import org.smallbox.faraway.common.util.Log;

/**
 * Created by Alex on 09/11/2015.
 */
public class TestApplication extends ApplicationAdapter {
    public Sprite[]             sprites;
    public Sprite               sprite;
    public Texture groundTexture;
    private TextureRegion[]     textureRegion;
    private FPSLogger logger;
    private SpriteBatch batch;
    private SpriteCache cache;
    private Texture             texture;
    private int                 cacheId;

    @Override
    public void create () {
        logger = new FPSLogger();
        batch = new SpriteBatch();
        cache = new SpriteCache(5000, true);

//                TexturePacker.Settings settings = new TexturePacker.Settings();
//                settings.paddingX = 0;
//                settings.paddingY = 0;
//                settings.maxWidth = 512;
//                settings.maxHeight = 512;
//                TexturePacker.process(settings, "data/graphics/items", "data/assets", "items");

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/assets/items.atlas"));
//                region.setRegion(0, 0, 32, 32);
//                Sprite sprite = atlas.createSprite("bed");
        sprites = new Sprite[20];
        sprites[0] = new Sprite(atlas.findRegion("chair"), 0, 0, 32, 32);
        sprites[1] = new Sprite(atlas.findRegion("consumables/brick_rock"), 0, 0, 32, 32);
        sprites[2] = new Sprite(atlas.findRegion("consumables/copper_ore"), 0, 0, 32, 32);
        sprites[3] = new Sprite(atlas.findRegion("consumables/copper_plate"), 0, 0, 32, 32);
        sprites[4] = new Sprite(atlas.findRegion("consumables/easy_meal"), 0, 0, 32, 32);
        sprites[5] = new Sprite(atlas.findRegion("consumables/great_meal"), 0, 0, 32, 32);
        sprites[6] = new Sprite(atlas.findRegion("consumables/iron_filings"), 0, 0, 32, 32);
        sprites[7] = new Sprite(atlas.findRegion("consumables/iron_ore"), 0, 0, 32, 32);
        sprites[8] = new Sprite(atlas.findRegion("consumables/iron_plate"), 0, 0, 32, 32);
        sprites[9] = new Sprite(atlas.findRegion("consumables/organic_vegetable"), 0, 0, 32, 32);
        sprites[10] = new Sprite(atlas.findRegion("consumables/rubble"), 0, 0, 32, 32);
        sprites[11] = new Sprite(atlas.findRegion("consumables/seafood"), 0, 0, 32, 32);
        sprites[12] = new Sprite(atlas.findRegion("consumables/seaweed"), 0, 0, 32, 32);
        sprites[13] = new Sprite(atlas.findRegion("consumables/spice"), 0, 0, 32, 32);
        sprites[14] = new Sprite(atlas.findRegion("consumables/vegetable"), 0, 0, 32, 32);
        sprites[15] = new Sprite(atlas.findRegion("consumables/wood"), 0, 0, 32, 32);
        sprites[16] = new Sprite(atlas.findRegion("consumables/wood_log"), 0, 0, 32, 32);
        sprites[17] = new Sprite(atlas.findRegion("consumables/wood_log"), 32, 0, 32, 32);
        sprites[18] = new Sprite(atlas.findRegion("consumables/wood_log"), 64, 0, 32, 32);
        sprites[19] = new Sprite(atlas.findRegion("consumables/wood_log"), 96, 0, 32, 32);
//                sprite.setRegion(new TextureRegion(new Texture(Gdx.files.internal("data/graphics/items/chair.png"))), 0, 0, 32, 32);
//                NinePatch patch = atlas.createPatch("bed");

//                Texture groundTexture;
        {
            FileHandle file = Gdx.files.internal("data/graphics/items/ground.png");
//                    TextureAtlas.AtlasRegion groundRegion = atlas.findRegion("ground");
            Texture groundItemTexture = new Texture(file);
            groundItemTexture.getTextureData().prepare();
            Pixmap groundPixmap = groundItemTexture.getTextureData().consumePixmap();

            Pixmap pixmap = new Pixmap(1000, 1000, Pixmap.Format.RGB888);
            for (int x = 0; x < 60; x++) {
                for (int y = 0; y < 40; y++) {
                    pixmap.drawPixmap(groundPixmap, x * 32, y * 32, 0, 32, 32, 32);
                }
            }

            groundPixmap.dispose();
            groundTexture = new Texture(pixmap);
        }

//                textureRegion = new TextureRegion[100];
//                FileHandle file = Gdx.files.internal("data/graphics/planets/arrakis_bg.jpg");
//                texture = new Texture(file);
////                FileHandle file = Gdx.files.internal("data/chess.png");
//                for (int i = 0; i < 100; i++) {
//                    textureRegion[i] = new TextureRegion(texture, 32 * (i/2), 32 * (i/2));
////                    textureRegion[i] = new TextureRegion(new Texture(file), 32 * (i/2), 32 * (i/2));
//                }

        Gdx.gl.glEnable(GL20.GL_BLEND);

        cache.beginCache();
        cache.add(groundTexture, 0, 0);
//                cache.addSubJob(sprite, 32, 32);

        int i = 0;
        for (int x = 0; x < 60; x++) {
            for (int y = 0; y < 40; y++) {
                i++;
//                        cache.addSubJob(texture);
                Log.info("cache " + i);
//                        cache.addSubJob(sprite, x * 32, y * 32);
//                        cache.addSubJob(textureRegion[i % 20], x * 32, y * 32);
//                        batch.drawPixel(textureRegion[x*y % 10], x * 32, y * 32);
//                        batch.drawPixel(textureRegion[(int)(Math.random() * 100)], x * 32, y * 32);
            }
        }
        cacheId = cache.endCache();

        GLProfiler.enable();
    }

    @Override
    public void render () {
        GLProfiler.reset();

        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);

//                Gdx.gl.glEnable(GL20.GL_BLEND);

        batch.begin();

        batch.draw(groundTexture, 0, 0, 1000, 1000);

        for (int x = 0; x < 60; x++) {
            for (int y = 0; y < 40; y++) {
//                        batch.drawPixel(textureRegion[x*y % 10], x * 32, y * 32);
                batch.draw(sprites[x * y % 20], x * 32, y * 32);
//                        batch.drawPixel(textureRegion[(int)(Math.random() * 100)], x * 32, y * 32);
            }
        }
//                cache.begin();
//                Gdx.gl.glEnable(GL20.GL_BLEND);
//                cache.drawPixel(cacheId);
//                cache.end();

//                batch.drawPixel(sprite, 32, 32);

        batch.end();

        logger.log();

//                Log.info("bindings: " + GLProfiler.textureBindings);
//                Log.info("drawPixel calls: " + GLProfiler.drawCalls);
//                Log.info("calls: " + GLProfiler.calls);
//                Log.info("shader switches: " + GLProfiler.shaderSwitches);
//                Log.info("vertices: " + GLProfiler.vertexCount.average);
    }
}
