package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;

import java.util.Map;

@GameLayer(level = LayerManager.CHARACTER_LAYER_LEVEL, visible = true)
public class CharacterLayer extends BaseLayer {

    @BindModule
    private CharacterModule _characterModule;

    @BindComponent
    private SpriteManager spriteManager;

    private int                     _floor;

    private static Color COLOR_CRITICAL = ColorUtils.fromHex(0xbb0000);
    private static Color COLOR_WARNING = ColorUtils.fromHex(0xbbbb00);
    private static Color COLOR_OK = ColorUtils.fromHex(0x448800);

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        _characterModule.getCharacters().forEach(character -> drawCharacter(renderer, viewport, character));
        _characterModule.getVisitors().forEach(visitor -> drawCharacter(renderer, viewport, visitor));
    }

    private void drawCharacter(GDXRenderer renderer, Viewport viewport, CharacterModel character) {
        int viewPortX = viewport.getPosX();
        int viewPortY = viewport.getPosY();

        PathModel path = character.getPath();
        if (path != null && path._curve != null) {
            Vector2 out = new Vector2();
            path.myCatmull.valueAt(out, (float) character.getMoveProgress2() / path.getLength());
            doDraw(renderer, character,
                    (int) (viewPortX + out.x * 32),
                    (int) (viewPortY + out.y * 32));
        } else {
            doDraw(renderer, character,
                    viewPortX + character.getParcel().x * 32,
                    viewPortY + character.getParcel().y * 32);
        }
    }

    private void doDraw(GDXRenderer renderer, CharacterModel character, int posX, int posY) {
        if (character.isAlive()) {
            drawCharacter(renderer, character, posX, posY);
            drawLabel(renderer, character, posX, posY);
            drawSelection(renderer, spriteManager, character, posX, posY, 32, 36, 0, 0);
            drawInventory(renderer, character, posX, posY);
            drawJob(renderer, character, posX, posY);
        }

        else {
            renderer.draw(posX, posY, spriteManager.getIcon("[base]/res/ic_dead.png"));
        }
    }

    /**
     * Draw job
     */
    private void drawJob(GDXRenderer renderer, CharacterModel character, int posX, int posY) {
        JobModel job = character.getJob();
        if (job != null) {

            if (job.getProgress() > 0) {
                renderer.drawRectangle(posX, posY, 32, 6, Color.CYAN, true);
                renderer.drawRectangle(posX, posY, (int) (32 * job.getProgress()), 6, Color.BLUE, true);
                renderer.drawRectangle(posX, posY, 32, 6, Color.YELLOW, false);
            }

            if (job.getMainLabel() != null) {
                renderer.drawText(posX, posY + 16, 12, com.badlogic.gdx.graphics.Color.YELLOW, job.getMainLabel());
            }
        }
    }

    /**
     * Draw label
     */
    private void drawLabel(GDXRenderer renderer, CharacterModel character, int posX, int posY) {
        renderer.drawText(posX, posY - 8, 14, com.badlogic.gdx.graphics.Color.CHARTREUSE, character.getName());
    }

    /**
     * Draw characters
     */
    private void drawCharacter(GDXRenderer renderer, CharacterModel character, int posX, int posY) {
        renderer.draw(posX, posY, spriteManager.getCharacter(character, 0, 0));
    }

    /**
     * Draw inventory
     */
    private void drawInventory(GDXRenderer renderer, CharacterModel character, int posX, int posY) {
        if (character.getInventory() != null) {
            for (Map.Entry<ItemInfo, Integer> entry: character.getInventory().entrySet()) {
                if (entry.getValue() > 0) {
                    renderer.draw(posX, posY + 2, spriteManager.getNewSprite(entry.getKey()));
                }
            }
        }
    }

    // TODO: https://github.com/libgdx/libgdx/wiki/Path-interface-%26-Splines
    private void drawPath(CharacterModel character, Viewport viewport, GDXRenderer renderer) {
        int viewPortX = viewport.getPosX();
        int viewPortY = viewport.getPosY();
        int framePerTick = Application.gameManager.getGame().getTickInterval() / (1000 / 60);
//
//        if (character.getPath().getSections().peek().p1 == character.getParcel()) {
//            character.getPath().getSections().poll();
//        }
//
        PathModel.PathSection section = character.getPath().getSections().peek();
        if (section.startTime == 0) {
            section.startTime = System.currentTimeMillis();
//            section.lastTime = _frame + (section.length * framePerTick);
        }
//
//        int posX = section.p1.x * Constant.TILE_WIDTH;
//        int posY = section.p1.y * Constant.TILE_HEIGHT;
//        double progress = Utils.progress(section.startFrame, section.lastFrame, _frame);
//        System.out.println("progress: " + progress);
//
//        double xPerFrame = section.x * 32.0 * section.length;
//        double yPerFrame = section.y * 32.0 * section.length;
//        renderer.draw(
//                (int)(viewPortX + posX + progress * xPerFrame),
//                (int)(viewPortY + posY + progress * yPerFrame),
//                spriteManager.getCharacter(character, 0, 0));

//        renderer.draw((int) (posX + _frame * 32 / character.getPath().getLength()), posY, spriteManager.getCharacter(character, 0, 0));

        PathModel path = character.getPath();

        if (path.getStartTime() == 0) {
            path.setStartTime(System.currentTimeMillis());
//            System.out.println("start index: " + path.getIndex());
        }

        int tickInterval = Application.gameManager.getGame().getTickInterval();

//        Interpolation easAlpha;
//        int lifeTime;
//        float elapsed;
//        float progress;
//        double progressInterpolation;
//        int index;
//        int length1 = 2;
//        int length2 = path.getLength() - 4;
//        int length3 = 2;
//
//        if (path.getIndex() < 2) {
//            System.out.println("P1");
//            easAlpha = Interpolation.circleIn;
//            lifeTime = length1 * tickInterval;
//            elapsed = System.currentTimeMillis() - character.getPath().getStartTime();
//            progress = Math.min(1f, elapsed / lifeTime);
//            progressInterpolation = easAlpha.apply(progress);
//            index = (int) (length1 * progressInterpolation);
//        }
//
//        else if (path.getIndex() >= 2 && path.getIndex() <= path.getLength() - 3) {
//            System.out.println("P2");
//            easAlpha = Interpolation.linear;
//            lifeTime = length2 * tickInterval;
//            elapsed = System.currentTimeMillis() - (character.getPath().getStartTime() + (tickInterval * length1));
//            progress = Math.min(1f, elapsed / lifeTime);
//            progressInterpolation = easAlpha.apply(progress);
//            index = (int) (length2 * progressInterpolation) + length1;
//        }
//
//        else {
//            System.out.println("P3");
//            easAlpha = Interpolation.circleOut;
//            lifeTime = length3 * Application.gameManager.getGame().getTickInterval();
//            elapsed = System.currentTimeMillis() - (character.getPath().getStartTime() + (tickInterval * (length1 + length2)));
//            progress = Math.min(1f, elapsed / lifeTime);
//            progressInterpolation = easAlpha.apply(progress);
//            index = (int) (length3 * progressInterpolation) + length1 + length2;
//        }

//        int lifeTime = section.length * Application.gameManager.getGame().getTickInterval();
//        float elapsed = System.currentTimeMillis() - section.startTime;

//        elapsed += delta

        Interpolation easAlpha = new Interpolation() {
            @Override
            public float apply(float a) {
                int power = 2;
                if (a <= 0.5f) return (float)Math.pow(a * 2, power) / 2;
                return (float)Math.pow((a - 1) * 2, power) / (power % 2 == 0 ? -2 : 2) + 1;
            }
        };
//        Interpolation easAlpha = Interpolation.pow2;
        long lifeTime = path.getLength() * tickInterval;
        float elapsed = System.currentTimeMillis() - character.getPath().getStartTime();
        float progress = Math.min(1f, elapsed / lifeTime);
        double progressInterpolation = easAlpha.apply(progress);
        int index = (int) (path.getLength() * progressInterpolation);

        if (index < path.getLength()) {
            double decimal = (path.getLength() * progressInterpolation) - index;
//            double decimal = 0;
//            System.out.println("index: " + index + ", progress: " + progressInterpolation);
            ParcelModel parcel = path.getNodes().get(index);

            int dirX = 0;
            int dirY = 0;
            if (index + 1 < path.getLength()) {
                ParcelModel nextParcel = path.getNodes().get(index + 1);
                dirX = nextParcel.x - parcel.x;
                dirY = nextParcel.y - parcel.y;
            }
//        if (progress >= 1) {
////        if (character.getPath().getSections().peek().p1 == character.getParcel()) {
//            character.getPath().getSections().poll();
//        }

            doDraw(renderer, character,
                    (int) (viewPortX + (parcel.x * 32) + (dirX * 32 * decimal)),
                    (int) (viewPortY + (parcel.y * 32) + (dirY * 32 * decimal))
            );
        }

        else {
            doDraw(renderer, character,
                    viewPortX + (character.getParcel().x * 32),
                    viewPortY + (character.getParcel().y * 32)
            );
        }

        //        renderer.draw(
//                (int) (viewPortX + (section.p1.x * 32) + (alpha * section.length * 32 * section.dirX)),
//                (int) (viewPortY + (section.p1.y * 32) + (alpha * section.length * 32 * section.dirY)),
//                spriteManager.getCharacter(character, 0, 0));
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }

}