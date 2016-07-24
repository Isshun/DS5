package org.smallbox.faraway.module.job;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.UIList;

/**
 * Created by Alex on 24/07/2016.
 */
public class JobController extends LuaController {
    @BindModule("")
    private JobModule jobs;

    @BindLua
    private UIList listJobs;

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
}
