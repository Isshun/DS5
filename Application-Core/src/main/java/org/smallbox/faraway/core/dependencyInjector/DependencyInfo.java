package org.smallbox.faraway.core.dependencyInjector;

public class DependencyInfo<T> {
    public boolean initialized;
    public T dependency;

    public DependencyInfo(T dependency) {
        this.dependency = dependency;
    }
}
