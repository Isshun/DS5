package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import com.sun.glass.ui.Cursor;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 15/08/2016.
 */
public class MainPanelController extends LuaController {

    @BindComponent
    private LayerManager layerManager;

    @BindLua
    private UIGrid mainGrid;

    @BindLua
    private UILabel lbPlanet;

    @BindLua
    private UILabel lbFloor;

    private LuaController _currentPaneController;

    @GameShortcut(key = Input.Keys.ESCAPE)
    public void onEscape() {
        if (!isVisible()) {
            setVisible(true);
        }
//        if (key == Input.Keys.ESCAPE && !Display.isVisible()) {
//            Cursor.setVisible(true);
//            return true;
//        }
//        return false;
    }

    @Override
    public void onGameUpdate(Game game) {
        lbPlanet.setText(game.getPlanetInfo().label + " / " + game.getRegionInfo().label);
        lbFloor.setText("Floor " + layerManager.getViewport().getFloor());
    }

    @Override
    public void onClickOnMap(GameEvent mouseEvent) {
        Cursor.setVisible(true);
    }

    public void addShortcut(String label, LuaController controller) {
        if (mainGrid != null) {
            mainGrid.addView(UILabel.create(null)
                    .setText(label)
                    .setTextSize(18)
                    .setPadding(10)
                    .setSize(170, 40)
                    .setId(mainGrid.getName() + "." + label)
                    .setName(mainGrid.getName() + "." + label)
                    .setBackgroundColor(0x349394)
                    .setFocusBackgroundColor(0x25c9cb)
                    .setOnClickListener(event -> {
                        _currentPaneController = controller;
                        _currentPaneController.setVisible(true);
                    }));
        }
    }

    @Override
    protected void onControllerUpdate() {

    }
}
