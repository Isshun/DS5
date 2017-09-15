package org.smallbox.faraway.common.lua;

import org.smallbox.faraway.common.ObjectModel;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaCharacterModel extends ObjectModel {
//    public final int                id;
//    public final String             name;
//    public final CharacterStatsExtra stats;
//    public final CharacterModel     character;
//    public final ParcelModel        parcel;
    public final String             faction;

    public LuaCharacterModel() {
//        this.id = org.smallbox.faraway.core.module.room.model.getId();
//        this.name = character.getPersonals().getName();
        this.faction = "fremen";
//        this.stats = character.getStats();
//        this.character = character;
//        this.parcel = character.getParcel();
//        this.type = character.getType().name;
    }

    public boolean isAlive() {
//        return this.character.isAlive();
        return true;
    }
}
