package org.smallbox.faraway.game.model.check.joy;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.path.PathManager;

/**
 * Created by Alex on 02/07/2015.
 */
public class JobTalk extends BaseJobModel {
    private CharacterModel  _friend;

    public static JobTalk create(CharacterModel character, CharacterModel friend) {

        if (friend.getJob() != null && friend.getJob().isJoy()) {
            Game.getJobManager().closeJob(friend.getJob());
        }

        GraphPath<ParcelModel> path = PathManager.getInstance().getPath(character.getParcel(), friend.getParcel());
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

        character.moveTo(job, path.get(path.getCount() / 2), null);
        friend.moveTo(job, path.get(Math.min(path.getCount() / 2 + 1, path.getCount() - 1)), null);

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
    public CharacterModel.TalentType getTalentNeeded() {
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
