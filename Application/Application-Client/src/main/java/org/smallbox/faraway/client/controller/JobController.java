package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.building.BasicBuildJob;
import org.smallbox.faraway.modules.building.BasicRepairJob;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.dig.BasicDigJob;
import org.smallbox.faraway.modules.itemFactory.BasicCraftJob;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.plant.BasicHarvestJob;
import org.smallbox.faraway.modules.storing.BasicStoreJob;
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
    public void onNewGameUpdate(Game game) {
        jobModule.getJobs().stream().filter(JobModel::isVisible).forEach(job ->
                listJobs.addNextView(new UIFrame(null)
                        .setSize(300, 28)
                        .addView(UIImage.create(null)
                                .setImage(getImagePath(job))
                                .setPosition(-19, -19)
                                .setSize(30, 30))
                        .addView(UILabel.create(null)
                                .setDashedString(job.getMainLabel(), getJobStatus(job), 40)
                                .setTextColor(job.getCharacter() != null ? 0x9afbff : 0xB4D4D3)
                                .setSize(300, 22)
                                .setPosition(24, 0))
                )
        );
        listJobs.switchViews();
    }

    private String getJobStatus(JobModel job) {
        if (job.getProgress() > 0) {
            return String.format("%3d%%", (int)(job.getProgress() * 100));
        }

        if (job.getCharacter() != null) {
            return job.getCharacter().getName();
        }

        return job.getStatus().name();
    }

    private String getImagePath(JobModel job) {
        if (job instanceof BasicDigJob) return "[base]/graphics/jobs/ic_mining.png";
        if (job instanceof BasicHaulJob) return "[base]/graphics/jobs/ic_haul.png";
        if (job instanceof BasicStoreJob) return "[base]/graphics/jobs/ic_store.png";
        if (job instanceof BasicCraftJob) return "[base]/graphics/jobs/ic_craft.png";
        if (job instanceof BasicBuildJob) return "[base]/graphics/jobs/ic_build.png";
        if (job instanceof BasicRepairJob) return "[base]/graphics/jobs/ic_build.png";
        if (job instanceof BasicHarvestJob) return "[base]/graphics/jobs/ic_gather.png";
        return null;
    }

    @Override
    public void onKeyEvent(GameEventListener.Action action, int key, GameEventListener.Modifier modifier) {
        if (action == GameEventListener.Action.RELEASED && key == Input.Keys.T && modifier == GameEventListener.Modifier.NONE) {
            setVisible(!isVisible());
            ApplicationClient.uiManager.findById("base.ui.right_panel").setVisible(!isVisible());

            Log.info("jobModule: " + isVisible() + ", main: " + ApplicationClient.uiManager.findById("base.ui.right_panel").isVisible());
        }
    }
}
