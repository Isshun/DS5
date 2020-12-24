package org.smallbox.faraway.module.oxygen;

import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.module.job.model.MoveJob;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.room.model.RoomModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.module.room.RoomModule;

import java.util.Optional;

public class CheckCharacterOxygen extends CharacterCheck {
    private final RoomModule _roomModule;
    private ParcelModel _parcel;

    public CheckCharacterOxygen(OxygenModule oxygenModule, RoomModule roomModule) {
        _roomModule = roomModule;
    }

    @Override
    public JobModel onCreateJob(CharacterModel character) {
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
    public boolean isJobLaunchable(CharacterModel character) {
        if (character.getNeeds().get("oxygen") < 20) {
            Optional<RoomModel> roomOpt = _roomModule.getRooms().stream().filter(room -> room.getOxygen() >= 0.75).findAny();
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
    public boolean isJobNeeded(CharacterModel character) {
        return character.getType().needs.oxygen != null && character.getNeeds().get("oxygen") < character.getType().needs.oxygen.critical;
    }
}
