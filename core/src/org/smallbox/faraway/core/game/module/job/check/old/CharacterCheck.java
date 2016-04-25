package org.smallbox.faraway.core.game.module.job.check.old;

import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;

public abstract class CharacterCheck {
    public abstract JobModel create(CharacterModel character);
    public abstract boolean check(CharacterModel character);
    public abstract boolean need(CharacterModel character);
}
