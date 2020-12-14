package org.smallbox.faraway.core;

import org.json.JSONObject;
import org.smallbox.faraway.modules.character.model.HumanModel;

public class CharacterSerializer implements ModelSerializer<HumanModel> {

    @Override
    public JSONObject serialize(HumanModel model) {
        return new JSONObject()
                .put("id", model.getId())
                .put("name", model.getName())
                .put("parcelX", model._parcel.x)
                .put("parcelY", model._parcel.y)
                .put("parcelZ", model._parcel.z);
    }

}
