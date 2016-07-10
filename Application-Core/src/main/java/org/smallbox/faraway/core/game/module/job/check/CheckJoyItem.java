package org.smallbox.faraway.core.game.module.job.check;

import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.helper.ItemFinder;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.UseJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyItem extends CharacterCheck {

    @Override
    public JobModel create(CharacterModel character) {
        ItemModel item = getItem(character);
        if (item != null) {
            UseJob job = UseJob.create(character, item);
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
        return character.getNeeds().get("entertainment") < character.getType().needs.joy.warning;
    }
}

