package org.smallbox.faraway.modules.characterBuff.impl;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.characterBuff.BuffFactory;
import org.smallbox.faraway.modules.characterBuff.BuffModel;
import org.smallbox.faraway.modules.characterBuff.BuffType;

@GameObject
public class GoodEnvironmentBuff extends BuffFactory {

    @Inject private GameTime gameTime;

    @Override
    protected BuffModel build() {
        BuffModel buff = new BuffModel();

        buff.setName(name());
        buff.setBuffType(BuffType.BUFF);
        buff.addLevel(1, "Pleasant environment", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("environment") >= 25);
        buff.addLevel(2, "Really pleasant environment", 10, character -> character.getExtra(CharacterNeedsExtra.class).getValue("environment") >= 50);
        buff.addLevel(3, "Extremely pleasant environment", 15, character -> character.getExtra(CharacterNeedsExtra.class).getValue("environment") >= 75);

        return buff;
    }

    @Override
    protected boolean check(CharacterModel character) {
        return character.getExtra(CharacterNeedsExtra.class).getValue("environment") >= 25;
    }

    @Override
    protected String name() {
        return "base.buff.good_environment";
    }

}
