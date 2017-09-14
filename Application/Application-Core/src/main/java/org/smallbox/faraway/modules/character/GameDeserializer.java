package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.common.GameTaskDeserializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 300206 on 14/09/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GameDeserializer {
    Class<GameTaskDeserializer> value();
}
