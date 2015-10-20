package org.smallbox.faraway.core.game.module.job;

import com.ximpleware.VTDNav;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;

import java.io.FileOutputStream;

/**
 * Created by Alex on 01/06/2015.
 */
public class JobModuleSerializer implements SerializerInterface {
    private final JobModule _jobModule;

    public JobModuleSerializer(JobModule jobModule) {
        _jobModule = jobModule;
    }

    public static class JobSave {
        private final String    type;
        private final String    actionPath;
        private final int       itemId;

        public JobSave(JobModel job) {
            this.type = job.getClass().getName();
            this.itemId = job.getItem() != null ? job.getItem().getId() : -1;
            this.actionPath = job.getItem() != null && job.getActionInfo() != null ? job.getItem().getName() + ":" + job.getActionInfo().name : null;
        }
    }

    @Override
    public void save(FileOutputStream save) {
//        save.jobs = Game.getJobModule().getJobs().stream().old(JobSave::new).collect(Collectors.toList());
    }

    @Override
    public void load(VTDNav save) {
//        Map<String, ItemInfo.ItemInfoAction> actions = new HashMap<>();
//        GameData.getData().items.stream().filter(info -> info.actions != null && !info.actions.isEmpty()).forEach(info -> {
//            info.actions.stream().filter(action -> action.name != null).forEach(action -> {
//                actions.put(info.name + ":" + action.name, action);
//            });
//        });
//
//        for (JobSave job: save.jobs) {
//            // Create CraftJob
//            if (job.type.equals(CraftJob.class.getName())) {
//                if (job.itemId != -1 && job.actionPath != null) {
//                    ModuleHelper.getJobModule().addJob(ModuleHelper.getWorldModule().getItemById(job.itemId), actions.get(job.actionPath));
//                }
//            }
//        }
    }
}
