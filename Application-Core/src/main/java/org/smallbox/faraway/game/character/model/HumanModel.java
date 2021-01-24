package org.smallbox.faraway.game.character.model;

import org.smallbox.faraway.GameSerializer;
import org.smallbox.faraway.game.character.CharacterSerializer;
import org.smallbox.faraway.game.character.CharacterInfo;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.character.CharacterTimetableExtra;
import org.smallbox.faraway.game.character.model.base.CharacterName;
import org.smallbox.faraway.game.character.model.base.*;

@CharacterInfoAnnotation("base.character.human")
@GameSerializer(CharacterSerializer.class)
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

    public HumanModel(int id, CharacterInfo characterInfo, Parcel parcel) {
        super(id, characterInfo, parcel);

        _extra.put(CharacterNeedsExtra.class, new CharacterNeedsExtra(this, _type.needs));
        _extra.put(CharacterTimetableExtra.class, new CharacterTimetableExtra(this));
        _extra.put(CharacterSkillExtra.class, new CharacterSkillExtra(this));
        _extra.put(CharacterStatsExtra.class, new CharacterStatsExtra(this));

        _extra.put(CharacterDiseasesExtra.class, new CharacterDiseasesExtra(this));
        _extra.put(CharacterInventoryExtra.class, new CharacterInventoryExtra(this));

        CharacterPersonalsExtra.Gender gender = (int) (Math.random() * 1000) % 2 == 0 ? CharacterPersonalsExtra.Gender.MALE : CharacterPersonalsExtra.Gender.FEMALE;
        _extra.put(CharacterPersonalsExtra.class, new CharacterPersonalsExtra(this, CharacterName.getFirstname(gender), CharacterName.getLastName(), 16, gender));

        CharacterFreeTimeExtra characterFreeTimeExtra = new CharacterFreeTimeExtra(this);
//        characterFreeTimeExtra.addType(BasicWalkJob.class);
        _extra.put(CharacterFreeTimeExtra.class, characterFreeTimeExtra);
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
