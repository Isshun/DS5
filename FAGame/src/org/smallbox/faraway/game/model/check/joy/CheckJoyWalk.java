package org.smallbox.faraway.game.model.check.joy;

import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.job.JobModel;
import org.smallbox.faraway.game.model.job.JobMove;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyWalk extends CharacterCheck {
    private GameConfig.EffectValues _effects;

    public CheckJoyWalk() {
        _effects = new GameConfig.EffectValues();
        _effects.joy = 0.25;
    }

    @Override
    public JobModel create(CharacterModel character) {
        JobMove job = JobMove.create(character, (int)(Math.random() * 42), (int)(Math.random() * 42));
        job.setCharacter(character);
        job.setLabel("Move for a walk");
        job.setEffects(_effects);
        job.setSpeedModifier(0.15);
        return job;
    }

    @Override
    public boolean check(CharacterModel character) {
        return true;
    }
}
