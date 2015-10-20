package org.smallbox.faraway.core.game.module.job.check.character;

import org.smallbox.faraway.core.game.helper.ItemFinder;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.job.model.ConsumeJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.job.model.UseJob;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;

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
