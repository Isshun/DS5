package org.smallbox.faraway.core.game.module.job.check.joy;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.TalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;

/**
 * Created by Alex on 02/07/2015.
 */
public class JobTalk extends JobModel {
    private CharacterModel  _friend;

    public static JobTalk create(CharacterModel character, CharacterModel friend) {

        if (friend.getJob() != null && friend.getJob().isEntertainment()) {
            ModuleHelper.getJobModule().closeJob(friend.getJob());
        }

        PathModel path = PathManager.getInstance().getPath(character.getParcel(), friend.getParcel());
        if (path == null) {
            return null;
        }

        JobTalk job = new JobTalk();
        job._friend = friend;
        job._character = character;
        job._cost = 32;
        job._message = "Talk job";

        friend.setJob(job);
        character.setJob(job);

        character.moveTo(job, path.getNodes().get(path.getLength() / 2), null);
        friend.moveTo(job, path.getNodes().get(Math.min(path.getLength() / 2 + 1, path.getLength() - 1)), null);

        return job;
    }

    @Override
    public boolean hasCharacter(CharacterModel character) {
        return _character != null && _friend != null && (_character == character || _friend == character);
    }

    @Override
    public String getShortLabel() {
        return null;
    }

    @Override
    public ParcelModel getActionParcel() {
        return null;
    }

    @Override
    protected void onStart(CharacterModel character) {
    }

    @Override
    public TalentExtra.TalentType getTalentNeeded() {
        return null;
    }

    @Override
    public void onQuit(CharacterModel character) {

    }

    @Override
    public boolean onCheck(CharacterModel character) {
        return false;
    }

    @Override
    protected void onFinish() {

    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (_progress++ < _cost) {
            return JobActionReturn.CONTINUE;
        }
        return JobActionReturn.FINISH;
    }
}
