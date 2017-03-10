package org.smallbox.faraway.modules.disease;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.CharacterModuleObserver;
import org.smallbox.faraway.modules.character.CharacterModuleSerializer;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleSerializer(CharacterModuleSerializer.class)
public class DiseaseModule extends GameModule<CharacterModuleObserver> {

    public Map<CharacterModel, Collection<DiseaseModel>> _diseases = new ConcurrentHashMap<>();

    @BindModule
    private CharacterModule characterModule;

    @Override
    public void onModuleUpdate(Game game) {
    }

    public void addDisease(DiseaseInfo info, CharacterModel character) {
        if (!_diseases.containsKey(character)) {
            _diseases.put(character, new ConcurrentLinkedQueue<>());
        }
        _diseases.get(character).add(new DiseaseModel(info, character));
    }

    public Collection<DiseaseModel> getDiseases(CharacterModel character) {
        return _diseases.get(character);
    }
}
