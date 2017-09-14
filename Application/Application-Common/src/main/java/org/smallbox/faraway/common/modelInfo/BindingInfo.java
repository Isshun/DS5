package org.smallbox.faraway.common.modelInfo;

import org.smallbox.faraway.common.GameEventListener;

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

    public int key;
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
