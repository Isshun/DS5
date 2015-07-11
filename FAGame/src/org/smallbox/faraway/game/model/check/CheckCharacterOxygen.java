package org.smallbox.faraway.game.model.check;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobMove;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.util.Log;

import java.util.Optional;

/**
 * Created by Alex on 28/06/2015.
 */
public class CheckCharacterOxygen extends CharacterCheck {
    private ParcelModel _parcel;

    @Override
    public BaseJobModel create(CharacterModel character) {
        if (_parcel == null) {
            Log.error("[CheckJoyWalk] Create job with null parcel");
            return null;
        }

        JobMove job = JobMove.create(character, _parcel.x, _parcel.y);
        job.start(character);
        job.setLabel("Looking for oxygen");
        job.setLimit(50);

        return job;
    }

    @Override
    public boolean check(CharacterModel character) {
        if (character.getNeeds().oxygen < 20) {
            Optional<RoomModel> roomOpt = ((RoomManager)Game.getInstance().getManager(RoomManager.class)).getRoomList().stream().filter(room -> room.getOxygen() >= 0.75).findAny();
            if (roomOpt.isPresent()) {
                Optional<ParcelModel> parcelOpt = roomOpt.get().getParcels().stream().filter(ParcelModel::isEmpty).findAny();
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
