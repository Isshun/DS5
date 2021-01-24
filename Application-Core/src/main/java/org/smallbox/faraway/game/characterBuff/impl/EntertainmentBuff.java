package org.smallbox.faraway.game.characterBuff.impl;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.game.characterBuff.BuffFactory;
import org.smallbox.faraway.game.characterBuff.BuffModel;
import org.smallbox.faraway.game.characterBuff.BuffType;

@GameObject
public class EntertainmentBuff extends BuffFactory {

    @Inject private GameTime gameTime;

    @Override
    protected BuffModel build() {
        BuffModel buff = new BuffModel();

        buff.setName(name());
        buff.setBuffType(BuffType.BUFF);
        buff.addLevel(1, "Have entertainment", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("entertainment") >= 0.3);
        buff.addLevel(2, "Have a lot of fun", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("entertainment") >= 0.6);

        return buff;
    }

    @Override
    protected boolean check(CharacterModel character) {
        return character.getExtra(CharacterNeedsExtra.class).getValue("entertainment") >= 0.3;
    }

    @Override
    protected String name() {
        return "base.buff.entertainment";
    }

}
