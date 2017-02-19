package org.smallbox.faraway.core;

import org.smallbox.faraway.core.engine.GameEventListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Alex on 12/01/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GameShortcut {

    interface GameShortcutCallback {
        void onGameShortcut();
    }

    GameEventListener.Key key();
}
