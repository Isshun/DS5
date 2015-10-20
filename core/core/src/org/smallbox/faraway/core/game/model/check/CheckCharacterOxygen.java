package org.smallbox.faraway.core.game.model.check;

import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.model.item.ParcelModel;
import org.smallbox.faraway.core.game.model.job.MoveJob;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;
import org.smallbox.faraway.core.game.model.room.RoomModel;
import org.smallbox.faraway.core.game.module.ModuleManager;
import org.smallbox.faraway.core.game.module.base.RoomModule;
import org.smallbox.faraway.core.util.Log;

import java.util.Optional;

/**
 * Created by Alex on 28/06/2015.
 */
public class CheckCharacterOxygen extends CharacterCheck {
    private ParcelModel _parcel;

    @Override
    public JobModel create(CharacterModel character) {
        if (_parcel == null) {
            Log.error("[CheckEntertainmentWalk] Create job with null parcel");
            return null;
        }

        MoveJob job = MoveJob.create(character, _parcel);
        job.start(character);
        job.setLabel("Looking for oxygen");
        job.setLimit(50);

        return job;
    }

    @Override
    public boolean check(CharacterModel character) {
        if (character.getNeeds().oxygen < 20) {
            Optional<RoomModel> roomOpt = ((RoomModule) ModuleManager.getInstance().getModule(RoomModule.class)).getRoomList().stream().filter(room -> room.getOxygen() >= 0.75).findAny();
            if (roomOpt.isPresent()) {
                Optional<ParcelModel> parcelOpt = roomOpt.get().getParcels().stream().filter(ParcelModel::isWalkable).findAny();
                if (parcelOpt.isPresent()) {
                    _parcel = parcelOpt.get();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getType().needs.oxygen != null && character.getNeeds().oxygen < character.getType().needs.oxygen.critical;
    }
}
