package org.smallbox.faraway.game.model.character.base;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

import java.util.List;

/**
 * Created by Alex on 16/06/2015.
 */
public class CharacterStats {
    public String deathMessage;
    public double speed;
    public boolean isAlive = true;

    public static class CharacterStatsValues {
        public double cold;
        public double heat;
        public double oxygen;
    }

    public CharacterStatsValues debuff = new CharacterStatsValues();
    public CharacterStatsValues resist = new CharacterStatsValues();
    public CharacterStatsValues buff = new CharacterStatsValues();

    public void reset(CharacterModel character, List<ItemInfo> equipmentsInfo) {
        reset(this.debuff);
        reset(this.resist);
        reset(this.buff);

        character.addBodyStats(this);

        for (ItemInfo itemInfo: equipmentsInfo) {
            if (itemInfo.equipment.effects != null) {
                for (ItemInfo.EquipmentEffect effect: itemInfo.equipment.effects) {
                    // Check debuff
                    if (effect.debuff != null) {
                        addValues(this.debuff, effect.debuff);
                    }

                    // Check resist
                    if (effect.resist != null) {
                        addValues(this.resist, effect.resist);
                    }

                    // Check buff
                    if (effect.buff != null) {
                        addValues(this.buff, effect.buff);
                    }
                }
            }
        }

        this.resist.cold = Math.min(100, Math.max(0, this.resist.cold));
        this.resist.heat = Math.min(100, Math.max(0, this.resist.heat));
        this.resist.oxygen = Math.min(100, Math.max(0, this.resist.oxygen));
    }

    private void addValues(CharacterStatsValues values, ItemInfo.EquipmentEffectValues effect) {
        values.cold += effect.cold;
        values.heat += effect.heat;
        values.oxygen += effect.oxygen;
    }

    private void reset(CharacterStatsValues values) {
        values.cold = 0;
        values.heat = 0;
        values.oxygen = 0;
    }


}
