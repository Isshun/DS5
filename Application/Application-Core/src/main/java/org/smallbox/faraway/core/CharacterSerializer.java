package org.smallbox.faraway.core;

import org.json.JSONObject;
import org.smallbox.faraway.modules.character.model.HumanModel;

public class CharacterSerializer implements ModelSerializer<HumanModel> {

    @Override
    public String serialize(HumanModel model) {
        return new JSONObject()
                .put("id", model.getId())
                .put("name", model.getName())
                .put("parcelX", model.position.parcelX)
                .put("parcelY", model.position.parcelY)
                .put("parcelZ", model.position.parcelZ)
                .toString();
    }

}
