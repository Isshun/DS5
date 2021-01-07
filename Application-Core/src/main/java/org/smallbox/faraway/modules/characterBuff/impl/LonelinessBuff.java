package org.smallbox.faraway.modules.characterBuff.impl;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.characterBuff.BuffFactory;
import org.smallbox.faraway.modules.characterBuff.BuffModel;
import org.smallbox.faraway.modules.characterBuff.BuffType;

@GameObject
public class LonelinessBuff extends BuffFactory {

    @Override
    protected BuffModel build() {
        BuffModel buff = new BuffModel();

        buff.setName(name());
        buff.setBuffType(BuffType.DEBUFF);
        buff.addLevel(1, "Feeling lonely", 15, character -> character.getExtra(CharacterNeedsExtra.class).get("relation").value() < 50);
        buff.addLevel(2, "Begins to talk to himself", 15, character -> character.getExtra(CharacterNeedsExtra.class).get("relation").value() < 30);
        buff.addLevel(3, "About to going crazy", 5, character -> character.getExtra(CharacterNeedsExtra.class).get("relation").value() < 10);

        return buff;
    }

    @Override
    protected boolean check(CharacterModel character) {
        return character.getExtra(CharacterNeedsExtra.class).getValue("relation") < 50;
    }

    @Override
    protected String name() {
        return "base.buff.loneliness";
    }

}
