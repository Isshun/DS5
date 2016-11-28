package org.smallbox.faraway.module.job;

import org.smallbox.faraway.core.game.GameEvent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.lua.BindLuaController;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.character.controller.LuaController;
import org.smallbox.faraway.client.ui.ApplicationClient;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.module.mainPanel.MainPanelController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;

/**
 * Created by Alex on 24/07/2016.
 */
public class JobController extends LuaController {
    @BindModule
    private JobModule jobs;

    @BindLua
    private UIList listJobs;

    @BindLuaController
    private MainPanelController _mainPanelController;

    @Override
    protected void onGameCreate(Game game) {
        _mainPanelController.addShortcut("Jobs", (GameEvent event) -> setVisible(true));
    }

    @Override
    protected void onGameUpdate(Game game) {
        listJobs.clear();

        jobs.getJobs().forEach(job -> {
            UILabel lbJob = new UILabel(null);
            lbJob.setDashedString(job.getLabel(), job.getProgress() > 0 ? String.valueOf((int)(job.getProgress() * 100)) : job.getStatus().name(), 42);
            lbJob.setSize(300, 22);
            listJobs.addView(lbJob);
        });
    }

    @Override
    public void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        if (action == GameEventListener.Action.RELEASED && key == GameEventListener.Key.T && modifier == GameEventListener.Modifier.NONE) {
            setVisible(!isVisible());
            ApplicationClient.uiManager.findById("base.ui.panel_main").setVisible(!isVisible());

            Log.info("jobs: " + isVisible() + ", main: " + ApplicationClient.uiManager.findById("base.ui.panel_main").isVisible());
        }
    }
}
