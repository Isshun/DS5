package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.job.JobModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ModuleSerializer(CharacterModuleSerializer.class)
public class CharacterNeedModule extends GameModule {

    @BindModule
    private JobModule jobModule;

    @BindModule
    private CharacterModule characterModule;

    private Map<CharacterModel, CharacterNeedsExtra> _needs = new ConcurrentHashMap<>();

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    @Override
    public void onModuleUpdate(Game game) {

        // Ajoute les besoins pour les personnages manquants
        characterModule.getCharacters().stream()
                .filter(character -> !_needs.containsKey(character))
                .forEach(character -> _needs.put(character, new CharacterNeedsExtra(character, null)));

        _needs.forEach((character, need) -> need.update());
    }

    public CharacterNeedsExtra getNeed(CharacterModel character) {
        return _needs.get(character);
    }
}
