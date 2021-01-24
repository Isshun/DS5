package org.smallbox.faraway.game.characterBuff.impl;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.characterBuff.BuffFactory;
import org.smallbox.faraway.game.characterBuff.BuffModel;
import org.smallbox.faraway.game.characterBuff.BuffType;

@GameObject
public class StarterBuff extends BuffFactory {

    @Inject private GameTime gameTime;

    @Override
    protected BuffModel build() {
        BuffModel buff = new BuffModel();

        buff.setName(name());
        buff.setBuffType(BuffType.BUFF);
        buff.addLevel(1, "Moderatly excited by new colony", 5, character -> gameTime.getDay() < 10);
        buff.addLevel(2, "Excited by new colony", 15, character -> gameTime.getDay() < 5);

        return buff;
    }

    @Override
    protected boolean check(CharacterModel character) {
        return gameTime.getDay() < 10;
    }

    @Override
    protected String name() {
        return "base.buff.starter";
    }

}
