package org.smallbox.faraway.game;

import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;

/**
 * Created by Alex on 08/06/2015.
 */
public interface OnMoveListener<T extends MovableModel> {
    void onReach(BaseJobModel job, T movable);
    void onFail(BaseJobModel job, T movable);
    void onSuccess(BaseJobModel job, T movable);
}
