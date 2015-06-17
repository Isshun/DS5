package org.smallbox.faraway.model.check.old;

import org.smallbox.faraway.model.character.base.CharacterModel;
import org.smallbox.faraway.model.job.JobModel;

public abstract class CharacterCheck {
	public abstract JobModel create(CharacterModel character);
	public abstract boolean check(CharacterModel character);
}
