package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.job.JobUse;
import alone.in.deepspace.util.Constant;

// Play with random object
public class CharacterPlayTime implements JobCharacterCheck {

	@Override
	public boolean create(JobManager jobManager, Character character) {
		if ((int)(Math.random() * 100) <= Constant.CHANCE_TO_GET_MEETING_AREA_WHEN_JOBLESS) {
			return false;
		}

		UserItem toy = ServiceManager.getWorldMap().getRandomToy(character.getX(), character.getY());
		if (toy == null) {
			return false;
		}
		
		jobManager.addJob(JobUse.create(toy, character), character);
		return true;
	}
}
