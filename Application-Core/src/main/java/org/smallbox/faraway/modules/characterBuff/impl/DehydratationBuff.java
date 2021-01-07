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
public class DehydratationBuff extends BuffFactory {

    @Inject private GameTime gameTime;

    @Override
    protected BuffModel build() {
        BuffModel buff = new BuffModel();

        buff.setName(name());
        buff.setBuffType(BuffType.DEBUFF);
        buff.addLevel(1, "Is a little thirsty", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("drink") < 0.75);
        buff.addLevel(2, "Is very thirsty", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("drink") < 0.5);
        buff.addLevel(3, "Dying of thirst", 5, character -> character.getExtra(CharacterNeedsExtra.class).getValue("drink") < 0.25);

        return buff;
    }

    @Override
    protected boolean check(CharacterModel character) {
        return character.getExtra(CharacterNeedsExtra.class).getValue("drink") < 0.75;
    }

    @Override
    protected String name() {
        return "base.buff.dehydration";
    }

}
