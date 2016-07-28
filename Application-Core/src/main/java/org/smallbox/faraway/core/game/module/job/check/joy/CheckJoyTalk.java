package org.smallbox.faraway.core.game.module.job.check.joy;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyTalk extends CharacterCheck {
    @Override
    public JobModel onCreateJob(CharacterModel character) {
//        return JobTalk.createGame(character, getBestCharacter(character));
        return null;
    }

    @Override
    public boolean isJobLaunchable(CharacterModel character) {
//        return getBestCharacter(character) != null;
        return false;
    }

    private CharacterModel getBestCharacter(CharacterModel character) {
        throw new NotImplementedException("");

//        int bestDistance = Integer.MAX_VALUE;
//        CharacterModel bestCharacter = null;
//        for (CharacterModel friend: ModuleHelper.getCharacterModule().getCharacters()) {
//            if (friend != character && friend.isAlive() && (friend.getJob() == null || friend.getJob().isEntertainment())) {
//                PathModel path = PathManager.getInstance().getPath(character.getParcel(), friend.getParcel(), true, false);
//                if (path != null && path.getLength() < bestDistance) {
//                    bestDistance = path.getLength();
//                    bestCharacter = friend;
//                }
//            }
//        }
//        return bestCharacter;
    }

    @Override
    public boolean isJobNeeded(CharacterModel character) {
        return character.getNeeds().get("relation") < character.getType().needs.relation.critical;
    }
}
