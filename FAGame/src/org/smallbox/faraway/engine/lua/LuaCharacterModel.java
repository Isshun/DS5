package org.smallbox.faraway.engine.lua;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterNeeds;
import org.smallbox.faraway.game.model.character.base.CharacterStats;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaCharacterModel {
    public final int                id;
    public final String             name;
    public final CharacterNeeds     needs;
    public final CharacterStats     stats;
    public final CharacterModel     character;
    public final ItemInfo           item;
    public final ParcelModel        parcel;
    public final String             type;
    public final String             faction;

    public LuaCharacterModel(CharacterModel character) {
        this.id = character.getId();
        this.name = character.getInfo().getName();
        this.faction = "fremen";
        this.needs = character.getNeeds();
        this.stats = character.getStats();
        this.character = character;
        this.parcel = character.getParcel();
        this.type = character.getType().name;
        this.item = character.getJob() != null && character.getJob().getItem() != null ? character.getJob().getItem().getInfo() : null;
    }

    public boolean isAlive() {
        return this.character.isAlive();
    }
}
