package org.smallbox.faraway.client.module;

import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.core.dependencyInjector.GameObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
public class CharacterClientModule {

    public Map<Long, CharacterCommon> characters = new ConcurrentHashMap<>();

    public void update(CharacterCommon character) {
        characters.put(character.id, character);
    }
}
