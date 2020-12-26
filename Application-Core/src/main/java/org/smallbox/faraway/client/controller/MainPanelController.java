package org.smallbox.faraway.client.controller;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.selection.SelectionManager;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.area.AreaPanelController;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;

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

    @Inject
    private CrewController crewController;

    @Inject
    private AreaPanelController areaPanelController;

    @Inject
    private BuildController buildController;

    @Inject
    private JobController jobController;

    @BindLua
    private UIGrid mainGrid;

    @BindLua
    private UILabel lbPlanet;

    @BindLua
    private UILabel lbFloor;

    @BindLua
    private View btCrew;

    @BindLua
    private View btBuild;

    @BindLua
    private View btArea;

    @BindLua
    private View btJobs;

    @BindLua
    private View mapContainer;

    private LuaController _currentPaneController;

    @Override
    public void onReloadUI() {
        selectionManager.registerSelectionPre(this);
        btCrew.setOnClickListener((x, y) -> crewController.getRootView().setVisible(true));
        btArea.setOnClickListener((x, y) -> areaPanelController.getRootView().setVisible(true));
        btBuild.setOnClickListener((x, y) -> buildController.getRootView().setVisible(true));
        btJobs.setOnClickListener((x, y) -> jobController.getRootView().setVisible(true));
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

    public View getMapContainer() {
        return mapContainer;
    }
}
