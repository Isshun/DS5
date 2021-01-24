package org.smallbox.faraway.game.characterBuff;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.engine.module.GenericGameModule;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.characterNeed.CharacterNeedModule;

import java.util.Collection;
import java.util.stream.Collectors;

@GameObject
public class CharacterBuffModule extends GenericGameModule<BuffModel> {
    @Inject private Game game;
    @Inject private DataManager dataManager;
    @Inject private CharacterModule characterModule;
    @Inject private GameTime gameTime;
    @Inject private CharacterNeedModule characterNeedModule;
    @Inject private DependencyManager dependencyManager;

    private Collection<BuffFactory> buffFactories;

    @OnInit
    private void init() {
        buffFactories = dependencyManager.getSubTypesOf(BuffFactory.class);
    }

    @Override
    protected void onModuleUpdate(Game game) {
        characterModule.getAll().forEach(this::addMissingBuffs);

        modelList.forEach(BuffModel::update);
        modelList.forEach(this::applyBuffsEffects);

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

    private void addMissingBuffs(CharacterModel character) {
        buffFactories.stream()
                .filter(buffFactory -> modelList.stream().noneMatch(buff -> isSame(buff, buffFactory, character)))
                .filter(buffFactory -> buffFactory.check(character))
                .forEach(buffFactory -> {
                    BuffModel buff = buffFactory.build();
                    buff.setCharacter(character);
                    add(buff);
                });
    }

    private boolean isSame(BuffModel buff, BuffFactory buffFactory, CharacterModel character) {
        return buff.getCharacter() == character && StringUtils.equals(buff.getName(), buffFactory.name());
    }

    private void applyBuffsEffects(BuffModel buff) {
    }

    public Collection<BuffModel> getBuffs(CharacterModel character) {
        return modelList.stream().filter(buff -> buff.getCharacter() == character).collect(Collectors.toList());
    }

    public double getMood(CharacterModel character) {
//        return getBuffs(character).stream().mapToInt(buff -> buff.mood).sum() / 100.0;
        return 50;
    }
}
