package org.smallbox.faraway.modules.room.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Alex on 08/03/2017.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RoomTypeInfo {
    String label();
}