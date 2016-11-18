package org.smallbox.faraway.core;

import org.smallbox.faraway.core.data.serializer.GameSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Alex on 18/11/2016.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleSerializer {
    Class<? extends GameSerializer> value();
}
