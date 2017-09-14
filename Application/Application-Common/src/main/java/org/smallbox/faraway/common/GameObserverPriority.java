package org.smallbox.faraway.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Alex on 04/12/2016.
 */
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
