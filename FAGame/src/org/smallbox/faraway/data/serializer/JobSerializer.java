package org.smallbox.faraway.data.serializer;

import com.ximpleware.VTDNav;
import org.smallbox.faraway.game.model.job.BaseJobModel;

import java.io.FileOutputStream;

/**
 * Created by Alex on 01/06/2015.
 */
public class JobSerializer implements SerializerInterface {
    public static class JobSave {
        private final String    type;
        private final String    actionPath;
        private final int       itemId;

        public JobSave(BaseJobModel job) {
            this.type = job.getClass().getName();
            this.itemId = job.getItem() != null ? job.getItem().getId() : -1;
            this.actionPath = job.getItem() != null && job.getActionInfo() != null ? job.getItem().getName() + ":" + job.getActionInfo().name : null;
        }
    }

    @Override
    public void save(FileOutputStream save) {
//        save.jobs = Game.getJobManager().getJobs().stream().map(JobSave::new).collect(Collectors.toList());
    }

    @Override
    public void load(VTDNav save) {
//        Map<String, ItemInfo.ItemInfoAction> actions = new HashMap<>();
//        GameData.getData().items.stream().filter(itemInfo -> itemInfo.actions != null && !itemInfo.actions.isEmpty()).forEach(itemInfo -> {
//            itemInfo.actions.stream().filter(action -> action.name != null).forEach(action -> {
//                actions.put(itemInfo.name + ":" + action.name, action);
//            });
//        });
//
//        for (JobSave job: save.jobs) {
//            // Create JobCraft
//            if (job.type.equals(JobCraft.class.getName())) {
//                if (job.itemId != -1 && job.actionPath != null) {
//                    JobManager.getInstance().addJob(Game.getWorldManager().getItemById(job.itemId), actions.get(job.actionPath));
//                }
//            }
//        }
    }
}
