package org.smallbox.faraway.client.render.layer.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.manager.input.InputManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.common.CharacterPositionCommon;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.util.Constant;

import java.util.Map;
import java.util.Optional;

@GameObject
@GameLayer(level = LayerManager.CHARACTER_LAYER_LEVEL, visible = true)
public class CharacterLayer extends BaseMapLayer {
    @Inject private AssetManager assetManager;
    @Inject private InputManager inputManager;
    @Inject private SpriteManager spriteManager;
    @Inject private CharacterModule characterModule;
    @Inject private GameManager gameManager;
    @Inject private GDXRenderer gdxRenderer;
    @Inject private Viewport viewport;
    @Inject private Game game;

    float stateTime;

    private static final Color COLOR_CRITICAL = ColorUtils.fromHex(0xbb0000ff);
    private static final Color COLOR_WARNING = ColorUtils.fromHex(0xbbbb00ff);
    private static final Color COLOR_OK = ColorUtils.fromHex(0x448800ff);

    public Animation<TextureRegion> runningAnimation;
    public Animation<TextureRegion> runningAnimation2;

    @AfterGameLayerInit
    public void init() {
//        TextureAtlas.AtlasRegion region = atlas.findRegion("hero_walk");
//        Sprite sprite = atlas.createSprite("otherimagename");
//        NinePatch patch = atlas.createPatch("patchimagename");

        {
            TextureAtlas atlas;
            atlas = new TextureAtlas(Gdx.files.internal("data/graphics/player/player.atlas"));
            atlas.getTextures().forEach(texture -> texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear));
            atlas.getRegions().forEach(region -> region.flip(false, true));
            runningAnimation = new Animation<>(0.045f, atlas.findRegions("hero_walk"), Animation.PlayMode.LOOP);
        }

        {
            TextureAtlas atlas;
            atlas = new TextureAtlas(Gdx.files.internal("data/graphics/player/ball.atlas"));
            atlas.getTextures().forEach(texture -> texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear));
            atlas.getRegions().forEach(region -> region.flip(false, true));
            runningAnimation2 = new Animation<>(0.045f, atlas.findRegions("skeleton-animation"), Animation.PlayMode.LOOP);
        }
    }

    @Override
    public void onUpdate(Object object) {

        if (object instanceof CharacterPositionCommon) {

        }

        if (object instanceof CharacterCommon) {

        }

    }

    @Override
    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        characterModule.getAll().forEach(character -> drawCharacter(renderer, viewport, character));
    }

    private void drawCharacter(GDXRendererBase renderer, Viewport viewport, CharacterModel character) {
        character.stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        if (character.getParcel().z == viewport.getFloor()) {
            int viewPortX = viewport.getPosX();
            int viewPortY = viewport.getPosY();
            CharacterPositionCommon position = character.position;

            PathModel path = character.getPath();
            if (path != null) {
                Vector2 out = new Vector2();
                path.myCatmull.valueAt(out, (float) character._moveProgress2 / position.pathLength);

                character.lastDirection = getDirection(character, position, path);

                doDraw(renderer, character,
                        (int) (viewPortX + out.x * Constant.TILE_SIZE),
                        (int) (viewPortY + out.y * Constant.TILE_SIZE), character.lastDirection);
            } else {
                doDraw(renderer, character,
                        viewPortX + character.getParcel().x * Constant.TILE_SIZE,
                        viewPortY + character.getParcel().y * Constant.TILE_SIZE, null);
            }
        }
    }

    private MovableModel.Direction getDirection(CharacterModel character, CharacterPositionCommon position, PathModel path) {
        Vector2 dout = new Vector2();
        path.myCatmull.derivativeAt(dout, (float) character._moveProgress2 / position.pathLength);

        MovableModel.Direction direction;
        if (dout.angleDeg() < 45 || dout.angleDeg() > 315) {
            direction = MovableModel.Direction.RIGHT;
        } else if (dout.angleDeg() > 135 && dout.angleDeg() < 225) {
            direction = MovableModel.Direction.LEFT;
        } else {
            direction = character.lastDirection;
        }
        return direction;
    }

    private void doDraw(GDXRendererBase renderer, CharacterModel character, int posX, int posY, MovableModel.Direction dout) {
//        if (positionCommon.isAlive()) {
        drawCharacter(renderer, character, posX, posY, dout);
        drawLabel(renderer, character, posX, posY);
        drawSelection(renderer, spriteManager, character, posX, posY, Constant.TILE_SIZE, (int) (Constant.TILE_SIZE * 1.25), 0, 0);
        drawInventory(renderer, character, posX, posY);
        drawJob(renderer, character, posX, posY);
//        }
//
//        else {
//            renderer.draw(posX, posY, spriteManager.getIcon("[base]/res/ic_dead.png"));
//        }
    }

    /**
     * Draw job
     */
    private void drawJob(GDXRendererBase renderer, CharacterModel character, int posX, int posY) {
        JobModel job = character.getJob();
        if (job != null) {

            if (job.getProgress() > 0) {
                renderer.drawRectangle(posX, posY, Constant.TILE_SIZE, 6, Color.CYAN, true);
                renderer.drawRectangle(posX, posY, (int) (Constant.TILE_SIZE * job.getProgress()), 6, Color.BLUE, true);
                renderer.drawRectangle(posX, posY, Constant.TILE_SIZE, 6, Color.YELLOW, false);
            }

            if (job.getMainLabel() != null) {
                renderer.drawText(posX, posY + 16, job.getMainLabel(), Color.YELLOW, 12);
            }
        }
    }

    /**
     * Draw label
     */
    private void drawLabel(GDXRendererBase renderer, CharacterModel character, int posX, int posY) {
        renderer.drawText(posX, posY - 8, character.getName(), Color.CHARTREUSE, 28);
    }

    /**
     * Draw characters
     */
    private void drawCharacter(GDXRendererBase renderer, CharacterModel character, int posX, int posY, MovableModel.Direction direction) {
        TextureRegion currentFrame = runningAnimation2.getKeyFrame(character.stateTime, true);

        Parcel parcelOver = WorldHelper.getParcel(viewport.getWorldPosX(inputManager.getMouseX()), viewport.getWorldPosY(inputManager.getMouseY()), viewport.getFloor());

        // TODO: direction not applied to overlay
        if (character.getPath() != null) {

            if (direction == MovableModel.Direction.LEFT && currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }

            if (direction == MovableModel.Direction.RIGHT && !currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }

            drawPath(character);
        }

        if (parcelOver == character.getParcel()) {
            drawOverlay(currentFrame, posX, posY);
        } else {
            renderer.drawTextureRegion(currentFrame, posX, posY);
        }
    }

    private void drawOverlay(TextureRegion currentFrame, int posX, int posY) {
        String key = "player-" + currentFrame.getRegionX() + "-" + currentFrame.getRegionY();

        if (!assetManager.contains(key, Texture.class)) {
            assetManager.createTextureFromPixmap(key, currentFrame.getRegionWidth(), currentFrame.getRegionHeight(), Pixmap.Format.RGBA8888, newPixmap -> {
                currentFrame.getTexture().getTextureData().prepare();
                Pixmap pixmap = currentFrame.getTexture().getTextureData().consumePixmap();
                for (int x = 0; x < currentFrame.getRegionWidth(); x++) {
                    for (int y = 0; y < currentFrame.getRegionHeight(); y++) {
                        int colorInt = pixmap.getPixel(currentFrame.getRegionX() + x, currentFrame.getRegionY() - y);
                        int r = (int) Math.min(((colorInt >> 24) & 0x000000ff) * 1.4 + 32, 255);
                        int g = (int) Math.min(((colorInt >> 16) & 0x000000ff) * 1.4 + 32, 255);
                        int b = (int) Math.min(((colorInt >> 8) & 0x000000ff) * 1.4 + 32, 255);
                        newPixmap.drawPixel(x, y, (r << 24) + (g << 16) + (b << 8) + (colorInt & 0x000000ff));
                    }
                }
                pixmap.dispose();
            });
        }

        gdxRenderer.drawSprite(new Sprite(assetManager.get(key, Texture.class)), posX, posY);
    }

    /**
     * Draw inventory
     */
    private void drawInventory(GDXRendererBase renderer, CharacterModel character, int posX, int posY) {
        if (character.hasExtra(CharacterInventoryExtra.class)) {
            for (Map.Entry<ItemInfo, Integer> entry : character.getExtra(CharacterInventoryExtra.class).getAll().entrySet()) {
                if (entry.getValue() > 0) {
                    renderer.drawSprite(spriteManager.getNewSprite(entry.getKey()), posX, posY + 2);
                }
            }
        }
    }

    int k = 100; //increase k for more fidelity to the spline
    Vector2[] points = new Vector2[k];
    boolean init = false;

    // TODO: https://github.com/libgdx/libgdx/wiki/Path-interface-%26-Splines
    private void drawPath(CharacterModel character) {

        Optional.ofNullable(character.getPath()).ifPresent(path -> {

            if (!init) {
                init = true;
                for (int i = 0; i < k; ++i) {
                    points[i] = new Vector2();
                    path.myCatmull.valueAt(points[i], ((float) i) / ((float) k - 1));
                }
            }

            for (int i = 0; i < k - 1; ++i) {
                Vector2 v1 = path.myCatmull.valueAt(points[i], ((float) i) / ((float) k - 1));
                Vector2 v2 = path.myCatmull.valueAt(points[i + 1], ((float) (i + 1)) / ((float) k - 1));

                gdxRenderer.drawLine(
                        (int) (viewport.getPosX() + v1.x * Constant.TILE_SIZE + Constant.HALF_TILE_SIZE),
                        (int) (viewport.getPosY() + v1.y * Constant.TILE_SIZE + Constant.HALF_TILE_SIZE),
                        (int) (viewport.getPosX() + v2.x * Constant.TILE_SIZE + Constant.HALF_TILE_SIZE),
                        (int) (viewport.getPosY() + v2.y * Constant.TILE_SIZE + Constant.HALF_TILE_SIZE),
                        Colors.BLUE_LIGHT_3);
            }

        });
//
//        int viewPortX = viewport.getPosX();
//        int viewPortY = viewport.getPosY();
//        int framePerTick = game.getTickInterval() / (1000 / 60);
////
////        if (character.getPath().getSections().peek().p1 == character.getParcel()) {
////            character.getPath().getSections().poll();
////        }
////
//        PathModel.PathSection section = character.getPath().getSections().peek();
//        if (section.startTime == 0) {
//            section.startTime = System.currentTimeMillis();
////            section.lastTime = _frame + (section.length * framePerTick);
//        }
////
////        int posX = section.p1.x * Constant.TILE_WIDTH;
////        int posY = section.p1.y * Constant.TILE_HEIGHT;
////        double progress = Utils.progress(section.startFrame, section.lastFrame, _frame);
////        System.out.println("progress: " + progress);
////
////        double xPerFrame = section.x * 32.0 * section.length;
////        double yPerFrame = section.y * 32.0 * section.length;
////        renderer.draw(
////                (int)(viewPortX + posX + progress * xPerFrame),
////                (int)(viewPortY + posY + progress * yPerFrame),
////                spriteManager.getCharacter(character, 0, 0));
//
////        renderer.draw((int) (posX + _frame * 32 / character.getPath().getLength()), posY, spriteManager.getCharacter(character, 0, 0));
//
//        PathModel path = character.getPath();
//
//        if (path.getStartTime() == 0) {
//            path.setStartTime(System.currentTimeMillis());
////            System.out.println("start index: " + path.getIndex());
//        }
//
//        int tickInterval = game.getTickInterval();
//
////        Interpolation easAlpha;
////        int lifeTime;
////        float elapsed;
////        float progress;
////        double progressInterpolation;
////        int index;
////        int length1 = 2;
////        int length2 = path.getLength() - 4;
////        int length3 = 2;
////
////        if (path.getIndex() < 2) {
////            System.out.println("P1");
////            easAlpha = Interpolation.circleIn;
////            lifeTime = length1 * tickInterval;
////            elapsed = System.currentTimeMillis() - character.getPath().getStartTime();
////            progress = Math.min(1f, elapsed / lifeTime);
////            progressInterpolation = easAlpha.apply(progress);
////            index = (int) (length1 * progressInterpolation);
////        }
////
////        else if (path.getIndex() >= 2 && path.getIndex() <= path.getLength() - 3) {
////            System.out.println("P2");
////            easAlpha = Interpolation.linear;
////            lifeTime = length2 * tickInterval;
////            elapsed = System.currentTimeMillis() - (character.getPath().getStartTime() + (tickInterval * length1));
////            progress = Math.min(1f, elapsed / lifeTime);
////            progressInterpolation = easAlpha.apply(progress);
////            index = (int) (length2 * progressInterpolation) + length1;
////        }
////
////        else {
////            System.out.println("P3");
////            easAlpha = Interpolation.circleOut;
////            lifeTime = length3 * Application.gameManager.getGame().getTickInterval();
////            elapsed = System.currentTimeMillis() - (character.getPath().getStartTime() + (tickInterval * (length1 + length2)));
////            progress = Math.min(1f, elapsed / lifeTime);
////            progressInterpolation = easAlpha.apply(progress);
////            index = (int) (length3 * progressInterpolation) + length1 + length2;
////        }
//
////        int lifeTime = section.length * Application.gameManager.getGame().getTickInterval();
////        float elapsed = System.currentTimeMillis() - section.startTime;
//
////        elapsed += delta
//
//        Interpolation easAlpha = new Interpolation() {
//            @Override
//            public float apply(float a) {
//                int power = 2;
//                if (a <= 0.5f) return (float) Math.pow(a * 2, power) / 2;
//                return (float) Math.pow((a - 1) * 2, power) / (power % 2 == 0 ? -2 : 2) + 1;
//            }
//        };
////        Interpolation easAlpha = Interpolation.pow2;
//        long lifeTime = path.getLength() * tickInterval;
//        float elapsed = System.currentTimeMillis() - character.getPath().getStartTime();
//        float progress = Math.min(1f, elapsed / lifeTime);
//        double progressInterpolation = easAlpha.apply(progress);
//        int index = (int) (path.getLength() * progressInterpolation);
//
//        if (index < path.getLength()) {
//            double decimal = (path.getLength() * progressInterpolation) - index;
////            double decimal = 0;
////            System.out.println("index: " + index + ", progress: " + progressInterpolation);
//            ParcelModel parcel = path.getNodes().get(index);
//
//            int dirX = 0;
//            int dirY = 0;
//            if (index + 1 < path.getLength()) {
//                ParcelModel nextParcel = path.getNodes().get(index + 1);
//                dirX = nextParcel.x - parcel.x;
//                dirY = nextParcel.y - parcel.y;
//            }
////        if (progress >= 1) {
//////        if (character.getPath().getSections().peek().p1 == character.getParcel()) {
////            character.getPath().getSections().poll();
////        }
//
//            doDraw(renderer, character,
//                    (int) (viewPortX + (parcel.x * Constant.TILE_SIZE) + (dirX * Constant.TILE_SIZE * decimal)),
//                    (int) (viewPortY + (parcel.y * Constant.TILE_SIZE) + (dirY * Constant.TILE_SIZE * decimal))
//            );
//        } else {
//            doDraw(renderer, character,
//                    viewPortX + (character.getParcel().x * Constant.TILE_SIZE),
//                    viewPortY + (character.getParcel().y * Constant.TILE_SIZE)
//            );
//        }
//
//        //        renderer.draw(
////                (int) (viewPortX + (section.p1.x * 32) + (alpha * section.length * 32 * section.dirX)),
////                (int) (viewPortY + (section.p1.y * 32) + (alpha * section.length * 32 * section.dirY)),
////                spriteManager.getCharacter(character, 0, 0));
    }

}