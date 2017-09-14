package org.smallbox.faraway.core;

public interface ModelSerializer<T> {
    String serialize(T model);
}
