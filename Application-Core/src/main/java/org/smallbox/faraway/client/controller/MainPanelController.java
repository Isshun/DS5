package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.area.AreaPanelController;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;

@GameObject
public class MainPanelController extends LuaController {
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private UIManager uiManager;
    @Inject private Game game;
    @Inject private LayerManager layerManager;
    @Inject private CrewController crewController;
    @Inject private AreaPanelController areaPanelController;
    @Inject private BuildController buildController;
    @Inject private JobController jobController;
    @Inject private Viewport viewport;

    @BindLua private UIGrid mainGrid;
    @BindLua private UILabel lbPlanet;
    @BindLua private UILabel lbFloor;
    @BindLua private View btCrew;
    @BindLua private View btBuild;
    @BindLua private View btArea;
    @BindLua private View btJobs;
    @BindLua private View mapContainer;

    private LuaController _currentPaneController;

    @Override
    public void onReloadUI() {
        gameSelectionManager.registerSelectionPre(this);
        btCrew.getEvents().setOnClickListener((x, y) -> crewController.getRootView().setVisible(true));
        btArea.getEvents().setOnClickListener((x, y) -> areaPanelController.getRootView().setVisible(true));
        btBuild.getEvents().setOnClickListener((x, y) -> buildController.getRootView().setVisible(true));
        btJobs.getEvents().setOnClickListener((x, y) -> jobController.getRootView().setVisible(true));
    }

    @Override
    public void onGameUpdate(Game game) {
        lbPlanet.setText(game.getPlanetInfo().label + " / " + game.getRegionInfo().label);
        lbFloor.setText("Floor " + viewport.getFloor());
    }

    @Override
    public void onClickOnMap(GameEvent mouseEvent) {
//        Cursor.setVisible(true);
    }

    public void addShortcut(String label, LuaController controller) {
        String id = mainGrid.getId() + "." + controller.getClass().getCanonicalName();

        // Remove old entries and listeners if exists
        mainGrid.getViews().stream()
                .filter(view -> StringUtils.equals(view.getId(), id))
                .forEach(oldView -> uiManager.removeView(oldView));

        UILabel uiLabel = (UILabel)mainGrid.createFromTemplate();
        uiLabel.setId(id);
        uiLabel.setText(label);
        uiLabel.setId(mainGrid.getId() + "." + label);
        uiLabel.getEvents().setOnClickListener((x, y) -> controller.getRootView().setVisible(true));
    }

    public View getMapContainer() {
        return mapContainer;
    }

    @GameShortcut(key = Input.Keys.F1)
    public void onRefreshUI() {
        DependencyManager.getInstance().getDependency(UIManager.class).refresh(this, "panel_main.lua");
    }

}
