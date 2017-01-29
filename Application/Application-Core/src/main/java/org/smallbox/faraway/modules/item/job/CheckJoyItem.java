package org.smallbox.faraway.modules.item.job;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ItemFilter;
import org.smallbox.faraway.modules.item.ItemFinderModule;
import org.smallbox.faraway.modules.item.UsableItem;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyItem extends CharacterCheck {

    @Override
    public JobModel onCreateJob(CharacterModel character) {
        UsableItem item = getItem(character);
        if (item != null) {
            UseJob job = UseJob.create(character, item);
            if (job != null) {
                job.start(character);
                job.setCharacterRequire(character);
            }
            return job;
        }
        return null;
    }

    private UsableItem getItem(CharacterModel character) {
        ItemFilter filter = ItemFilter.createUsableFilter();
        filter.effectEntertainment = true;
        return (UsableItem)((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getRandomNearest(filter, character);
    }

    @Override
    public boolean isJobLaunchable(CharacterModel character) {
        return getItem(character) != null;
    }

    @Override
    public boolean isJobNeeded(CharacterModel character) {
        return character.getNeeds().get("entertainment") < character.getType().needs.joy.warning;
    }
}

