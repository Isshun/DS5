package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.game.GameSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleSerializer {
    Class<? extends GameSerializer> value();
}
