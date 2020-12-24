package org.smallbox.faraway.core.game;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface GameObserverPriority {
    enum Priority {
        LOW,
        REGULAR,
        HIGH
    }

    Priority value();
}
