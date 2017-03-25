package org.smallbox.faraway.client;

import org.smallbox.faraway.client.render.layer.BaseLayer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Alex on 18/11/2016.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleLayer {
    Class<? extends BaseLayer>[] value();
}
