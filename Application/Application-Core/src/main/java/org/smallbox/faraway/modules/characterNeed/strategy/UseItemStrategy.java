package org.smallbox.faraway.modules.characterNeed.strategy;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.character.model.base.NeedEntry;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumeJob;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

/**
 * Created by Alex on 11/03/2017.
 */
public class UseItemStrategy implements NeedRestoreStrategy {

    private boolean _done;

    @Override
    public boolean ok(CharacterModel character, NeedEntry need, JobModule jobModule, ConsumableModule consumableModule, ItemModule itemModule) {
        // Find best item
        UsableItem bestItem = itemModule.getItems().stream()
                .filter(item -> need.hasEffect(item.getInfo().use))
                .findAny().orElse(null);

        // Create use job
        if (bestItem != null) {

            jobModule.createJob(ConsumeJob.class, null, bestItem.getParcel(), job -> {
                job.addTask("Move", c -> c.moveTo(bestItem.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);
                job.addTask("Use", c -> {
                    character.getExtra(CharacterNeedsExtra.class).use(bestItem.getInfo().use);
                    return JobTaskReturn.COMPLETE;
                });
                job.addTechnicalTask("Done", c -> _done = true);

                return true;
            });

        }

        return false;
    }

    @Override
    public boolean done() {
        return _done;
    }

}
