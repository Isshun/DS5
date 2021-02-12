package org.smallbox.faraway.game.characterDisease;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.SuperGameModule2;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.character.CharacterModuleObserver;
import org.smallbox.faraway.game.character.model.base.CharacterDiseasesExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

import java.util.Collection;

@GameObject
public class CharacterDiseaseModule extends SuperGameModule2<CharacterModuleObserver> {
    @Inject private CharacterModule characterModule;

    @Override
    public void onGameUpdate() {
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
