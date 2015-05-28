package org.smallbox.faraway.model.jobCheck;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;

public interface CharacterCheck {
	boolean create(JobManager jobManager, CharacterModel character);
}
