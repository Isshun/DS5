package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.client.controller.BuildController;
import org.smallbox.faraway.client.manager.InputManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Constant;

@GameRenderer(level = MainRenderer.CHARACTER_RENDERER_LEVEL)
public class BuildRenderer extends BaseRenderer {

//    @BindModule
//    private WorldModule worldModule;
//
    @BindLuaController
    private BuildController buildController;

    private int                     _frame;
    private int                     _floor;
    private ItemInfo                _cursorItem;
    private int                     _cursorParcelX;
    private int                     _cursorParcelY;

    private static Color COLOR_CRITICAL = new Color(0xbb0000);
    private static Color    COLOR_WARNING = new Color(0xbbbb00);
    private static Color    COLOR_OK = new Color(0x448800);

    private UIFrame resEden;
    private UIFrame resEden2;

    {
        resEden = new UIFrame(null);
        resEden.setSize(32, 32);
        resEden.setBackgroundColor(Color.BLUE);

        resEden2 = new UIFrame(null);
        resEden2.setSize(32, 32);
        resEden2.setBackgroundColor(Color.RED);
    }

    @Override
    public void onGameStart(Game game) {
//        worldModule.addObserver(new WorldModuleObserver() {
//            @Override
//            public void onMouseMove(GameEvent event, int parcelX, int parcelY, int floor) {
//                setCursor(_buildController.getCurrentItem(), parcelX, parcelY);
//            }
//
//            @Override
//            public void onMouseRelease(GameEvent event, int parcelX, int parcelY, int floor, GameEventListener.MouseButton button) {
//                if (button == GameEventListener.MouseButton.RIGHT) {
//                    _buildController.setCurrentItem(null);
//                    dismissCursor();
//                }
//            }
//        });
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        if (buildController != null && buildController.getCurrentItem() != null) {

            if (ApplicationClient.inputManager.getTouchDrag()) {
                InputManager inputManager = ApplicationClient.inputManager;
                WorldHelper.getParcelInRect(
                        viewport.getWorldPosX(inputManager.getTouchDownX()),
                        viewport.getWorldPosY(inputManager.getTouchDownY()),
                        viewport.getWorldPosX(inputManager.getTouchDragX()),
                        viewport.getWorldPosY(inputManager.getTouchDragY()),
                        viewport.getFloor())
                .forEach(parcel -> renderer.draw(viewport.getScreenPosX(parcel.x), viewport.getScreenPosY(parcel.y), resEden2));
            } else {
                renderer.draw(ApplicationClient.inputManager.getMouseX(), ApplicationClient.inputManager.getMouseY(), resEden);

                Sprite sprite = ApplicationClient.spriteManager.getIcon(buildController.getCurrentItem());
                renderer.draw(ApplicationClient.inputManager.getMouseX(), ApplicationClient.inputManager.getMouseY(), sprite);
            }
        }


//        if (_cursorItem != null) {
////            renderer.draw(Color.BLUE, _cursorParcelX + viewport.getPosX(), _cursorParcelY + viewport.getPosY(), 320, 320);
//
//            final UIFrame resEden = new UIFrame(null);
//            resEden.setSize(32, 32);
//            resEden.setBackgroundColor(Color.BLUE);
//
//            int startX = Math.max(_cursorParcelX, 0);
//            int startY = Math.max(_cursorParcelY, 0);
//            int toX = Math.min(_cursorParcelX, Application.gameManager.getGame().getInfo().worldWidth);
//            int toY = Math.min(_cursorParcelY, Application.gameManager.getGame().getInfo().worldHeight);
//
//        for (int x = startX; x <= toX; x++) {
//            for (int y = startY; y <= toY; y++) {
////                Log.info("Draw cursor: %d x %d", x * 32 + viewport.getPosX(), y * 32 + viewport.getPosY());
//
//                renderer.draw(resEden, x * 32 + viewport.getPosX(), y * 32 + viewport.getPosY());
//
////                onDraw(renderer, ModuleHelper.getWorldModule().getParcel(x, y, WorldHelper.getCurrentFloor()), x * 32 + viewport.getPosX(), y * 32 + viewport.getPosY(), (x + y) % 2 == 0, isPressed);
//            }
//        }

//            renderer.draw(resEden, _cursorParcelX - viewport.getPosX(), _cursorParcelY - viewport.getPosY());
//            renderer.draw(resEden, 500, 500);
//            onDraw(renderer,
//                    worldModule.getParcel(_cursorParcelX, _cursorParcelY, WorldHelper.getCurrentFloor()),
//                    _cursorParcelX + viewport.getPosX(),
//                    _cursorParcelY * 32 + viewport.getPosY(),
//                    false,
//                    false);
//        }
    }

    private void drawCharacter(GDXRenderer renderer, Viewport viewport, CharacterModel c) {
        int viewPortX = viewport.getPosX();
        int viewPortY = viewport.getPosY();
        double viewPortScale = viewport.getScale();

        ParcelModel parcel = c.getParcel();
        if (parcel.z == _floor) {
            int posX = parcel.x * Constant.TILE_WIDTH + viewPortX;
            int posY = parcel.y * Constant.TILE_HEIGHT + viewPortY;

            if (c.isAlive()) {
                // Get game position and direction
                MovableModel.Direction direction = c.getDirection();
                int frame = 0;
                int dirIndex = 0;

                // Get offset based on current frame
                if (c.isAlive()) {
                    int offset = 0;
                    if (direction != MovableModel.Direction.NONE) {
//                    offset = (int) ((-c.getMoveProgress() + (c.getMoveStep() * animProgress)) * Constant.TILE_WIDTH);
//                        offset = -(int)(((c.getMoveStep() * (1-animProgress))) * Constant.TILE_WIDTH);
//                        if (animProgress > 1) {
//                            offset += Constant.TILE_WIDTH;
//                        }
//                        if ("paige".equals(c.getInfo().getFirstName().toLowerCase().trim())) {
////                            Log.info("offset: " + c.getMoveProgress());
////                            Log.info("offset: " + offset + ", animProgress: " + animProgress);
//                            Log.info("animProgress: " + animProgress);
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
                renderer.draw(posX, posY, ApplicationClient.spriteManager.getCharacter(c, dirIndex, frame));

//                // TODO
//                // Draw label
//                if (c.getNeeds().get("happiness") < 20) {
//                    c.getLabelDrawable().setBackgroundColor(COLOR_CRITICAL);
//                } else if (c.getNeeds().get("happiness") < 40) {
//                    c.getLabelDrawable().setBackgroundColor(COLOR_WARNING);
//                } else {
//                    c.getLabelDrawable().setBackgroundColor(COLOR_OK);
//                }
//                renderer.draw(c.getLabelDrawable(), posX - ((c.getLabelDrawable().getContentWidth() - 24) / 2), posY - 8);

                // Selection
                if (c.isSelected()) {
                    renderer.draw(posX + 0, posY + -4, ApplicationClient.spriteManager.getSelectorCorner(0));
                    renderer.draw(posX + 24, posY + -4, ApplicationClient.spriteManager.getSelectorCorner(1));
                    renderer.draw(posX + 0, posY + 28, ApplicationClient.spriteManager.getSelectorCorner(2));
                    renderer.draw(posX + 24, posY + 28, ApplicationClient.spriteManager.getSelectorCorner(3));
                }

                // Draw inventory
                if (c.getInventory() != null) {
                    renderer.draw(posX, posY + 2, ApplicationClient.spriteManager.getItem(c.getInventory()));
                }

//                // TODO
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
//                    renderer.draw(job.getActionDrawable(), x, y);
//                }

//                TODO
//                if (c.isSleeping()) {
//                    renderer.draw(c.getSleepDrawable(), posX + 16, posY - 16);
//                }
            }

            // Is dead
            else {
                renderer.draw(posX, posY, ApplicationClient.spriteManager.getIcon("[base]/res/ic_dead.png"));
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

    public void setCursor(ItemInfo itemInfo, int parcelX, int parcelY) {
        _cursorItem = itemInfo;
        _cursorParcelX = parcelX;
        _cursorParcelY = parcelY;
    }

    public void dismissCursor() {
        _cursorItem = null;
    }
}