package org.smallbox.faraway.model.check.old;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;

public abstract class CharacterCheck {
	protected final CharacterModel _character;

	public CharacterCheck(CharacterModel character) {
		_character = character;
	}

	public abstract boolean create(JobManager jobManager);
}
