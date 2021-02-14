package org.smallbox.faraway.core.game.modelInfo;

import org.smallbox.faraway.client.input.KeyModifier;

public class BindingInfo extends ObjectInfo {
    public interface BindingCheckInterface {
        boolean onCheck();
    }

    public interface BindingActionInterface {
        void onAction();
    }

    public int key;
    public KeyModifier keyModifier = KeyModifier.NONE;
    public String                       label;
    public BindingCheckInterface        check;
    public BindingActionInterface       action;

    public void action() {
        if (check.onCheck()) {
            action.onAction();
        }
    }
}
