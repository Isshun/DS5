package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.selection.SelectionManager;
import org.smallbox.faraway.client.controller.BuildController;
import org.smallbox.faraway.client.manager.InputManager;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

@GameObject
@GameLayer(level = LayerManager.CHARACTER_LAYER_LEVEL, visible = true)
public class BuildLayer extends BaseLayer {

    @Inject
    private BuildController buildController;

    @Inject
    private InputManager inputManager;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private SelectionManager selectionManager;

    private int                     _frame;
    private int                     _floor;
    private ItemInfo                _cursorItem;
    private int                     _cursorParcelX;
    private int                     _cursorParcelY;

    private static Color COLOR_CRITICAL = ColorUtils.fromHex(0xbb0000ff);
    private static Color COLOR_WARNING = ColorUtils.fromHex(0xbbbb00ff);
    private static Color COLOR_OK = ColorUtils.fromHex(0x448800ff);

    private UIFrame resEden;
    private UIFrame resEden2;
    private ItemInfo _itemInfo;

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

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {

        if (selectionManager.getSelectionListener() != null) {

            if (_itemInfo != null) {

                if (_mouseDownX != -1 && _mouseDownY != -1) {
                    int fromMapX = Math.min(viewport.getWorldPosX(_mouseX), viewport.getWorldPosX(_mouseDownX));
                    int fromMapY = Math.min(viewport.getWorldPosY(_mouseY), viewport.getWorldPosY(_mouseDownY));
                    int toMapX = Math.max(viewport.getWorldPosX(_mouseX), viewport.getWorldPosX(_mouseDownX));
                    int toMapY = Math.max(viewport.getWorldPosY(_mouseY), viewport.getWorldPosY(_mouseDownY));
                    for (int mapX = fromMapX; mapX <= toMapX; mapX++) {
                        for (int mapY = fromMapY; mapY <= toMapY; mapY++) {
                            renderer.drawRectangleOnMap(mapX, mapY, 32, 32, com.badlogic.gdx.graphics.Color.BROWN, true, 0, 0);
                        }
                    }
                } else {
                    renderer.drawRectangleOnMap(viewport.getWorldPosX(_mouseX), viewport.getWorldPosY(_mouseY), 32, 32, com.badlogic.gdx.graphics.Color.BROWN, true, 0, 0);
                }

                renderer.drawText(_mouseX - 20, _mouseY - 20, 16, com.badlogic.gdx.graphics.Color.CHARTREUSE, "Build " + _itemInfo.label);
            }

            if (buildController != null && buildController.getCurrentItem() != null) {

                if (inputManager.getTouchDrag()) {
                    WorldHelper.getParcelInRect(
                            viewport.getWorldPosX(inputManager.getTouchDownX()),
                            viewport.getWorldPosY(inputManager.getTouchDownY()),
                            viewport.getWorldPosX(inputManager.getTouchDragX()),
                            viewport.getWorldPosY(inputManager.getTouchDragY()),
                            viewport.getFloor())
                            .forEach(parcel -> renderer.draw(viewport.getScreenPosX(parcel.x), viewport.getScreenPosY(parcel.y), resEden2));
                } else {
                    renderer.draw(inputManager.getMouseX(), inputManager.getMouseY(), resEden);

                    Sprite sprite = spriteManager.getIcon(buildController.getCurrentItem());
                    renderer.draw(inputManager.getMouseX(), inputManager.getMouseY(), sprite);
                }
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

    public void setItemInfo(ItemInfo itemInfo) {
        _itemInfo = itemInfo;
        _mouseDownX = -1;
        _mouseDownY = -1;
    }
}