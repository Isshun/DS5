package org.smallbox.faraway.core.save;

import org.json.JSONObject;

public interface ModelSerializer<T> {
    JSONObject serialize(T model);
}
