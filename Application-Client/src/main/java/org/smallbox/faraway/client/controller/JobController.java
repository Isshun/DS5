package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;

@GameObject
public class JobController extends LuaController {

    @Inject
    private JobModule jobModule;

    @BindLua
    private UIList listJobs;

    @Inject
    private MainPanelController mainPanelController;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
        mainPanelController.addShortcut("Jobs", this);
    }

    @Override
    public void onControllerUpdate() {
        jobModule.getJobs().stream().filter(JobModel::isVisible).forEach(job ->
                listJobs.addNextView(new UIFrame(null)
                        .setSize(300, 28)
                        .addView(UIImage.create(null)
                                .setImage(job.getIcon())
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

    @GameShortcut(key = Input.Keys.T)
    public void onPressT() {
        setVisible(true);
    }
}
