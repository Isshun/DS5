package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.job.JobCharacterStatus;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.JobModule;

@GameObject
public class JobController extends LuaController {
    @Inject private MainPanelController mainPanelController;
    @Inject private CharacterModule characterModule;
    @Inject private JobModule jobModule;

    @BindLua private UIList listJobs;

    @OnGameUpdate
    public void onControllerUpdate() {
        jobModule.getAll().stream().filter(JobModel::isVisible).forEach(job -> {
            UIFrame frame = listJobs.createFromTemplate(UIFrame.class);
            frame.findLabel("lb_job").setText(job.getMainLabel());
            frame.findLabel("lb_status").setText(String.valueOf(job.getStatus()));
            frame.findImage("img_job").setImage(job.getIcon());

            UIList listStatus = (UIList) frame.find("list_status");
            characterModule.getAll().forEach(character -> {
                JobCharacterStatus status = job.getStatusForCharacter(character);
                if (status != null) {
                    CompositeView view = listStatus.createFromTemplate(CompositeView.class);
                    view.findLabel("lb_status_character").setText(character.getName());
                    view.findLabel("lb_status_label").setText(status.label);
                    view.find("view_status_available").setVisible(!status.available);
                    view.findLabel("lb_status_index").setText(String.valueOf(status.index));
                    listStatus.addNextView(view);
                }
            });
            listStatus.switchViews();

//            Optional.ofNullable(job.getCharacter()).ifPresent(character -> frame.findLabel("lb_character").setText(character.getName()));
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

}
