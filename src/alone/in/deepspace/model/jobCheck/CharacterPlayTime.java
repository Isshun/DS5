package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Job;
import alone.in.deepspace.model.UserItem;
import alone.in.deepspace.util.Constant;

// Play with random object
public class CharacterPlayTime implements JobCheck {

	@Override
	public void check(JobManager jobManager, Character character) {
		if ((int)(Math.random() * 100) <= Constant.CHANCE_TO_GET_MEETING_AREA_WHEN_JOBLESS) {
			return;
		}

		UserItem toy = ServiceManager.getWorldMap().getRandomToy(character.getX(), character.getY());
		if (toy != null) {
			Job job = jobManager.createUseJob(toy);
			if (job != null) {
				character.setJob(job);
				jobManager.addJob(job);
			}

		}
		return;
	}

}
