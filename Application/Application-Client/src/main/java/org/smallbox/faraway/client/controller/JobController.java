package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 24/07/2016.
 */
public class JobController extends LuaController {

    @BindModule
    private JobModule jobModule;

    @BindLua
    private UIList listJobs;

    @BindLuaController
    private MainPanelController mainPanelController;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Jobs", this);
    }

    @Override
    public void onGameUpdate(Game game) {
        listJobs.clear();

        jobModule.getJobs().forEach(job -> {
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

            Log.info("jobModule: " + isVisible() + ", main: " + ApplicationClient.uiManager.findById("base.ui.panel_main").isVisible());
        }
    }
}
