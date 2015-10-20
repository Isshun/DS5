package org.smallbox.faraway.core.game.module.job.check.joy;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.module.path.PathManager;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyTalk extends CharacterCheck {
    @Override
    public JobModel create(CharacterModel character) {
        return JobTalk.create(character, getBestCharacter(character));
    }

    @Override
    public boolean check(CharacterModel character) {
        return getBestCharacter(character) != null;
    }

    private CharacterModel getBestCharacter(CharacterModel character) {
        int bestDistance = Integer.MAX_VALUE;
        CharacterModel bestCharacter = null;
        for (CharacterModel friend: ModuleHelper.getCharacterModule().getCharacters()) {
            if (friend != character && friend.isAlive() && (friend.getJob() == null || friend.getJob().isEntertainment())) {
                GraphPath<ParcelModel> path = PathManager.getInstance().getPath(character.getParcel(), friend.getParcel());
                if (path != null && path.getCount() < bestDistance) {
                    bestDistance = path.getCount();
                    bestCharacter = friend;
                }
            }
        }
        return bestCharacter;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getNeeds().relation < character.getType().needs.relation.critical;
    }
}
