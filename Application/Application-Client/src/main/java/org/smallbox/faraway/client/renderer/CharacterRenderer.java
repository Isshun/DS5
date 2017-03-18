package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.math.Interpolation;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.MovableModel.Direction;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.util.Constant;

import java.util.Map;

@GameRenderer(level = MainRenderer.CHARACTER_RENDERER_LEVEL, visible = true)
public class CharacterRenderer extends BaseRenderer {

    @BindModule
    private CharacterModule _characterModule;

    @BindComponent
    private SpriteManager _spriteManager;

    private int                     _floor;

    private static Color    COLOR_CRITICAL = new Color(0xbb0000);
    private static Color    COLOR_WARNING = new Color(0xbbbb00);
    private static Color    COLOR_OK = new Color(0x448800);

    private long _lastUpdate;
    private double _value;
    private long _startTime;

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        _characterModule.getCharacters().forEach(character -> drawCharacter(renderer, viewport, character));
        _characterModule.getVisitors().forEach(visitor -> drawCharacter(renderer, viewport, visitor));
    }

    @Override
    public void onGameUpdate(Game game) {
        _lastUpdate = System.currentTimeMillis();
    }

    private void drawCharacter(GDXRenderer renderer, Viewport viewport, CharacterModel character) {
        int viewPortX = viewport.getPosX();
        int viewPortY = viewport.getPosY();
        double viewPortScale = viewport.getScale();

        if (character.getPath() != null) {
            drawPath(character, viewport, renderer);
            return;
        }

        ParcelModel parcel = character.getParcel();
        if (parcel.z == _floor) {
            int posX = parcel.x * Constant.TILE_WIDTH + viewPortX;
            int posY = parcel.y * Constant.TILE_HEIGHT + viewPortY;

            if (character.isAlive()) {
                // Get game position and direction
                Direction direction = character.getDirection();

                double rest = Application.gameManager.getGame().getNextTick() - System.currentTimeMillis();
                double ratio = 1.0 - rest / Application.gameManager.getGame().getTickInterval();
//                System.out.println("ratio: " + ratio);

                // Get exact position
//                switch (direction) {
//                    case BOTTOM:
//                        posY += 32 * ratio;
//                        break;
//                    case LEFT:
//                        posX -= 32 * ratio;
//                        break;
//                    case RIGHT:
//                        posX += 32 * ratio;
//                        break;
//                    case TOP:
//                        posY -= 32 * ratio;
//                        break;
//                    case TOP_LEFT:
//                        posY -= 32 * ratio;
//                        posX -= 32 * ratio;
//                        break;
//                    case TOP_RIGHT:
//                        posY -= 32 * ratio;
//                        posX += 32 * ratio;
//                        break;
//                    case BOTTOM_LEFT:
//                        posY += 32 * ratio;
//                        posX -= 32 * ratio;
//                        break;
//                    case BOTTOM_RIGHT:
//                        posY += 32 * ratio;
//                        posX += 32 * ratio;
//                        break;
//                    default:
//                        break;
//                }
//                }

                doDraw(renderer, character, posX, posY);
            }

        }
    }

    private void doDraw(GDXRenderer renderer, CharacterModel character, int posX, int posY) {
        if (character.isAlive()) {
            drawCharacter(renderer, character, posX, posY);
            drawLabel(renderer, character, posX, posY);
            drawSelection(renderer, character, posX, posY);
            drawInventory(renderer, character, posX, posY);
            drawJob(renderer, character, posX, posY);
        }

        else {
            renderer.draw(posX, posY, _spriteManager.getIcon("[base]/res/ic_dead.png"));
        }
    }

    /**
     * Draw job
     */
    private void drawJob(GDXRenderer renderer, CharacterModel character, int posX, int posY) {
        JobModel job = character.getJob();
        if (job != null && job.getProgress() > 0) {
            renderer.drawRectangle(posX, posY, 32, 6, Color.CYAN, true);
            renderer.drawRectangle(posX, posY, (int) (32 * job.getProgress()), 6, Color.BLUE, true);
            renderer.drawRectangle(posX, posY, 32, 6, Color.YELLOW, false);
//            int x = posX;
//            int y = posY;
//            ParcelModel targetParcel = job.getTargetParcel();
//            if (targetParcel != null) {
//                if (targetParcel.y < parcel.y) y -= 16;
//                if (targetParcel.y > parcel.y) y += 16;
//                if (targetParcel.x < parcel.x) x -= 16;
//                if (targetParcel.x > parcel.x) x += 16;
//            }
//            renderer.drawPixel(job.getActionDrawable(), x, y);
        }

//        if (character.isSleeping()) {
//            renderer.drawPixel(character.getSleepDrawable(), posX + 16, posY - 16);
//        }
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
        renderer.draw(posX, posY, _spriteManager.getCharacter(character, 0, 0));
    }

    /**
     * Draw selection
     */
    private void drawSelection(GDXRenderer renderer, CharacterModel character, int posX, int posY) {
        if (character.isSelected()) {
            renderer.draw(posX, posY + -4, _spriteManager.getSelectorCorner(0));
            renderer.draw(posX + 24, posY + -4, _spriteManager.getSelectorCorner(1));
            renderer.draw(posX, posY + 28, _spriteManager.getSelectorCorner(2));
            renderer.draw(posX + 24, posY + 28, _spriteManager.getSelectorCorner(3));
        }
    }

    /**
     * Draw inventory
     */
    private void drawInventory(GDXRenderer renderer, CharacterModel character, int posX, int posY) {
        if (character.getInventory2() != null) {
            for (Map.Entry<ItemInfo, Integer> entry: character.getInventory2().entrySet()) {
                if (entry.getValue() > 0) {
                    renderer.draw(posX, posY + 2, _spriteManager.getNewSprite(entry.getKey()));
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
//                _spriteManager.getCharacter(character, 0, 0));

//        renderer.draw((int) (posX + _frame * 32 / character.getPath().getLength()), posY, _spriteManager.getCharacter(character, 0, 0));

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
//                _spriteManager.getCharacter(character, 0, 0));
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }

}