package org.smallbox.faraway.modules.characterBuff;

import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.characterNeed.CharacterNeedModule;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
public class CharacterBuffModule extends GameModule {

    @Inject
    private Game game;

    @Inject
    private Data data;

    @Inject
    private CharacterModule characterModule;

    @Inject
    private CharacterNeedModule characterNeedModule;

    private Map<CharacterModel, Map<BuffInfo, CharacterBuff>> _characters = new ConcurrentHashMap<>();

    @Override
    protected void onModuleUpdate(Game game) {
        //TODO
//
//        // Ajoute les personnages manquant
//        characterModule.getCharacters().forEach(character -> {
//            if (!_characters.containsKey(character)) {
//                _characters.put(character, new ConcurrentHashMap<>());
//            }
//        });
//
//        // Check les buffs de chaque personnage
//        _characters.forEach(this::addMissingBuffs);
//        _characters.forEach(this::updateBuffs);
//        _characters.forEach(this::applyBuffsEffects);
    }

    /**
     * Ajoute / supprime les buffs pour le personnage
     *
     * @param character CharacterModel
     * @param buffs Map
     */
    private void addMissingBuffs(CharacterModel character, Map<BuffInfo, CharacterBuff> buffs) {
        data.buffs.forEach(buffInfo -> {

            // Test le niveau max pour ce personnage
            BuffInfo.BuffLevelInfo maxLevelInfo = getMaxLevel(character, buffInfo);

            // Ajout le buff si le niveau max existe mais que le personnage n'a pas encore le buff
            if (maxLevelInfo != null && !buffs.containsKey(buffInfo)) {
                buffs.put(buffInfo, new CharacterBuff(buffInfo, character));
            }

            // Supprime le buff si le niveau max n'existe pas et que le personnage l'a
            if (maxLevelInfo == null && buffs.containsKey(buffInfo)) {
                buffs.remove(buffInfo);
            }

        });
    }

    /**
     * Fait progresser chaque buff jusqu'à son niveau max
     *
     * @param character CharacterModel
     * @param buffs Map
     */
    private void updateBuffs(CharacterModel character, Map<BuffInfo, CharacterBuff> buffs) {
        buffs.forEach((buffInfo, buff) -> {
            BuffInfo.BuffLevelInfo maxLevel = getMaxLevel(character, buffInfo);
            if (maxLevel != null) {
                buff.levelInfo = maxLevel;
                buff.level = maxLevel.level;
                buff.message = maxLevel.message;
                buff.mood = maxLevel.mood;
            }
        });
    }

    /**
     * Applique au personnage les effets des buffs présents
     *
     * @param character CharacterModel
     * @param buffs Map
     */
    private void applyBuffsEffects(CharacterModel character, Map<BuffInfo, CharacterBuff> buffs) {
        buffs.forEach((info, buff) -> buff.levelInfo.effects.forEach(effect -> {

            // Apply need effect
            if (character.hasExtra(CharacterNeedsExtra.class)) {
                CharacterNeedsExtra needs = character.getExtra(CharacterNeedsExtra.class);
                if (needs != null) {
                    effect.needs.forEach((name, value) -> needs.addValue(name, game.byTick(value)));
                }
            }

        }));
    }

    private BuffInfo.BuffLevelInfo getMaxLevel(CharacterModel character, BuffInfo buffInfo) {
        if (buffInfo.handler != null) {
            int level = buffInfo.handler.getLevel(character);
            for (BuffInfo.BuffLevelInfo levelInfo : buffInfo.levels) {
                if (levelInfo.level == level) {
                    return levelInfo;
                }
            }
        }
        if (buffInfo.onGetLevel != null) {
            int level = buffInfo.onGetLevel.getLevel(character);
            for (BuffInfo.BuffLevelInfo levelInfo : buffInfo.levels) {
                if (levelInfo.level == level) {
                    return levelInfo;
                }
            }
        }
        return null;
    }

    public Collection<CharacterBuff> getBuffs(CharacterModel character) {
        return _characters.computeIfAbsent(character, k -> new ConcurrentHashMap<>()).values();
    }

    public double getMood(CharacterModel character) {
        return getBuffs(character).stream().mapToInt(buff -> buff.mood).sum() / 100.0;
    }
}
