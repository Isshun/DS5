package org.smallbox.faraway.common;

import org.json.JSONObject;

/**
 * Created by 300206 on 14/09/2017.
 */
public interface ModelDeserializer<T> {
    T deserialize(JSONObject jsonObject);
}
