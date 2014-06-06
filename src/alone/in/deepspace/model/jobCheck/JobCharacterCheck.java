package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.character.Character;

public interface JobCharacterCheck {
	boolean create(JobManager jobManager, Character character);
}
