package org.smallbox.faraway.game.model.check.joy;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobMove;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyWalk extends CharacterCheck {
    private GameConfig.EffectValues _effects;
    private ParcelModel             _parcel;

    public CheckJoyWalk() {
        _effects = new GameConfig.EffectValues();
        _effects.joy = 0.25;
    }

    @Override
    public BaseJobModel create(CharacterModel character) {
        if (_parcel == null) {
            Log.error("[CheckJoyWalk] Create job with null parcel");
            return null;
        }

        JobMove job = JobMove.create(character, _parcel.getX(), _parcel.getY());
        job.start(character);
        job.setLabel("Move for a walk");
        job.setEffects(_effects);
        job.setSpeedModifier(0.5);
        job.setLimit(50);
        return job;
    }

    @Override
    public boolean check(CharacterModel character) {
        _parcel = Game.getWorldManager().getNearestFreeSpace((int) (Math.random() * 32), (int) (Math.random() * 30), false);
        return _parcel != null;
    }
}
