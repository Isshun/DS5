package org.smallbox.faraway.core.engine.lua;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterStatsExtra;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaCharacterModel extends ObjectModel {
//    public final int                id;
    public final String             name;
    public final CharacterNeedsExtra needs;
    public final CharacterStatsExtra stats;
    public final CharacterModel     character;
    public final ParcelModel        parcel;
    public final String             type;
    public final String             faction;

    public LuaCharacterModel(CharacterModel character) {
//        this.id = model.getId();
        this.name = character.getPersonals().getName();
        this.faction = "fremen";
        this.needs = character.getNeeds();
        this.stats = character.getStats();
        this.character = character;
        this.parcel = character.getParcel();
        this.type = character.getType().name;
    }

    public boolean isAlive() {
        return this.character.isAlive();
    }
}
