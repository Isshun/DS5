package org.smallbox.faraway.core.game.module.job.check.joy;

import org.smallbox.faraway.core.game.module.area.AreaModule;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.MoveJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Log;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyWalk extends CharacterCheck {
    private ParcelModel             _parcel;
    private AreaModel               _area;

    @Override
    public JobModel create(CharacterModel character) {
        if (_parcel == null) {
            Log.error("[CheckEntertainmentWalk] Create job with null parcel");
            return null;
        }

        MoveJob job = MoveJob.create(character, _parcel);
        job.start(character);
        job.setLabel("Move for a walk");
        job.setStrategy(j -> j.getCharacter().getNeeds().joy += 1);
        job.setSpeedModifier(0.5);
        job.setLimit(150);
        job.setEntertainment(true);

        return job;
    }

    @Override
    public boolean check(CharacterModel character) {
        // LF model
        _area = null;
        int bestDistance = Integer.MAX_VALUE;
        for (AreaModel area: ((AreaModule) ModuleManager.getInstance().getModule(AreaModule.class)).getAreas()) {
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
