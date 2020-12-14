package org.smallbox.faraway.modules.character.model;

import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterStatsExtra;

/**
 * Created by Alex on 17/06/2015.
 */
@CharacterInfoAnnotation("base.character.droid")
public class DroidModel extends CharacterModel {
    private static final double     BODY_COLD_ABSORB = 100;
    private static final double     BODY_COLD_RESIST = 1;

    public static final String[][] EQUIPMENT_VIEW_IDS = new String[][] {
            new String[] {"bt_wheel", "wheel"},
            new String[] {"bt_detector", "detector"},
            new String[] {"bt_tool_1", "tool"},
            new String[] {"bt_tool_2", "tool"},
            new String[] {"bt_tool_3", "tool"}
    };

    public DroidModel(int id, CharacterInfo characterInfo, ParcelModel parcel) {
        super(id, characterInfo, parcel);
//        _personals.setColor(new Color(0xf1f1f1ff));
    }

    @Override
    public void addBodyStats(CharacterStatsExtra stats) {
        stats.debuff.cold += BODY_COLD_ABSORB;
        stats.resist.cold += BODY_COLD_RESIST;
    }

    @Override
    public String getName() {
        return "B5";
    }
}
