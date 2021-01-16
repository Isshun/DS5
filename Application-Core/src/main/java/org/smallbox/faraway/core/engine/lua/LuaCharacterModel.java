package org.smallbox.faraway.core.engine.lua;

import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

public class LuaCharacterModel extends ObjectModel {
//    public final int                id;
//    public final String             name;
//    public final CharacterStatsExtra stats;
    public final CharacterModel     character;
    public final Parcel parcel;
    public final String             type;
    public final String             faction;

    public LuaCharacterModel(CharacterModel character) {
//        this.id = org.smallbox.faraway.core.module.room.model.getId();
//        this.name = character.getPersonals().getName();
        this.faction = "fremen";
//        this.stats = character.getStats();
        this.character = character;
        this.parcel = character.getParcel();
        this.type = character.getType().name;
    }

    public boolean isAlive() {
        return this.character.isAlive();
    }
}
