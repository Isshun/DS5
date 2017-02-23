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
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

@GameRenderer(level = MainRenderer.CHARACTER_RENDERER_LEVEL)
public class BuildRenderer extends BaseRenderer {

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