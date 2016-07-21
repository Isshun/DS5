package org.smallbox.faraway.module.character.job;

import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.ConsumeJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.module.item.ItemFinder;
import org.smallbox.faraway.module.item.SleepJob;
import org.smallbox.faraway.module.item.item.ItemModel;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckCharacterEnergyWarning extends CharacterCheck {
    private final ItemFilter bedFilter;
    private final ItemFilter consumableFilter;

    public CheckCharacterEnergyWarning() {
        bedFilter = ItemFilter.createItemFilter();
        bedFilter.effectEnergy = true;
        bedFilter.needItem = true;
        bedFilter.needFreeSlot = true;

        consumableFilter = ItemFilter.createItemFilter();
        consumableFilter.effectEnergy = true;
        consumableFilter.needConsumable = true;
    }

    @Override
    public boolean check(CharacterModel character) {
        ItemFinder finder = (ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class);
        return finder.getNearest(bedFilter, character) != null || finder.getNearest(consumableFilter, character) != null;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getNeeds().get("energy") < character.getType().needs.energy.warning;
    }

    @Override
    public JobModel create(CharacterModel character) {
        // Get nearest bed
        ItemModel item = (ItemModel)((ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class)).getNearest(bedFilter, character);
        if (item != null) {
            return new SleepJob(item.getParcel(), item);
        }

        // Get energy consumable
        ConsumableModel consumable = (ConsumableModel) ((ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class)).getNearest(consumableFilter, character);
        if (consumable != null) {
            return ConsumeJob.create(character, consumable);
        }

        return null;
    }
}
