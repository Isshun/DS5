package org.smallbox.faraway.game;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.job.JobModel;

/**
 * Created by Alex on 08/06/2015.
 */
public interface OnMoveListener {
    void onReach(JobModel job, CharacterModel character);
    void onFail(JobModel job, CharacterModel character);
}
