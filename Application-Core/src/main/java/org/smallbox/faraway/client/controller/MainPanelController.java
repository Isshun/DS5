package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.area.AreaPanelController;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.UIManager;
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

    private Pane last;

    private enum Pane {CREW, BUILD, AREA, JOBS}

    @OnGameLayerInit
    public void layerInit() {
        openCrew();
    }

    @Override
    public void onReloadUI() {
        gameSelectionManager.registerSelectionPre(this);
        btCrew.getEvents().setOnClickListener(this::openCrew);
        btBuild.getEvents().setOnClickListener(this::openBuild);
        btArea.getEvents().setOnClickListener(this::openArea);
        btJobs.getEvents().setOnClickListener(this::openJobs);
    }

    private void openPane(LuaController controller, UILabel label, View mask, View focus, int focusColor) {
        setVisible(true);
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
        viewBorder.getStyle().setBackgroundColor(focusColor);

        controller.getRootView().setVisible(true);
    }

    public void openCrew() {
        last = Pane.CREW;
        openPane(crewController, lbCrew, maskCrew, focusCrew, RawColors.RAW_YELLOW);
    }

    public void openBuild() {
        last = Pane.BUILD;
        openPane(buildController, lbBuild, maskBuild, focusBuild, RawColors.RAW_BLUE);
    }

    public void openArea() {
        last = Pane.AREA;
        openPane(areaPanelController, lbArea, maskArea, focusArea, RawColors.RAW_GREEN);
    }

    public void openJobs() {
        last = Pane.JOBS;
        openPane(jobController, lbJobs, maskJobs, focusJobs, RawColors.RAW_RED);
    }

    public void openLast() {
        switch (last) {
            case CREW: openCrew(); break;
            case BUILD: openBuild(); break;
            case AREA: openArea(); break;
            case JOBS: openJobs(); break;
        }
    }

}
