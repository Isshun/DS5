package org.smallbox.faraway.core.game.module.minimap;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.View;

public class MinimapModule extends GameModule {
    private static final int    FRAME_WIDTH = 352;
    private static final int    FRAME_HEIGHT = 220;
    private static final int    POS_X = Data.config.screen.resolution[0] - FRAME_WIDTH - 10;
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
                Game.getInstance().getViewport().setPosition(-((x - 19 - POS_X) * Game.getInstance().getInfo().worldWidth / FRAME_WIDTH) * Constant.TILE_WIDTH, -((y - 16 - POS_Y) * Game.getInstance().getInfo().worldHeight / FRAME_HEIGHT) * Constant.TILE_HEIGHT);
                return true;
            }
        }
        _isPressed = false;
        return false;
    }

    @Override
    public void onReloadUI() {
        _panelMain = UserInterface.getInstance().findById("panel_main");
    }

    @Override
    public void onGameStart() {
        _panelMain = UserInterface.getInstance().findById("panel_main");
    }

    @Override
    protected void onLoaded(Game game) {

    }

    @Override
    protected void onUpdate(int tick) {

    }

    @Override
    public void onAddStructure(StructureModel structure) {

    }

    @Override
    public void onRemoveStructure(StructureModel structure) {

    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {

    }

    @Override
    public void onFloorChange(int floor) {

    }
}