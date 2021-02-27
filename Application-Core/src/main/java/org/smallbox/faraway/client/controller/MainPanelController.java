package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.math.Interpolation;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.controller.area.AreaPanelController;
import org.smallbox.faraway.client.engine.animator.InstantAnimator;
import org.smallbox.faraway.client.engine.animator.NewAnimator;
import org.smallbox.faraway.client.engine.animator.SerialAnimator;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.TransitionManager;
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
    @Inject private TransitionManager transitionManager;
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

    @BindLua private View btCrew;
    @BindLua private View btBuild;
    @BindLua private View btAreas;
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

    @OnGameLayerBegin
    public void layerInit() {
        openPaneCrew();
    }

    private void openPane(LuaController controller, View button, UILabel label, View mask, View focus) {
        controller.getRootView().setVisible(true);

        transitionManager.add(SerialAnimator.of(
                new NewAnimator<>(0, -25, 40, Interpolation.smoother, button, (view, value) -> movePane(button, controller, value)),
                new InstantAnimator<>(button, (view, progress) -> doOpenPane(controller, label, mask, focus)),
                new NewAnimator<>(-25, 0, 160, Interpolation.smoother, button, (view, value) -> movePane(button, controller, value))
        ));
    }

    private void doOpenPane(LuaController controller, UILabel label, View mask, View focus) {
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
    }

    private void movePane(View button, LuaController controller, Float value) {
        controller.getRootView().setPositionX(value);
        button.setPositionX(value);
    }

    @BindLuaAction
    public void openPaneCrew() {
        last = Pane.CREW;
        openPane(crewController, btCrew, lbCrew, maskCrew, focusCrew);
    }

    @BindLuaAction
    public void openPaneBuild() {
        last = Pane.BUILD;
        openPane(buildController, btBuild, lbBuild, maskBuild, focusBuild);
    }

    @BindLuaAction
    public void openPaneArea() {
        last = Pane.AREA;
        openPane(areaPanelController, btAreas, lbArea, maskArea, focusArea);
    }

    @BindLuaAction
    public void openPaneJobs() {
        last = Pane.JOBS;
        openPane(jobController, btJobs, lbJobs, maskJobs, focusJobs);
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
