package org.smallbox.faraway.client.module;

import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.common.ParcelCommon;
import org.smallbox.faraway.common.dependencyInjector.GameObject;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
public class CharacterClientModule {

    public Map<Long, CharacterCommon> characters = new ConcurrentHashMap<>();

    public void update(CharacterCommon character) {
        characters.put(character.id, character);
    }

    public Collection<CharacterCommon> getCharacters() {
        return characters.values();
    }

    public CharacterCommon getCharacter(ParcelCommon parcel) {
        return characters.values().stream()
                .filter(character -> character.parcelX == parcel.x && character.parcelY == parcel.y && character.parcelZ == character.parcelZ)
                .findFirst()
                .orElse(null);
    }
}
