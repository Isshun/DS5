package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.job.Job;

public interface JobCheck {
	Job create(JobManager jobManager, Character character);
	// TODO: new signature
	// boolean create(JobManager jobManager, Character character);
}
