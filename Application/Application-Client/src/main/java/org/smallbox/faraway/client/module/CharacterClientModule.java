package org.smallbox.faraway.client.module;

import org.json.JSONObject;
import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.core.dependencyInjector.GameObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
public class CharacterClientModule {

    public Map<Long, CharacterCommon> characters = new ConcurrentHashMap<>();

    public void onReceiveCharacter(String data) {
        JSONObject object = new JSONObject(data);

        long id = object.getLong("id");

        CharacterCommon character = characters.get(id);
        if (character == null) {
            character = new CharacterCommon();
            character.id = id;
            characters.put(id, character);
        }

        character.name = object.getString("name");
        character.parcelX = object.getInt("parcelX");
        character.parcelY = object.getInt("parcelY");
        character.parcelZ = object.getInt("parcelZ");
    }
}
