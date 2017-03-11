package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

@ModuleSerializer(CharacterModuleSerializer.class)
public class CharacterRelationModule extends GameModule {

    @BindModule
    private CharacterModule characterModule;

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    @Override
    public void onModuleUpdate(Game game) {
        characterModule.getCharacters().forEach(this::updateCharacter);
    }

    private void updateCharacter(CharacterModel character) {
    }

    public int getScore(CharacterModel character) {
        if (characterModule.havePeopleOnProximity(character)) {
            return 10;
        }
        return -1;
    }
}
