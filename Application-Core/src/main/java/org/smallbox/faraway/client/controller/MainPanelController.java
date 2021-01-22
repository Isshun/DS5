package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.area.AreaPanelController;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.RawColors;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
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

    @BindLua private View viewBorder;
    @BindLua private View btCrew;
    @BindLua private View btBuild;
    @BindLua private View btArea;
    @BindLua private View btJobs;
    @BindLua private View maskCrew;
    @BindLua private View maskBuild;
    @BindLua private View maskArea;
    @BindLua private View maskJobs;
    @BindLua private View focusCrew;
    @BindLua private View focusBuild;
    @BindLua private View focusArea;
    @BindLua private View focusJobs;
    @BindLua private UILabel lbCrew;
    @BindLua private UILabel lbBuild;
    @BindLua private UILabel lbArea;
    @BindLua private UILabel lbJobs;

    private LuaController _currentPaneController;

    @OnGameLayerInit
    public void layerInit() {
//        openPane(crewController, lbCrew, maskCrew, focusCrew, RawColors.RAW_YELLOW);
//        openPane(buildController, lbBuild, maskBuild, focusBuild, RawColors.RAW_BLUE);
        openPane(areaPanelController, lbArea, maskArea, focusArea, RawColors.RAW_GREEN);
    }

    @Override
    public void onReloadUI() {
        gameSelectionManager.registerSelectionPre(this);
        btCrew.getEvents().setOnClickListener((x, y) -> openPane(crewController, lbCrew, maskCrew, focusCrew, RawColors.RAW_YELLOW));
        btBuild.getEvents().setOnClickListener((x, y) -> openPane(buildController, lbBuild, maskBuild, focusBuild, RawColors.RAW_BLUE));
        btArea.getEvents().setOnClickListener((x, y) -> openPane(areaPanelController, lbArea, maskArea, focusArea, RawColors.RAW_GREEN));
        btJobs.getEvents().setOnClickListener((x, y) -> openPane(jobController, lbJobs, maskJobs, focusJobs, RawColors.RAW_RED));
    }

    private void openPane(LuaController controller, UILabel label, View mask, View focus, int focusColor) {
        maskCrew.setVisible(true);
        maskBuild.setVisible(true);
        maskArea.setVisible(true);
        maskJobs.setVisible(true);
        focusCrew.setVisible(false);
        focusBuild.setVisible(false);
        focusArea.setVisible(false);
        focusJobs.setVisible(false);
        lbCrew.setTextColor(RawColors.RAW_YELLOW);
        lbBuild.setTextColor(RawColors.RAW_BLUE);
        lbArea.setTextColor(RawColors.RAW_GREEN);
        lbJobs.setTextColor(RawColors.RAW_RED);

        mask.setVisible(false);
        focus.setVisible(true);
        label.setTextColor(RawColors.RAW_BLUE_DARK_4);
//        label.getStyle().setBackgroundColor(RawColors.RAW_BLUE_DARK_4);
//        viewBorder.setRegularBackgroundColor(focusColor);
        viewBorder.getStyle().setBackgroundColor(focusColor);
//        viewBorder.getStyle().setBackgroundFocusColor(focusColor);

        controller.getRootView().setVisible(true);
    }

    @Override
    public void onClickOnMap(GameEvent mouseEvent) {
//        Cursor.setVisible(true);
    }

    public void addShortcut(String label, LuaController controller) {
//        String id = mainGrid.getId() + "." + controller.getClass().getCanonicalName();
//
//        // Remove old entries and listeners if exists
//        mainGrid.getViews().stream()
//                .filter(view -> StringUtils.equals(view.getId(), id))
//                .forEach(oldView -> uiManager.removeView(oldView));
//
//        UILabel uiLabel = (UILabel)mainGrid.createFromTemplate();
//        uiLabel.setId(id);
//        uiLabel.setText(label);
//        uiLabel.setId(mainGrid.getId() + "." + label);
//        uiLabel.getEvents().setOnClickListener((x, y) -> controller.getRootView().setVisible(true));
    }

//    @GameShortcut(key = Input.Keys.F1)
//    public void onRefreshUI() {
//        DependencyManager.getInstance().getDependency(UIManager.class).refresh(this, "panel_main.lua");
//        openPane(crewController, lbCrew, maskCrew, focusCrew, RawColors.RAW_YELLOW);
//    }

}
