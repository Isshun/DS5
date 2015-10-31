package org.smallbox.faraway.core.game.module.job.check;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.ItemFinder;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.SleepJob;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.module.java.ModuleManager;

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
    public JobModel create(CharacterModel character) {
        // TODO: magic
        int bedHour = Game.getInstance().getHour();
        int wakeTime = 0;
        for (int i = 0; i < 8; i++) {
            if (character.getTimetable().get(bedHour + i) != 1) {
                wakeTime = i;
            }
        }

        ItemFinder finder = (ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class);
        ItemModel item = (ItemModel)finder.getNearest(bedFilter, character);
        if (item != null) {
            SleepJob job = new SleepJob(item.getParcel(), item);
            job.setWakeTime(wakeTime);
            return job;
        }

        return null;
    }

    @Override
    public boolean check(CharacterModel character) {
        ItemFinder finder = (ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class);
        return finder.getNearest(bedFilter, character) != null;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getTimetable().get(Game.getInstance().getHour()) == 1;
    }
}
