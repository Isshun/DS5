package org.smallbox.faraway.core.dependencyInjector.handler;

/**
 * Created by Alex on 13/01/2017.
 */
public abstract class ComponentHandler<T> {
    public abstract void invoke(Class<T> cls);
}
