package org.smallbox.faraway.modules.buff;

import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.BuffInfo;
import org.smallbox.faraway.modules.character.model.BuffModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alex on 16/06/2015.
 */
public class BuffModule extends GameModule {

    @BindComponent
    private Data data;

    @BindModule
    private CharacterModule characterModule;

    private Map<CharacterModel, Map<BuffInfo, BuffModel>> _characters = new ConcurrentHashMap<>();

    @Override
    protected void onModuleUpdate(Game game) {

        // Ajoute les personnages manquant
        characterModule.getCharacters().forEach(character -> {
            if (!_characters.containsKey(character)) {
                _characters.put(character, new ConcurrentHashMap<>());
            }
        });

        // Check les buffs de chaque personnage
        _characters.forEach(this::addMissingBuffs);
        _characters.forEach(this::updateBuffs);
    }

    /**
     * Fait progresser chaque buff jusqu'à son niveau max
     *
     * @param character CharacterModel
     * @param buffs Map
     */
    private void updateBuffs(CharacterModel character, Map<BuffInfo, BuffModel> buffs) {
        buffs.forEach((buffInfo, buff) -> {
            BuffInfo.BuffLevelInfo maxLevel = getMaxLevel(character, buffInfo);
            if (maxLevel != null) {
                buff.level = maxLevel.level;
                buff.message = maxLevel.message;
                buff.mood = maxLevel.mood;
            }
        });
    }

    /**
     * Ajoute / supprime les buffs pour le personnage
     *
     * @param character CharacterModel
     * @param buffs Map
     */
    private void addMissingBuffs(CharacterModel character, Map<BuffInfo, BuffModel> buffs) {
        data.buffs.forEach(buffInfo -> {

            // Test le niveau max pour ce personnage
            BuffInfo.BuffLevelInfo maxLevelInfo = getMaxLevel(character, buffInfo);

            // Ajout le buff si le niveau max existe mais que le personnage n'a pas encore le buff
            if (maxLevelInfo != null && !buffs.containsKey(buffInfo)) {
                buffs.put(buffInfo, new BuffModel(buffInfo, character));
            }

            // Supprime le buff si le niveau max n'existe pas et que le personnage l'a
            if (maxLevelInfo == null && buffs.containsKey(buffInfo)) {
                buffs.remove(buffInfo);
            }

        });
    }

    private BuffInfo.BuffLevelInfo getMaxLevel(CharacterModel character, BuffInfo buffInfo) {
        int level = buffInfo.handler.getLevel(character);
        for (BuffInfo.BuffLevelInfo levelInfo: buffInfo.levels) {
            if (levelInfo.level == level) {
                return levelInfo;
            }
        }
        return null;
    }

    public Collection<BuffModel> getBuffs(CharacterModel character) {
        return _characters.get(character).values();
    }
}
