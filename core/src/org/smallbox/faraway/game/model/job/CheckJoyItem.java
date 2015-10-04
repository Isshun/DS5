package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.helper.ItemFinder;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ItemFilter;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.module.ModuleManager;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyItem extends CharacterCheck {

    @Override
    public BaseJobModel create(CharacterModel character) {
        ItemModel item = getItem(character);
        if (item != null) {
            JobUse job = JobUse.create(item, character);
            job.start(character);
            job.setCharacterRequire(character);
            return job;
        }
        return null;
    }

    private ItemModel getItem(CharacterModel character) {
        ItemFilter filter = ItemFilter.createUsableFilter();
        filter.effectJoy = true;
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

