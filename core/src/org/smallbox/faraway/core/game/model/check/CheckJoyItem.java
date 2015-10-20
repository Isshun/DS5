package org.smallbox.faraway.core.game.model.check;

import org.smallbox.faraway.core.game.helper.ItemFinder;
import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.model.item.ItemFilter;
import org.smallbox.faraway.core.game.model.item.ItemModel;
import org.smallbox.faraway.core.game.model.job.UseJob;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;
import org.smallbox.faraway.core.game.module.ModuleManager;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyItem extends CharacterCheck {

    @Override
    public JobModel create(CharacterModel character) {
        ItemModel item = getItem(character);
        if (item != null) {
            UseJob job = UseJob.create(item, character);
            job.start(character);
            job.setCharacterRequire(character);
            return job;
        }
        return null;
    }

    private ItemModel getItem(CharacterModel character) {
        ItemFilter filter = ItemFilter.createUsableFilter();
        filter.effectEntertainment = true;
        return (ItemModel)((ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class)).getRandomNearest(filter, character);
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

