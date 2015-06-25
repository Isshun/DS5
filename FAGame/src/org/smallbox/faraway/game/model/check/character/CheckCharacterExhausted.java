package org.smallbox.faraway.game.model.check.character;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.WorldFinder;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ItemFilter;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobUse;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckCharacterExhausted extends CharacterCheck {

    @Override
    public boolean check(CharacterModel character) {
        return character.getNeeds().isExhausted();
    }

    @Override
    public BaseJobModel create(CharacterModel character) {
        ItemFilter filter = ItemFilter.createUsableFilter();
        filter.effectEnergy = true;

        // Get nearest bed
        MapObjectModel item = ((WorldFinder)Game.getInstance().getManager(WorldFinder.class)).getNearest(filter, character);
        if (item == null) {
            return null;
        }

        return JobUse.create(item, character);
    }
}
