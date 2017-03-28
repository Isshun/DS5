package org.smallbox.faraway.modules.character.model;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterTimetableExtra;
import org.smallbox.faraway.modules.character.model.base.*;

/**
 * Created by Alex on 17/06/2015.
 */
public class HumanModel extends CharacterModel {
    public static final double     BODY_COLD_ABSORB = 32;
    public static final double     BODY_COLD_RESIST = 0.25;

    public static final String[][] EQUIPMENT_VIEW_IDS = new String[][] {
            new String[] {"bt_top", "top"},
            new String[] {"bt_head", "head"},
            new String[] {"bt_hand", "hand"},
            new String[] {"bt_bottom", "bottom"},
            new String[] {"bt_feet", "feet"},
            new String[] {"bt_face", "face"},
            new String[] {"bt_tool_1", "tool"},
            new String[] {"bt_tool_2", "tool"},
            new String[] {"bt_tool_3", "tool"}
    };

    public HumanModel(int id, ParcelModel parcel, String name, String lastName, double old) {
        super(id, parcel, name, lastName, old, Application.data.characters.get("base.character.human"));

        _extra.put(CharacterNeedsExtra.class, new CharacterNeedsExtra(_type.needs));
        _extra.put(CharacterTimetableExtra.class, new CharacterTimetableExtra());
        _extra.put(CharacterSkillExtra.class, new CharacterSkillExtra());
        _extra.put(CharacterStatsExtra.class, new CharacterStatsExtra());
        _extra.put(CharacterPersonalsExtra.class, new CharacterPersonalsExtra(name, lastName, old));
        _extra.put(CharacterDiseasesExtra.class, new CharacterDiseasesExtra());

//        _personals.setGender((int) (Math.random() * 1000) % 2 == 0 ? CharacterPersonalsExtra.Gender.MALE : CharacterPersonalsExtra.Gender.FEMALE);
    }

    @Override
    public void addBodyStats(CharacterStatsExtra stats) {
        stats.debuff.cold += BODY_COLD_ABSORB;
        stats.resist.cold += BODY_COLD_RESIST;
    }

    @Override
    public String getName() {
        return getExtra(CharacterPersonalsExtra.class).getFirstName() + " " + getExtra(CharacterPersonalsExtra.class).getLastName();
    }
}
