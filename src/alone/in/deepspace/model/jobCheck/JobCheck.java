package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.job.Job;

public interface JobCheck {
	void check(JobManager jobManager, Character character);
}
