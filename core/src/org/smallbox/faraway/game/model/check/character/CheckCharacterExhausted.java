package org.smallbox.faraway.game.model.check.character;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.ItemFinder;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemFilter;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobConsume;
import org.smallbox.faraway.game.model.job.JobUse;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckCharacterExhausted extends CharacterCheck {

    @Override
    public boolean check(CharacterModel character) {
        for (ItemModel item: Game.getWorldManager().getItems()) {
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
    public BaseJobModel create(CharacterModel character) {
        ItemFilter filter = ItemFilter.createItemFilter();
        filter.effectEnergy = true;

        // Get nearest bed
        MapObjectModel item = ((ItemFinder)Game.getInstance().getModule(ItemFinder.class)).getNearest(filter, character);
        if (item == null) {
            return null;
        }

        if (item instanceof ItemModel) {
            return JobUse.create((ItemModel)item, character);
        }

        if (item instanceof ConsumableModel) {
            return JobConsume.create(character, (ConsumableModel)item);
        }

        return null;
    }
}
