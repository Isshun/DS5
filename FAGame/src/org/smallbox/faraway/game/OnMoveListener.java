package org.smallbox.faraway.game;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;

/**
 * Created by Alex on 08/06/2015.
 */
public interface OnMoveListener {
    void onReach(BaseJobModel job, CharacterModel character);
    void onFail(BaseJobModel job, CharacterModel character);
    void onSuccess(BaseJobModel job, CharacterModel character);
}
