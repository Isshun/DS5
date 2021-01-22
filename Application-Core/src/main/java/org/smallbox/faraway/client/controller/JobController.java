package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;

import java.util.Optional;

@GameObject
public class JobController extends LuaController {
    @Inject private JobModule jobModule;
    @BindLua private UIList listJobs;
    @Inject private MainPanelController mainPanelController;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
        mainPanelController.addShortcut("Jobs", this);
    }

    @Override
    public void onControllerUpdate() {
        jobModule.getAll().stream().filter(JobModel::isVisible).forEach(job -> {
            UIFrame frame = listJobs.createFromTemplate(UIFrame.class);
            frame.findLabel("lb_job").setText(job.getMainLabel());
            Optional.ofNullable(job.getCharacter()).ifPresent(character -> frame.findLabel("lb_character").setText(character.getName()));
            frame.findImage("img_job").setImage(job.getIcon());
            listJobs.addNextView(frame);
        });
        listJobs.switchViews();
    }

    private String getJobStatus(JobModel job) {
        if (job.getProgress() > 0) {
            return String.format("%3d%%", (int) (job.getProgress() * 100));
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
