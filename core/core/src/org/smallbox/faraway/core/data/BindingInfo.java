package org.smallbox.faraway.core.data;

import org.smallbox.faraway.core.engine.GameEventListener;

/**
 * Created by Alex on 04/11/2015.
 */
public class BindingInfo {
    public GameEventListener.Key        key;
    public GameEventListener.Modifier   modifier = GameEventListener.Modifier.NONE;
    public String                       label;
    public String                       command;
}
