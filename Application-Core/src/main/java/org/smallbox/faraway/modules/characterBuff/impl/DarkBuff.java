package org.smallbox.faraway.modules.characterBuff.impl;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.characterBuff.BuffFactory;
import org.smallbox.faraway.modules.characterBuff.BuffModel;
import org.smallbox.faraway.modules.characterBuff.BuffType;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@GameObject
public class DarkBuff extends BuffFactory {

    @Inject private GameTime gameTime;

    @Override
    protected BuffModel build() {
        BuffModel buff = new BuffModel();

        LocalDateTime level1Time = gameTime.plus(8, TimeUnit.HOURS);
        LocalDateTime level2Time = gameTime.plus(24, TimeUnit.HOURS);
        LocalDateTime level3Time = gameTime.plus(72, TimeUnit.HOURS);

        buff.setName(name());
        buff.setBuffType(BuffType.DEBUFF);
        buff.addLevel(1, "In the dark for a while", -5, character -> gameTime.getTime().isAfter(level1Time));
        buff.addLevel(2, "In the dark for a long time", -10, character -> gameTime.getTime().isAfter(level2Time));
        buff.addLevel(3, "In the dark for ages", -15, character -> gameTime.getTime().isAfter(level3Time));

        return buff;
    }

    @Override
    protected boolean check(CharacterModel character) {
        return character.getExtra(CharacterNeedsExtra.class).getValue("light") < 1;
    }

    @Override
    protected String name() {
        return "base.buff.dark";
    }

}
