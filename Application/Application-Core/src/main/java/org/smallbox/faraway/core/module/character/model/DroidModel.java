package org.smallbox.faraway.core.module.character.model;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.character.model.base.CharacterStatsExtra;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

/**
 * Created by Alex on 17/06/2015.
 */
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

    public DroidModel(int id, ParcelModel parcel, String name, String lastName, double old) {
        super(id, parcel, name, lastName, old, Application.data.characters.get("droid"));
        _personals.setColor(new Color(0xf1f1f1));
    }

    @Override
    public void addBodyStats(CharacterStatsExtra stats) {
        stats.debuff.cold += BODY_COLD_ABSORB;
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
