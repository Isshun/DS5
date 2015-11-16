package org.smallbox.faraway.core.game.module.job.check.character;

import org.smallbox.faraway.core.game.helper.ItemFinder;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.ConsumeJob;
import org.smallbox.faraway.core.game.module.job.model.UseJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckCharacterWaterWarning extends CharacterCheck {

    @Override
    public boolean check(CharacterModel character) {
        return character.getType().needs.water != null && character.getNeeds().get("drink") < character.getType().needs.water.warning;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getType().needs.water != null && character.getNeeds().get("drink") < character.getType().needs.water.warning;
    }

    @Override
    public JobModel create(CharacterModel character) {
        ConsumableModel consumable = null;
        ItemFilter consumableFilter = ItemFilter.createConsumableFilter();
        consumableFilter.effectDrink = true;

        // Get consumable on inventory
        if (character.getInventory() != null && character.getInventory().matchFilter(consumableFilter)) {
            consumable = character.getInventory();
        }

        // Get consumable on ground
        ConsumableModel nearestConsumable = (ConsumableModel)((ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class)).getNearest(consumableFilter, character);
        if (nearestConsumable != null && nearestConsumable.hasFreeSlot()) {
            consumable = nearestConsumable;
        }

        ItemModel item = null;
        ItemFilter itemFilter = ItemFilter.createItemFilter();
        itemFilter.effectDrink = true;

        // Get drink provider
        ItemModel nearestItem = (ItemModel)((ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class)).getNearest(itemFilter, character);
        if (nearestItem != null && nearestItem.hasFreeSlot()) {
            item = nearestItem;
        }

        if (item != null && consumable != null) {
            if (WorldHelper.getDistance(character.getParcel(), consumable.getParcel()) < WorldHelper.getDistance(character.getParcel(), item.getParcel())) {
                return ConsumeJob.create(character, consumable);
            } else {
                return UseJob.create(character, item);
            }
        }

        if (item != null) {
            return UseJob.create(character, item);
        }

        if (consumable != null) {
            return ConsumeJob.create(character, consumable);
        }

        return null;
    }
}
