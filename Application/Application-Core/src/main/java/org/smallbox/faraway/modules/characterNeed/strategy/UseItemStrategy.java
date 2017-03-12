package org.smallbox.faraway.modules.characterNeed.strategy;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.character.model.base.NeedEntry;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumeJob;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModule;

/**
 * Created by Alex on 11/03/2017.
 */
public class UseItemStrategy implements NeedRestoreStrategy {

    private ConsumeJob _job;

    @Override
    public boolean ok(CharacterModel character, NeedEntry need, JobModule jobModule, ConsumableModule consumableModule, ItemModule itemModule) {

        // Find best item
        UsableItem bestItem = itemModule.getItems().stream()
                .filter(item -> need.hasEffect(item.getInfo().use))
                .findAny().orElse(null);

        // Create use job
        if (bestItem != null) {

            _job = itemModule.createUseJob(bestItem, bestItem.getInfo().use.duration, (consumable, durationLeft) -> {
                character.getExtra(CharacterNeedsExtra.class).use(consumable.getInfo().use);
            });

            return true;
        }

        return false;
    }

    @Override
    public boolean done() {
        return _job == null || _job.isClose();
    }

}
