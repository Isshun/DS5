package org.smallbox.faraway.model.check.character;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.model.character.base.CharacterModel;
import org.smallbox.faraway.model.check.old.CharacterCheck;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.item.MapObjectModel;
import org.smallbox.faraway.model.job.JobModel;
import org.smallbox.faraway.model.job.JobUse;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckCharacterExhausted extends CharacterCheck {

    @Override
    public boolean check(CharacterModel character) {
        return character.getNeeds().isExhausted();
    }

    @Override
    public JobModel create(CharacterModel character) {
        ItemFilter filter = ItemFilter.createUsableFilter();
        filter.effectEnergy = true;

        // Get nearest bed
        MapObjectModel item = Game.getWorldFinder().getNearest(filter, character);
        if (item == null) {
            return null;
        }

        return JobUse.create(item, character);
    }
}
