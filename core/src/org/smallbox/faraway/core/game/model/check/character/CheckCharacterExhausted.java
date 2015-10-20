package org.smallbox.faraway.core.game.model.check.character;

import org.smallbox.faraway.core.game.helper.ItemFinder;
import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.model.item.ConsumableModel;
import org.smallbox.faraway.core.game.model.item.ItemFilter;
import org.smallbox.faraway.core.game.model.item.ItemModel;
import org.smallbox.faraway.core.game.model.item.MapObjectModel;
import org.smallbox.faraway.core.game.model.job.ConsumeJob;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;
import org.smallbox.faraway.core.game.model.job.UseJob;
import org.smallbox.faraway.core.game.module.ModuleHelper;
import org.smallbox.faraway.core.game.module.ModuleManager;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckCharacterExhausted extends CharacterCheck {

    @Override
    public boolean check(CharacterModel character) {
        for (ItemModel item: ModuleHelper.getWorldModule().getItems()) {
            if (item.isBed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getNeeds().energy < character.getType().needs.energy.warning;
    }

    @Override
    public JobModel create(CharacterModel character) {
        ItemFilter filter = ItemFilter.createItemFilter();
        filter.effectEnergy = true;

        // Get nearest bed
        MapObjectModel item = ((ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class)).getNearest(filter, character);
        if (item == null) {
            return null;
        }

        if (item instanceof ItemModel) {
            return UseJob.create((ItemModel) item, character);
        }

        if (item instanceof ConsumableModel) {
            return ConsumeJob.create(character, (ConsumableModel) item);
        }

        return null;
    }
}
