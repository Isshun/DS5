package org.smallbox.faraway.core.game.module.job.check;

import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.helper.ItemFinder;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.SleepJob;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.ConsumeJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

/**
 * Created by Alex on 25/10/2015.
 */
public class CheckCharacterEnergyCritical extends CharacterCheck {
    private final ItemFilter bedFilter;
    private final ItemFilter consumableFilter;

    public CheckCharacterEnergyCritical() {
        bedFilter = ItemFilter.createItemFilter();
        bedFilter.effectEnergy = true;
        bedFilter.needItem = true;
        bedFilter.needFreeSlot = true;

        consumableFilter = ItemFilter.createItemFilter();
        consumableFilter.effectEnergy = true;
        consumableFilter.needConsumable = true;
    }

    @Override
    public JobModel create(CharacterModel character) {
        // Go to nearest bed
        ItemModel item = (ItemModel)((ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class)).getNearest(bedFilter, character);
        if (item != null) {
            return new SleepJob(item.getParcel(), item);
        }

        // Use energy consumable
        ConsumableModel consumable = (ConsumableModel) ((ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class)).getNearest(consumableFilter, character);
        if (consumable != null) {
            return ConsumeJob.create(character, consumable);
        }

        // Sleep on ground
        return new SleepJob(character.getParcel());
    }

    @Override
    public boolean check(CharacterModel character) {
        return true;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getNeeds().get("energy") < character.getType().needs.energy.critical;
    }
}
