package org.smallbox.faraway.modules.character.model;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterStatsExtra;

/**
 * Created by Alex on 17/06/2015.
 */
public class AndroidModel extends CharacterModel {
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

    public AndroidModel(int id, ParcelModel parcel, String name, String lastName, double old) {
        super(id, parcel, name, lastName, old, Application.data.characters.get("android"));
        _personals.setColor(new Color(0xc57de6));
    }

    @Override
    public void addBodyStats(CharacterStatsExtra stats) {
        stats.debuff.cold += BODY_COLD_ABSORB;
        stats.resist.cold += BODY_COLD_RESIST;
    }

    @Override
    public String        getName() { return _personals.getFirstName(); }

    @Override
    public String[][]   getEquipmentViewIds() {
        return EQUIPMENT_VIEW_IDS;
    }

    @Override
    public String       getEquipmentViewPath() {
        return "data/ui/panels/view_equipment_android.yml";
    }

    @Override
    public String       getNeedViewPath() {
        return "data/ui/panels/view_need_android.yml";
    }
}
