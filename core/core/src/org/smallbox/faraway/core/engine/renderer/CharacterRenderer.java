package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.MovableModel.Direction;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;

public class CharacterRenderer extends BaseRenderer {
    private SpriteManager           _spriteManager;
    private int                     _frame;
    private int                     _floor;

    private static Color    COLOR_CRITICAL = new Color(0xbb0000);
    private static Color    COLOR_WARNING = new Color(0xbbbb00);
    private static Color    COLOR_OK = new Color(0x448800);

    public CharacterRenderer() {
        _spriteManager = SpriteManager.getInstance();
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int viewPortX = viewport.getPosX();
        int viewPortY = viewport.getPosY();
        double viewPortScale = viewport.getScale();

        for (CharacterModel c : ModuleHelper.getCharacterModule().getCharacters()) {
            ParcelModel parcel = c.getParcel();
            if (parcel.z == _floor) {
                int posX = parcel.x * Constant.TILE_WIDTH + viewPortX;
                int posY = parcel.y * Constant.TILE_HEIGHT + viewPortY;

                if (c.isAlive()) {
                    // Get game position and direction
                    Direction direction = c.getDirection();
                    int frame = 0;
                    int dirIndex = 0;

                    // Get offset based on current frame
                    if (c.isAlive()) {
                        int offset = 0;
                        if (direction != Direction.NONE) {
//                    offset = (int) ((-c.getMoveProgress() + (c.getMoveStep() * animProgress)) * Constant.TILE_WIDTH);
//                        offset = -(int)(((c.getMoveStep() * (1-animProgress))) * Constant.TILE_WIDTH);
//                        if (animProgress > 1) {
//                            offset += Constant.TILE_WIDTH;
//                        }
//                        if ("paige".equals(c.getInfo().getFirstName().toLowerCase().trim())) {
////                            System.out.println("offset: " + c.getMoveProgress());
////                            System.out.println("offset: " + offset + ", animProgress: " + animProgress);
//                            System.out.println("animProgress: " + animProgress);
//                        }
//                        offset = (int) ((c.getMoveProgress()) * Constant.TILE_WIDTH);
                            frame = c.getFrameIndex() / 20 % 4;
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
                    renderer.draw(_spriteManager.getCharacter(c, dirIndex, frame), posX, posY);

                    // Draw label
                    if (c.getNeeds().get("happiness") < 20) {
                        c.getLabelDrawable().setBackgroundColor(COLOR_CRITICAL);
                    } else if (c.getNeeds().get("happiness") < 40) {
                        c.getLabelDrawable().setBackgroundColor(COLOR_WARNING);
                    } else {
                        c.getLabelDrawable().setBackgroundColor(COLOR_OK);
                    }
                    renderer.draw(c.getLabelDrawable(), posX - ((c.getLabelDrawable().getContentWidth() - 24) / 2), posY - 8);

                    // Selection
                    if (c.isSelected()) {
                        renderer.draw(_spriteManager.getSelectorCorner(0), posX + 0, posY + -4);
                        renderer.draw(_spriteManager.getSelectorCorner(1), posX + 24, posY + -4);
                        renderer.draw(_spriteManager.getSelectorCorner(2), posX + 0, posY + 28);
                        renderer.draw(_spriteManager.getSelectorCorner(3), posX + 24, posY + 28);
                    }

                    // Draw inventory
                    if (c.getInventory() != null) {
                        renderer.draw(_spriteManager.getItem(c.getInventory()), posX, posY + 2);
                    }

                    // Draw action icon
                    JobModel job = c.getJob();
                    if (!c.isSleeping() && job != null && job.getActionDrawable() != null && job.getTargetParcel() == c.getParcel()) {
                        int x = posX;
                        int y = posY;
                        ParcelModel targetParcel = job.getTargetParcel();
                        if (targetParcel != null) {
                            if (targetParcel.y < parcel.y) y -= 16;
                            if (targetParcel.y > parcel.y) y += 16;
                            if (targetParcel.x < parcel.x) x -= 16;
                            if (targetParcel.x > parcel.x) x += 16;
                        }
                        renderer.draw(job.getActionDrawable(), x, y);
                    }

                    if (c.isSleeping()) {
                        renderer.draw(c.getSleepDrawable(), posX + 16, posY - 16);
                    }
                }

                // Is dead
                else {
                    renderer.draw(_spriteManager.getIcon("[base]/res/ic_dead.png"), posX, posY);
                }
            }
        }
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }

    @Override
    public boolean isActive(GameConfig config) {
        return true;
    }

    public int getLevel() {
        return MainRenderer.CHARACTER_RENDERER_LEVEL;
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }
}