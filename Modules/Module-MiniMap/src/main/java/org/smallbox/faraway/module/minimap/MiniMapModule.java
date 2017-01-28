package org.smallbox.faraway.module.minimap;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.GameClientModule;
import org.smallbox.faraway.client.ModuleRenderer;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Constant;

@ModuleRenderer(MinimapRenderer.class)
public class MiniMapModule extends GameClientModule {
    private static final int    FRAME_WIDTH = 352;
    private static final int    FRAME_HEIGHT = 220;
    private static final int    POS_X = Gdx.graphics.getWidth() - FRAME_WIDTH - 10;
    private static final int    POS_Y = 84;

    private View _panelMain;
    private boolean _isPressed;

    @Override
    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        if (_panelMain != null && _panelMain.isVisible() && x >= POS_X && x <= POS_X + FRAME_WIDTH && y >= POS_Y && y <= POS_Y + FRAME_HEIGHT) {
            if (action == GameEventListener.Action.PRESSED && button == GameEventListener.MouseButton.LEFT) {
                _isPressed = true;
            }
            if (action == GameEventListener.Action.RELEASED && button == GameEventListener.MouseButton.LEFT) {
                _isPressed = false;
            }
            if ((action == GameEventListener.Action.RELEASED && button == GameEventListener.MouseButton.LEFT) ||
                    (action == GameEventListener.Action.MOVE && _isPressed)) {
                ApplicationClient.mainRenderer.getViewport().setPosition(-((x - 19 - POS_X) * Application.gameManager.getGame().getInfo().worldWidth / FRAME_WIDTH) * Constant.TILE_WIDTH, -((y - 16 - POS_Y) * Application.gameManager.getGame().getInfo().worldHeight / FRAME_HEIGHT) * Constant.TILE_HEIGHT);
                return true;
            }
        }
        _isPressed = false;
        return false;
    }

    @Override
    public void onReloadUI() {
        _panelMain = ApplicationClient.uiManager.findById("base.ui.panel_main");
    }

    @Override
    public void onGameStart(Game game) {
        _panelMain = ApplicationClient.uiManager.findById("base.ui.panel_main");
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {

    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {

    }

    @Override
    public void onFloorChange(int floor) {

    }
}