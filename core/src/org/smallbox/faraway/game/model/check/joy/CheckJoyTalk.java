package org.smallbox.faraway.game.model.check.joy;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.path.PathManager;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyTalk extends CharacterCheck {
    @Override
    public BaseJobModel create(CharacterModel character) {
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
            if (friend != character && friend.isAlive() && (friend.getJob() == null || friend.getJob().isJoy())) {
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
