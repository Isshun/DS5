package org.smallbox.faraway;

import org.smallbox.faraway.core.ModelSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 300206 on 14/09/2017.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameSerializer {
    Class<? extends ModelSerializer> value();
}
