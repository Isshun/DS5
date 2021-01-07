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
public class PainBuff extends BuffFactory {

    @Inject private GameTime gameTime;

    @Override
    protected BuffModel build() {
        BuffModel buff = new BuffModel();

        buff.setName(name());
        buff.setBuffType(BuffType.DEBUFF);
        buff.addLevel(1, "Suffers from woundings", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("pain") > 0.2);
        buff.addLevel(2, "Suffers enormously from woundings", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("pain") > 0.5);
        buff.addLevel(3, "Is in agony", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("pain") > 0.8);
        buff.addLevel(4, "Fainted because of the pain", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("pain") > 0.95);

        return buff;
    }

    @Override
    protected boolean check(CharacterModel character) {
        return character.getExtra(CharacterNeedsExtra.class).getValue("pain") > 0.2;
    }

    @Override
    protected String name() {
        return "base.buff.pain";
    }

}
