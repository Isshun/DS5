package org.smallbox.faraway.modules.room.model;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 08/03/2017.
 */
@RoomTypeInfo(label = "Quarter")
public class QuarterRoom extends RoomModel {

    private CharacterModel _owner;

    public QuarterRoom() {
        super(null, 0, null);
    }

    public CharacterModel getOwner() {
        return _owner;
    }

    public void setOwner(CharacterModel owner) {
        _owner = owner;
    }

}
