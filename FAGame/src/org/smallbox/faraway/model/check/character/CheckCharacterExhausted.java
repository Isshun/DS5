package org.smallbox.faraway.model.check.character;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.check.old.CharacterCheck;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.job.JobUse;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckCharacterExhausted extends CharacterCheck {
    public CheckCharacterExhausted(CharacterModel character) {
        super(character);
    }

    @Override
    public boolean create(JobManager jobManager) {
        if (_character.getNeeds().isTired()) {
            ItemFilter filter = ItemFilter.createUsableFilter();
            filter.effectEnergy = true;

            // Character has quarters
            if (_character.getQuarter() != null) {
                ItemBase item = _character.getQuarter().find(filter);
                if (item != null) {
                    jobManager.addJob(JobUse.create(item, _character), _character);
                    return true;
                }
            }

            // No quarters or no usable bed in quarters
            ItemBase item = Game.getWorldFinder().getNearest(filter, _character);
            if (item != null) {
                jobManager.addJob(JobUse.create(item, _character), _character);
                return true;
            }
        }

        return false;

    }
}
