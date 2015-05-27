package org.smallbox.faraway.model.jobCheck;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.Character;

public interface CharacterCheck {
	boolean create(JobManager jobManager, Character character);
}
