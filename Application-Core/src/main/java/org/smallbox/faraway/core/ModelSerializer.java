package org.smallbox.faraway.core;

import org.json.JSONObject;

public interface ModelSerializer<T> {
    JSONObject serialize(T model);
}
