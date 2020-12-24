package org.smallbox.faraway.util;

import org.smallbox.faraway.core.game.model.MovableModel;

public interface MoveListener<T extends MovableModel> {
    void onReach(T movable);
    void onFail(T movable);
}
