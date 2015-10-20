package org.smallbox.faraway.core.util;

import org.smallbox.faraway.core.game.model.MovableModel;

/**
 * Created by Alex on 08/06/2015.
 */
public interface MoveListener<T extends MovableModel> {
    void onReach(T movable);
    void onFail(T movable);
    void onSuccess(T movable);
}
