package org.smallbox.faraway.game.characterBuff.impl;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.characterBuff.BuffFactory;
import org.smallbox.faraway.game.characterBuff.BuffModel;
import org.smallbox.faraway.game.characterBuff.BuffType;

import java.util.concurrent.TimeUnit;

@GameObject
public class SleepInBedBuff extends BuffFactory {

    @Inject private GameTime gameTime;

    @Override
    protected BuffModel build() {
        BuffModel buff = new BuffModel();

        buff.setName(name());
        buff.setBuffType(BuffType.BUFF);
        buff.addLevel(1, "Has slept in a great bed", 5, character -> true);
        buff.setEndTime(gameTime.plus(8, TimeUnit.HOURS));

        return buff;
    }

    @Override
    protected boolean check(CharacterModel character) {
        return character.isSleeping() && character.getParcel().getItems().stream().anyMatch(item -> item.getName().contains("bed"));
    }

    @Override
    protected String name() {
        return "base.buff.sleep_in_bed";
    }

}
