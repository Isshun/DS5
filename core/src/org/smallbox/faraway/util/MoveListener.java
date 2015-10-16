package org.smallbox.faraway.util;

import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;

/**
 * Created by Alex on 08/06/2015.
 */
public interface MoveListener<T extends MovableModel> {
    void onReach(T movable);
    void onFail(T movable);
    void onSuccess(T movable);
}
