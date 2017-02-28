package org.smallbox.faraway.client.renderer;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.MovableModel.Direction;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.util.Constant;

import java.util.Map;

@GameRenderer(level = MainRenderer.CHARACTER_RENDERER_LEVEL, visible = true)
public class CharacterRenderer extends BaseRenderer {

    @BindModule
    private CharacterModule _characterModule;

    @BindComponent
    private SpriteManager           _spriteManager;

    private int                     _frame;
    private int                     _floor;

    private static Color    COLOR_CRITICAL = new Color(0xbb0000);
    private static Color    COLOR_WARNING = new Color(0xbbbb00);
    private static Color    COLOR_OK = new Color(0x448800);

    private long _lastUpdate;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
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

        ParcelModel parcel = character.getParcel();
        if (parcel.z == _floor) {
            int posX = parcel.x * Constant.TILE_WIDTH + viewPortX;
            int posY = parcel.y * Constant.TILE_HEIGHT + viewPortY;

            if (character.isAlive()) {
                // Get game position and direction
                Direction direction = character.getDirection();
                int frame = 0;
                int dirIndex = 0;

                // Get offset based on current frame
                if (character.isAlive()) {
                    int offset = 0;
                    if (direction != Direction.NONE) {
                        offset = (int)((System.currentTimeMillis() - _lastUpdate) * Constant.TILE_WIDTH / Application.APPLICATION_CONFIG.game.updateInterval);
                        frame = character.getFrameIndex() / 20 % 4;
                    }

                    // Get exact position
                    switch (direction) {
                        case BOTTOM:
                            posY += offset;
                            dirIndex = 0;
                            break;
                        case LEFT:
                            posX -= offset;
                            dirIndex = 1;
                            break;
                        case RIGHT:
                            posX += offset;
                            dirIndex = 2;
                            break;
                        case TOP:
                            posY -= offset;
                            dirIndex = 3;
                            break;
                        case TOP_LEFT:
                            posY -= offset;
                            posX -= offset;
                            dirIndex = 1;
                            break;
                        case TOP_RIGHT:
                            posY -= offset;
                            posX += offset;
                            dirIndex = 2;
                            break;
                        case BOTTOM_LEFT:
                            posY += offset;
                            posX -= offset;
                            dirIndex = 1;
                            break;
                        case BOTTOM_RIGHT:
                            posY += offset;
                            posX += offset;
                            dirIndex = 2;
                            break;
                        default:
                            break;
                    }
                }

                // Draw characters
                renderer.draw(posX, posY, _spriteManager.getCharacter(character, dirIndex, frame));

                // TODO
//                // Draw label
//                if (c.getNeeds().get("happiness") < 20) {
//                    c.getLabelDrawable().setBackgroundColor(COLOR_CRITICAL);
//                } else if (c.getNeeds().get("happiness") < 40) {
//                    c.getLabelDrawable().setBackgroundColor(COLOR_WARNING);
//                } else {
//                    c.getLabelDrawable().setBackgroundColor(COLOR_OK);
//                }
//                renderer.drawPixel(c.getLabelDrawable(), posX - ((c.getLabelDrawable().getContentWidth() - 24) / 2), posY - 8);

                // Selection
                if (character.isSelected()) {
                    renderer.draw(posX, posY + -4, _spriteManager.getSelectorCorner(0));
                    renderer.draw(posX + 24, posY + -4, _spriteManager.getSelectorCorner(1));
                    renderer.draw(posX, posY + 28, _spriteManager.getSelectorCorner(2));
                    renderer.draw(posX + 24, posY + 28, _spriteManager.getSelectorCorner(3));
                }

                // Draw inventory
                if (character.getInventory() != null) {
                    renderer.draw(posX, posY + 2, _spriteManager.getItem(character.getInventory()));
                }

                // Draw inventory 2
                if (character.getInventory2() != null) {
                    for (Map.Entry<ItemInfo, Integer> entry: character.getInventory2().entrySet()) {
                        if (entry.getValue() > 0) {
                            renderer.draw(posX, posY + 2, _spriteManager.getIcon(entry.getKey()));
                        }
                    }
                }

// TODO
                //                // Draw action icon
//                JobModel job = c.getJob();
//                if (!c.isSleeping() && job != null && job.getActionDrawable() != null && job.getTargetParcel() == c.getParcel()) {
//                    int x = posX;
//                    int y = posY;
//                    ParcelModel targetParcel = job.getTargetParcel();
//                    if (targetParcel != null) {
//                        if (targetParcel.y < parcel.y) y -= 16;
//                        if (targetParcel.y > parcel.y) y += 16;
//                        if (targetParcel.x < parcel.x) x -= 16;
//                        if (targetParcel.x > parcel.x) x += 16;
//                    }
//                    renderer.drawPixel(job.getActionDrawable(), x, y);
//                }
//
//                if (c.isSleeping()) {
//                    renderer.drawPixel(c.getSleepDrawable(), posX + 16, posY - 16);
//                }
            }

            // Is dead
            else {
                renderer.draw(posX, posY, _spriteManager.getIcon("[base]/res/ic_dead.png"));
            }
        }
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }
}