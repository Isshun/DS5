package org.smallbox.faraway.game.model.check.joy;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.world.AreaModule;
import org.smallbox.faraway.game.model.area.AreaModel;
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
    private ParcelModel             _parcel;
    private AreaModel               _area;

    @Override
    public BaseJobModel create(CharacterModel character) {
        if (_parcel == null) {
            Log.error("[CheckJoyWalk] Create job with null parcel");
            return null;
        }

        JobMove job = JobMove.create(character, _parcel.x, _parcel.y);
        job.start(character);
        job.setLabel("Move for a walk");
        job.setStrategy(j -> j.getCharacter().getNeeds().joy += 1);
        job.setSpeedModifier(0.5);
        job.setLimit(150);
        job.setJoy(true);

        return job;
    }

    @Override
    public boolean check(CharacterModel character) {
        // LF area
        _area = null;
        int bestDistance = Integer.MAX_VALUE;
        for (AreaModel area: ((AreaModule)Game.getInstance().getModule(AreaModule.class)).getAreas()) {
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
                if (parcel.isWalkable() && parcel.getItem() == null && (i++ < r || _parcel == null)) {
                    _parcel = parcel;
                }
            }
            if (_parcel != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getNeeds().joy < character.getType().needs.joy.warning;
    }
}
