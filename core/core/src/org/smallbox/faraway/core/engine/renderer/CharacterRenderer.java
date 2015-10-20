package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.MovableModel.Direction;
import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.item.ParcelModel;
import org.smallbox.faraway.core.game.model.job.ConsumeJob;
import org.smallbox.faraway.core.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class CharacterRenderer extends BaseRenderer {
    private List<CharacterModel>            _characters;
    private SpriteManager                   _spriteManager;
    private int                             _frame;

    private static Color  COLOR_CRITICAL = new Color(0xbb0000);
    private static Color  COLOR_WARNING = new Color(0xbbbb00);
    private static Color COLOR_OK = new Color(0x448800);
//    private static com.badlogic.gdx.graphics.Color  COLOR_CRITICAL = new com.badlogic.gdx.graphics.Color(0.8f, 0.2f, 0.3f, 1f);
//    private static com.badlogic.gdx.graphics.Color  COLOR_WARNING = new com.badlogic.gdx.graphics.Color(0.8f, 0.7f, 0.3f, 1f);
//    private static com.badlogic.gdx.graphics.Color  COLOR_OK = new com.badlogic.gdx.graphics.Color(0.2f, 0.8f, 0.7f, 1f);

    public CharacterRenderer() {
        _characters = new ArrayList<>();
        _spriteManager = SpriteManager.getInstance();
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int viewPortX = viewport.getPosX();
        int viewPortY = viewport.getPosY();
        double viewPortScale = viewport.getScale();

        for (CharacterModel c : _characters) {
            ParcelModel parcel = c.getParcel();
            int posX = c.getX() * Constant.TILE_WIDTH + viewPortX;
            int posY = c.getY() * Constant.TILE_HEIGHT + viewPortY;

            if (c.isAlive()) {
                // Get game position and direction
                Direction direction = c.getDirection();
                Direction move = c.getMove();
                int frame = 0;
                int dirIndex = 0;

                // Get offset based on current frame
                if (c.isAlive()) {
                    int offset = 0;
                    if (move != Direction.NONE) {
//                    offset = (int) ((c.getMoveProgress() + (c.getMoveStep() * animProgress)) * Constant.TILE_WIDTH);
//                    if ("rhea".equals(c.getInfo().getFirstName().toLowerCase().trim())) {
//                        Log.notice("offset: " + offset);
//                    }
//                offset = (int) ((c.getMoveProgress()) * Constant.TILE_WIDTH);
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
                if (c.getNeeds().happiness < 20) {
                    c.getLabelDrawable().setBackgroundColor(COLOR_CRITICAL);
                } else if (c.getNeeds().happiness < 40) {
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

                if (c.getInventory() != null) {
                    renderer.draw(_spriteManager.getItem(c.getInventory()), posX, posY);
                }

                // Draw consume job
                if (!c.isSleeping() && c.getJob() != null && c.getJob() instanceof ConsumeJob && c.getJob().getTargetParcel() == c.getParcel()) {
                    renderer.draw(_spriteManager.getItem(((ConsumeJob) c.getJob()).getConsumable()), posX + 2, posY + 8);
                }

                // Draw action icon
                if (!c.isSleeping() && c.getJob() != null && c.getJob().getActionDrawable() != null && c.getJob().getTargetParcel() == c.getParcel()) {
                    int x = posX;
                    int y = posY;
                    ParcelModel actionParcel = c.getJob().getActionParcel();
                    if (actionParcel != null) {
                        if (actionParcel.y < parcel.y) y -= 16;
                        if (actionParcel.y > parcel.y) y += 16;
                        if (actionParcel.x < parcel.x) x -= 16;
                        if (actionParcel.x > parcel.x) x += 16;
                    }
                    ((GDXRenderer) renderer).draw(c.getJob().getActionDrawable(), x, y);
                }

                if (c.isSleeping()) {
                    ((GDXRenderer) renderer).draw(c.getSleepDrawable(), posX + 16, posY - 16);
                }
            }

            // Is dead
            else {
                renderer.draw(_spriteManager.getIcon("data/res/ic_dead.png"), posX, posY);
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
        return 100;
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        _characters.add(character);
    }

}