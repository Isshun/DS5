package org.smallbox.faraway.game.model.character.base;

import org.smallbox.faraway.game.model.item.ItemInfo;

import java.util.List;

/**
 * Created by Alex on 16/06/2015.
 */
public class CharacterStats {
    public String deathMessage;
    public double speed;
    public boolean isAlive = true;

    public static class CharacterStatsValues {
        public double   cold;
        public int      coldScore;
        public double   heat;
        public int      heatScore;
        public double   oxygen;
        public int      oxygenScore;
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

        this.resist.cold = Math.min(100, Math.max(0, Math.log(this.resist.coldScore * 10))) / 10;
        this.resist.heat = Math.min(100, Math.max(0, Math.log(this.resist.heatScore * 10))) / 10;
        this.resist.oxygen = Math.min(100, Math.max(0, Math.log(this.resist.oxygenScore * 10))) / 10;

        this.buff.cold = this.buff.coldScore;
        this.buff.heat = this.buff.heatScore;
        this.buff.oxygen = this.buff.oxygenScore;

        this.debuff.cold = this.debuff.coldScore;
        this.debuff.heat = this.debuff.heatScore;
        this.debuff.oxygen = this.debuff.oxygenScore;
    }

    private void addValues(CharacterStatsValues values, ItemInfo.EquipmentEffectValues effect) {
        values.coldScore += effect.cold;
        values.heatScore += effect.heat;
        values.oxygenScore += effect.oxygen;
    }

    private void reset(CharacterStatsValues values) {
        values.cold = 0;
        values.coldScore = 0;
        values.heat = 0;
        values.heatScore = 0;
        values.oxygen = 0;
        values.oxygenScore = 0;
    }


}
