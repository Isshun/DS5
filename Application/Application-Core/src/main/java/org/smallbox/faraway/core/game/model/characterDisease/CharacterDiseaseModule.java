package org.smallbox.faraway.core.game.model.characterDisease;

import org.smallbox.faraway.common.GameModule;
import org.smallbox.faraway.common.dependencyInjector.BindComponent;
import org.smallbox.faraway.common.dependencyInjector.GameObject;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.CharacterModuleObserver;
import org.smallbox.faraway.modules.character.model.base.CharacterDiseasesExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Collection;

@GameObject
public class CharacterDiseaseModule extends GameModule<CharacterModuleObserver> {

    @BindComponent
    private CharacterModule characterModule;

    @Override
    public void onModuleUpdate() {
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
