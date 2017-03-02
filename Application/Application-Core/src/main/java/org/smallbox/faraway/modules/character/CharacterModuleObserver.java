package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 20/07/2016.
 */
public interface CharacterModuleObserver extends ModuleObserver {
    default void onAddCharacter(CharacterModel character) {}
    default void onSelectCharacter(GameEvent event, CharacterModel character) {}
}
