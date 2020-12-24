package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

public interface CharacterModuleObserver extends ModuleObserver {
    default void onAddCharacter(CharacterModel character) {}
}
