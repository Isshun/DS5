package org.smallbox.faraway.game.character;

import org.json.JSONObject;
import org.smallbox.faraway.core.ModelSerializer;
import org.smallbox.faraway.game.character.model.HumanModel;

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
