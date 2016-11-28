package org.smallbox.faraway.module.character.job;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.module.world.model.ItemFilter;
import org.smallbox.faraway.module.consumable.ConsumeJob;
import org.smallbox.faraway.module.item.ItemFinderModule;
import org.smallbox.faraway.module.item.SleepJob;
import org.smallbox.faraway.module.item.item.ItemModel;

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
    public JobModel onCreateJob(CharacterModel character) {
        // Go to nearest bed
        ItemModel item = (ItemModel)((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getNearest(bedFilter, character);
        if (item != null) {
            return new SleepJob(item.getParcel(), item);
        }

        // Use energy consumable
        ConsumableModel consumable = (ConsumableModel) ((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getNearest(consumableFilter, character);
        if (consumable != null) {
            return ConsumeJob.create(character, consumable);
        }

        // Sleep on ground
        return new SleepJob(character.getParcel());
    }

    @Override
    public boolean isJobLaunchable(CharacterModel character) {
        return true;
    }

    @Override
    public boolean isJobNeeded(CharacterModel character) {
        return character.getNeeds().get("energy") < character.getType().needs.energy.critical;
    }

    @Override
    public String getLabel() {
        return "Meurt de sommeil";
    }
}
