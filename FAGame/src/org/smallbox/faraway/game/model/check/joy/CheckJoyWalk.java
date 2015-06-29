package org.smallbox.faraway.game.model.check.joy;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobMove;
import org.smallbox.faraway.ui.AreaManager;
import org.smallbox.faraway.ui.AreaModel;
import org.smallbox.faraway.util.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyWalk extends CharacterCheck {
    private GameConfig.EffectValues _effects;
    private ParcelModel             _parcel;
    private AreaModel               _area;

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
        // LF area
        _area = null;
        int bestDistance = Integer.MAX_VALUE;
        for (AreaModel area: ((AreaManager)Game.getInstance().getManager(AreaManager.class)).getAreas()) {
            if (area.isHome()) {
                int distance = Math.abs(character.getX() - area.getX()) + Math.abs(character.getY() - area.getY());
                if (bestDistance > distance) {
                    bestDistance = distance;
                    _area = area;
                }
            }
        }

        // LF parcel
        if (_area != null) {
            _parcel = null;
            int size = _area.getParcels().size();
            int r = (int)(Math.random() * size);
            int i = 0;
            for (ParcelModel parcel: _area.getParcels()) {
                if (parcel.isWalkable() && !parcel.hasItem() && (i++ < r || _parcel == null)) {
                    _parcel = parcel;
                }
            }
            if (_parcel != null) {
                return true;
            }
        }
        return false;
    }
}
