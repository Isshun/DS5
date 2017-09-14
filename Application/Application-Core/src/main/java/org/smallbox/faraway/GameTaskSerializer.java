package org.smallbox.faraway;

import org.json.JSONObject;
import org.smallbox.faraway.core.ModelSerializer;

/**
 * Created by 300206 on 14/09/2017.
 */
public class GameTaskSerializer implements ModelSerializer<GameTask> {

    @Override
    public JSONObject serialize(GameTask model) {
        return new JSONObject()
                .put("id", model.id)
                .put("label", model.label)
                .put("name", model.getClass().getName())
                .put("elapsed", model.elapsed)
                .put("duration", model.duration);
    }

}
