package org.smallbox.faraway.modules.characterNeed.strategy;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.character.model.base.NeedEntry;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumeJob;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

/**
 * Created by Alex on 11/03/2017.
 */
public class ConsumeStrategy implements NeedRestoreStrategy {

    private boolean _done;
    private int _duration;

    @Override
    public boolean ok(CharacterModel character, NeedEntry need, JobModule jobModule, ConsumableModule consumableModule, ItemModule itemModule) {

        CharacterNeedsExtra needsExtra = character.getExtra(CharacterNeedsExtra.class);

        // Find best item
        ConsumableItem bestConsumable = consumableModule.getConsumables().stream()
                .filter(consumable -> consumable.getFreeQuantity() > 0)
                .filter(item -> need.hasEffect(item.getInfo().consume))
                .findAny().orElse(null);

        // Create consume job
        if (bestConsumable != null) {

            jobModule.createJob(ConsumeJob.class, null, bestConsumable.getParcel(), job -> {
                job.setMainLabel("Consume " + bestConsumable.getInfo().label);

                ConsumableModule.ConsumableJobLock lock = consumableModule.lock(job, bestConsumable, 1);
                job.addTask("Move", c -> c.moveTo(bestConsumable.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);
                job.addTask("Consume", c -> {
                    if (lock.available) {
                        ItemInfo itemInfo = bestConsumable.getInfo();
                        needsExtra.use(itemInfo.consume);
                        job.setProgress(_duration, itemInfo.consume.duration);
                        return ++_duration >= itemInfo.consume.duration ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE;
                    }
                    return JobTaskReturn.INVALID;
                });
                job.addTechnicalTask("Take lock", c -> consumableModule.takeConsumable(lock).getInfo());
                job.addTechnicalTask("Done", c -> _done = true);

                return true;
            });

            return true;
        }

        return false;
    }

    @Override
    public boolean done() {
        return _done;
    }

}
