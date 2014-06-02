package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.JobUse;

// TODO: change name by filter
public class CharacterIsTired implements JobCheck {

	@Override
	public Job create(JobManager jobManager, Character character) {
		if (character.getNeeds().isTired()) {
			ItemBase item = ServiceManager.getWorldMap().find("base.bed", true);
			if (item != null) {
				return JobUse.create(item, character);
			}
		}
		
		return null;
	}

}
