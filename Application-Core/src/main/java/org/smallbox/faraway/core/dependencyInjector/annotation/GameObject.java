package org.smallbox.faraway.core.dependencyInjector.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GameObject are objects handled by dependency injector who are created during the game start and destroyed when game is closed.
 * Use the same behavior that ApplicationObject but with different lifetime.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GameObject {}
