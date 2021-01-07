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
public class HungryBuff extends BuffFactory {

    @Inject private GameTime gameTime;

    @Override
    protected BuffModel build() {
        BuffModel buff = new BuffModel();

        buff.setName(name());
        buff.setBuffType(BuffType.DEBUFF);
        buff.addLevel(1, "Feeling a little peckish", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("food") < 1500);
        buff.addLevel(2, "Feeling hungry", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("food") < 1000);
        buff.addLevel(3, "Suffering from hunger", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("food") < 500);
        buff.addLevel(4, "Is starving to death", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("food") < 100);

        return buff;
    }

    @Override
    protected boolean check(CharacterModel character) {
        return character.getExtra(CharacterNeedsExtra.class).getValue("food") < 0;
    }

    @Override
    protected String name() {
        return "base.buff.hungry";
    }

}
