package org.smallbox.faraway.module.character.job;

import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.module.item.ItemModule;
import org.smallbox.faraway.module.item.job.UseJob;
import org.smallbox.faraway.module.item.UsableItem;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoySleep extends CharacterCheck {
    private final ItemModule _items;

    public CheckJoySleep(ItemModule items) {
        _items = items;
    }

    @Override
    public JobModel onCreateJob(CharacterModel character) {
        if (character != null) {
            UsableItem item = _items.getItems().stream().filter(UsableItem::isBed).findAny().orElse(null);
            if (item != null) {
                UseJob job = UseJob.create(item);
                if (job != null) {
                    job.setCharacterRequire(character);
                    return job;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isJobLaunchable(CharacterModel character) {
        return _items.getItems().stream().filter(UsableItem::isBed).findAny().isPresent();
    }

    @Override
    public boolean isJobNeeded(CharacterModel character) {
        return character.getNeeds().get("energy") <= character.getType().needs.energy.warning;
    }
}
