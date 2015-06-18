package org.smallbox.faraway.game.model.character;

import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterStats;

/**
 * Created by Alex on 17/06/2015.
 */
public class HumanModel extends CharacterModel {
    public static final double 	BODY_COLD_ABSORB = 32;
    public static final double 	BODY_COLD_RESIST = 0.25;

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

    public HumanModel(int id, int x, int y, String name, String lastName, double old) {
        super(id, x, y, name, lastName, old);
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
        return "data/ui/panels/view_equipment_human.yml";
    }

    @Override
    public String getNeedViewPath() {
        return "data/ui/panels/view_need_human.yml";
    }

    @Override
    public String getTypeName() {
        return "human";
    }

    @Override
    public GameConfig.EffectValues getNeedEffects() {
        return GameData.config.character.effects.human;
    }
}
