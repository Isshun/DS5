package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 15/08/2016.
 */
@GameObject
public class MainPanelController extends LuaController {

    @Inject
    private SelectionManager selectionManager;

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private UIManager uiManager;

    @Inject
    private Game game;

    @Inject
    private LayerManager layerManager;

    @BindLua
    private UIGrid mainGrid;

//    @BindLua
//    private UIFrame subController;

    @BindLua
    private UILabel lbPlanet;

    @BindLua
    private UILabel lbFloor;

    private LuaController _currentPaneController;

    @Override
    public void onReloadUI() {
        selectionManager.registerSelectionPre(this);
    }

    @Override
    public void onGameUpdate(Game game) {
        lbPlanet.setText(game.getPlanetInfo().label + " / " + game.getRegionInfo().label);
        lbFloor.setText("Floor " + layerManager.getViewport().getFloor());
    }

    @Override
    public void onClickOnMap(GameEvent mouseEvent) {
//        Cursor.setVisible(true);
    }

    public void addShortcut(String label, LuaController controller) {
        String id = mainGrid.getName() + "." + controller.getClass().getCanonicalName();

        // Remove old entries and listeners if exists
        mainGrid.getViews().stream()
                .filter(view -> StringUtils.equals(view.getId(), id))
                .forEach(oldView -> uiManager.removeView(oldView));

//        mainGrid.getViews().removeIf(view -> StringUtils.equals(view.getId(), id));


//        mainGrid.getViews().stream().filter(view -> StringUtils.equals(view.getId(), id)).forEach(oldView -> {
//            uiEventManager.removeListeners(oldView);
//            uiManager.getViews().remove(oldView);
//            mainGrid.getViews().remove(oldView);
//        });

        mainGrid.addView(UILabel.create(null)
                .setText(label)
                .setTextSize(18)
                .setPadding(10)
                .setSize(170, 40)
                .setId(id)
                .setName(mainGrid.getName() + "." + label)
                .setBackgroundColor(0x349394ff)
                .setFocusBackgroundColor(0x25c9cbff)
                .setOnClickListener((x, y) -> {
//                    mainContent.removeAllViews();
//                    mainContent.addView(controller.getRootView());
                    controller.getRootView().setVisible(true);
//                    _currentPaneController = controller;
//                    _currentPaneController.setVisible(true);
                }));
    }

}
