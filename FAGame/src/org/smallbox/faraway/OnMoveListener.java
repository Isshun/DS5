package org.smallbox.faraway;

import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.job.JobModel;

/**
 * Created by Alex on 08/06/2015.
 */
public interface OnMoveListener {
    void onReach(JobModel job, CharacterModel character);
    void onFail(JobModel job, CharacterModel character);
}
