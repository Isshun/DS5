package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.controller.area.AreaPanelController;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.extra.RawColors;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLayerBegin;
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

    @OnGameLayerBegin
    public void layerInit() {
        openPaneCrew();
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

    @BindLuaAction
    public void openPaneCrew() {
        last = Pane.CREW;
        openPane(crewController, lbCrew, maskCrew, focusCrew, RawColors.RAW_YELLOW);
    }

    @BindLuaAction
    public void openPaneBuild() {
        last = Pane.BUILD;
        openPane(buildController, lbBuild, maskBuild, focusBuild, RawColors.RAW_BLUE);
    }

    @BindLuaAction
    public void openPaneArea() {
        last = Pane.AREA;
        openPane(areaPanelController, lbArea, maskArea, focusArea, RawColors.RAW_GREEN);
    }

    @BindLuaAction
    public void openPaneJobs() {
        last = Pane.JOBS;
        openPane(jobController, lbJobs, maskJobs, focusJobs, RawColors.RAW_RED);
    }

    public void openLast() {
        switch (last) {
            case CREW -> openPaneCrew();
            case BUILD -> openPaneBuild();
            case AREA -> openPaneArea();
            case JOBS -> openPaneJobs();
        }
    }

}
