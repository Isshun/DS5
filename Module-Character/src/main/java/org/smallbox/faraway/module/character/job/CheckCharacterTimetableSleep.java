package org.smallbox.faraway.module.character.job;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.module.item.ItemFinderModule;
import org.smallbox.faraway.module.item.SleepJob;
import org.smallbox.faraway.module.item.item.ItemModel;

/**
 * Created by Alex on 25/10/2015.
 */
public class CheckCharacterTimetableSleep extends CharacterCheck {
    private final ItemFilter bedFilter;

    public CheckCharacterTimetableSleep() {
        bedFilter = ItemFilter.createItemFilter();
        bedFilter.effectEnergy = true;
        bedFilter.needItem = true;
        bedFilter.needFreeSlot = true;
    }

    @Override
    public JobModel onCreateJob(CharacterModel character) {
        ItemFinderModule finder = (ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class);
        ItemModel item = (ItemModel)finder.getNearest(bedFilter, character);
        if (item != null) {
            SleepJob job = new SleepJob(item.getParcel(), item);
            job.setCharacterRequire(character);
            return job;
        }

        return null;
    }

    @Override
    public boolean isJobLaunchable(CharacterModel character) {
        ItemFinderModule finder = (ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class);
        return finder.getNearest(bedFilter, character) != null;
    }

    @Override
    public boolean isJobNeeded(CharacterModel character) {
        return character.getTimetable().get(Application.gameManager.getGame().getHour()) == 1;
    }
}
