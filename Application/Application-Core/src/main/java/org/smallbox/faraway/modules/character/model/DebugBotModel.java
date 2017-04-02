package org.smallbox.faraway.modules.character.model;

import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterStatsExtra;

/**
 * Created by Alex on 17/06/2015.
 */
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

    public DebugBotModel(int id, CharacterInfo characterInfo, ParcelModel parcel, String name, String lastName, double old) {
        super(id, characterInfo, parcel, name, lastName, old);
    }

    @Override
    public void addBodyStats(CharacterStatsExtra stats) {
        stats.debuff.cold += BODY_COLD_ABSORB;
        stats.resist.cold += BODY_COLD_RESIST;
    }

    @Override
    public String        getName() { return "android"; }

}
