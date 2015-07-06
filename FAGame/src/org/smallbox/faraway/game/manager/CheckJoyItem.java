package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ItemFilter;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobUse;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyItem extends CharacterCheck {

    @Override
    public BaseJobModel create(CharacterModel character) {
        MapObjectModel item = getItem(character);
        if (item != null) {
            JobUse job = JobUse.create(item, character);
            job.start(character);
            job.setCharacterRequire(character);
            return job;
        }
        return null;
    }

    private MapObjectModel getItem(CharacterModel character) {
        ItemFilter filter = ItemFilter.createUsableFilter();
        filter.effectJoy = true;
        return ((WorldFinder)Game.getInstance().getManager(WorldFinder.class)).getRandomNearest(filter, character);
    }

    @Override
    public boolean check(CharacterModel character) {
        return getItem(character) != null;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getNeeds().joy < character.getType().needs.joy.warning;
    }
}

