package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.modules.building.BasicBuildJob;
import org.smallbox.faraway.modules.building.BasicRepairJob;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.dig.BasicDigJob;
import org.smallbox.faraway.modules.itemFactory.BasicCraftJob;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.plant.BasicHarvestJob;
import org.smallbox.faraway.modules.storing.BasicStoreJob;

/**
 * Created by Alex on 24/07/2016.
 */
@GameObject
public class JobController extends LuaController {

    @BindComponent
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
    public void onControllerUpdate() {
        jobModule.getJobs().stream().filter(JobModel::isVisible).forEach(job ->
                listJobs.addNextView(new UIFrame(null)
                        .setSize(300, 28)
                        .addView(UIImage.create(null)
                                .setImage(getImagePath(job))
                                .setPosition(-19, -19)
                                .setSize(30, 30))
                        .addView(UILabel.create(null)
                                .setDashedString(job.getMainLabel(), getJobStatus(job), 40)
                                .setTextColor(job.getCharacter() != null ? 0x9afbffff : 0xB4D4D3ff)
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

    @GameShortcut(key = Input.Keys.T)
    public void onPressT() {
        setVisible(true);
    }
}
