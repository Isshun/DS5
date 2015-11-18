package org.smallbox.faraway.core.data;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.model.ObjectInfo;

/**
 * Created by Alex on 04/11/2015.
 */
public class BindingInfo extends ObjectInfo {
    public interface BindingCheckInterface {
        boolean onCheck();
    }

    public interface BindingActionInterface {
        void onAction();
    }

    public GameEventListener.Key        key;
    public GameEventListener.Modifier   modifier = GameEventListener.Modifier.NONE;
    public String                       label;
    public BindingCheckInterface        check;
    public BindingActionInterface       action;

    public void action() {
        if (check.onCheck()) {
            action.onAction();
        }
    }
}
