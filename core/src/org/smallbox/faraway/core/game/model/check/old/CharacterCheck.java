package org.smallbox.faraway.core.game.model.check.old;

import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;

public abstract class CharacterCheck {
    public abstract JobModel create(CharacterModel character);
    public abstract boolean check(CharacterModel character);
    public abstract boolean need(CharacterModel character);
}
