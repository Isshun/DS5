package org.smallbox.faraway.modules.characterDisease;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.SuperGameModule2;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.CharacterModuleObserver;
import org.smallbox.faraway.modules.character.model.base.CharacterDiseasesExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Collection;

@GameObject
public class CharacterDiseaseModule extends SuperGameModule2<CharacterModuleObserver> {

    @Inject
    private CharacterModule characterModule;

    @Override
    public void onModuleUpdate(Game game) {
    }

    public void addDisease(DiseaseInfo info, CharacterModel character) {
        if (character.hasExtra(CharacterDiseasesExtra.class)) {
            character.getExtra(CharacterDiseasesExtra.class).addDisease(new CharacterDisease(info, character));
        }
    }

    public Collection<CharacterDisease> getDiseases(CharacterModel character) {
        if (character.hasExtra(CharacterDiseasesExtra.class)) {
            return character.getExtra(CharacterDiseasesExtra.class).getAll();
        }
        return null;
    }
}
