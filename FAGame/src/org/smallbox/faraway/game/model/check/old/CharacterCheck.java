package org.smallbox.faraway.game.model.check.old;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.job.JobModel;

public abstract class CharacterCheck {
	public abstract JobModel create(CharacterModel character);
	public abstract boolean check(CharacterModel character);
}
