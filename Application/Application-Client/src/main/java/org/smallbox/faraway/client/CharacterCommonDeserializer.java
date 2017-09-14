package org.smallbox.faraway.client;

import org.json.JSONObject;
import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.common.ModelDeserializer;

public class CharacterCommonDeserializer implements ModelDeserializer<CharacterCommon> {

    @Override
    public CharacterCommon deserialize(JSONObject object) {
        long id = object.getLong("id");

        CharacterCommon character = new CharacterCommon();
        character.id = id;

        character.name = object.getString("name");
        character.parcelX = object.getInt("parcelX");
        character.parcelY = object.getInt("parcelY");
        character.parcelZ = object.getInt("parcelZ");

        return character;
    }

}
