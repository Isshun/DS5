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

/**
 * Created by Alex on 11/03/2017.
 */
public class ConsumeStrategy implements NeedRestoreStrategy {

    private ConsumeJob _job;

    @Override
    public boolean ok(CharacterModel character, NeedEntry need, JobModule jobModule, ConsumableModule consumableModule, ItemModule itemModule) {

        // Find best item
        ConsumableItem bestConsumable = consumableModule.getConsumables().stream()
                .filter(consumable -> consumable.getFreeQuantity() > 0)
                .filter(item -> need.hasEffect(item.getInfo().consume))
                .findAny().orElse(null);

        // Create consume job
        if (bestConsumable != null) {

            _job = consumableModule.createConsumeJob(bestConsumable, bestConsumable.getInfo().consume.duration, (consumable, durationLeft) -> {
                ItemInfo itemInfo = bestConsumable.getInfo();
                character.getExtra(CharacterNeedsExtra.class).use(itemInfo.consume);
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
