package org.smallbox.faraway.game.character.model;

import org.smallbox.faraway.game.character.CharacterInfo;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.character.model.base.CharacterStatsExtra;
import org.smallbox.faraway.game.world.Parcel;

@CharacterInfoAnnotation("base.character.debug_bot")
public class DebugBotModel extends CharacterModel {
    private static final double     BODY_COLD_ABSORB = 64;
    private static final double     BODY_COLD_RESIST = 0.5;
    private static final String[][] EQUIPMENT_VIEW_IDS = new String[][] {
            new String[] {"bt_top",     "top"},
            new String[] {"bt_head",    "head"},
            new String[] {"bt_hand",    "hand"},
            new String[] {"bt_bottom",  "bottom"},
            new String[] {"bt_feet",    "feet"},
            new String[] {"bt_face",    "face"},
            new String[] {"bt_tool_1",  "tool"},
            new String[] {"bt_tool_2",  "tool"},
            new String[] {"bt_tool_3",  "tool"}
    };

    public DebugBotModel(int id, CharacterInfo characterInfo, Parcel parcel) {
        super(id, characterInfo, parcel);

        _extra.put(CharacterInventoryExtra.class, new CharacterInventoryExtra(this));

//        _personals.setColor(new Color(0xffc57de6));
    }

    @Override
    public void addBodyStats(CharacterStatsExtra stats) {
        stats.debuff.cold += BODY_COLD_ABSORB;
        stats.resist.cold += BODY_COLD_RESIST;
    }

    @Override
    public String        getName() { return "android"; }

}
