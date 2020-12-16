package org.smallbox.faraway.core.dependencyInjector;

public class DependencyInfo<T> {
    boolean initialized;
    T dependency;

    public DependencyInfo(T dependency) {
        this.dependency = dependency;
    }
}
