package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ItemFilter;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobUse;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyItem extends CharacterCheck {
    private GameConfig.EffectValues _effects;

    public CheckJoyItem() {
        _effects = new GameConfig.EffectValues();
        _effects.joy = 0.25;
    }

    @Override
    public BaseJobModel create(CharacterModel character) {
        ItemModel item = getItem(character);
        if (item != null) {
            JobUse job = JobUse.create(item, character);
            job.setCharacter(character);
            job.setCharacterRequire(character);
            return job;
        }
        return null;
    }

    private ItemModel getItem(CharacterModel character) {
        ItemFilter filter = ItemFilter.createUsableFilter();
        filter.effectJoy = true;
        return (ItemModel) Game.getWorldManager().getFinder().getNearest(filter, character);
    }

    @Override
    public boolean check(CharacterModel character) {
        return getItem(character) != null;
    }
}

