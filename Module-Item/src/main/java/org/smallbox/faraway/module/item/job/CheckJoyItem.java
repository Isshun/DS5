package org.smallbox.faraway.module.item.job;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ItemFilter;
import org.smallbox.faraway.module.item.ItemFinderModule;
import org.smallbox.faraway.module.item.UseJob;
import org.smallbox.faraway.module.item.item.ItemModel;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyItem extends CharacterCheck {

    @Override
    public JobModel onCreateJob(CharacterModel character) {
        ItemModel item = getItem(character);
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

    private ItemModel getItem(CharacterModel character) {
        ItemFilter filter = ItemFilter.createUsableFilter();
        filter.effectEntertainment = true;
        return (ItemModel)((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getRandomNearest(filter, character);
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

