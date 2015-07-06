package org.smallbox.faraway.game.model.character;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterStats;

/**
 * Created by Alex on 17/06/2015.
 */
public class DroidModel extends CharacterModel {
    private static final double 	BODY_COLD_ABSORB = 100;
    private static final double 	BODY_COLD_RESIST = 1;

    public static final String[][] EQUIPMENT_VIEW_IDS = new String[][] {
            new String[] {"bt_wheel", "wheel"},
            new String[] {"bt_detector", "detector"},
            new String[] {"bt_tool_1", "tool"},
            new String[] {"bt_tool_2", "tool"},
            new String[] {"bt_tool_3", "tool"}
    };

    public DroidModel(int id, int x, int y, String name, String lastName, double old) {
        super(id, x, y, name, lastName, old, GameData.getData().characters.get("droid"));
        _info.setColor(new Color(0xf1f1f1));
    }

    @Override
    public void addBodyStats(CharacterStats stats) {
        stats.absorb.cold += BODY_COLD_ABSORB;
        stats.resist.cold += BODY_COLD_RESIST;
    }

    @Override
    public String[][] getEquipmentViewIds() {
        return EQUIPMENT_VIEW_IDS;
    }

    @Override
    public String getEquipmentViewPath() {
        return "data/ui/panels/view_equipment_droid.yml";
    }

    @Override
    public String getNeedViewPath() {
        return "data/ui/panels/view_need_droid.yml";
    }

    @Override
    public String getTypeName() {
        return "droid";
    }

    @Override
    public String getName() {
        return "B5";
    }
}
