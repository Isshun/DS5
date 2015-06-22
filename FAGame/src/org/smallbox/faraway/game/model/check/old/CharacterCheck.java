package org.smallbox.faraway.game.model.check.old;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;

public abstract class CharacterCheck {
	public abstract BaseJobModel create(CharacterModel character);
	public abstract boolean check(CharacterModel character);
}
