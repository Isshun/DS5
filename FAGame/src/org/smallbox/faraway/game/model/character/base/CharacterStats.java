package org.smallbox.faraway.game.model.character.base;

import org.smallbox.faraway.game.model.item.ItemInfo;

import java.util.List;

/**
 * Created by Alex on 16/06/2015.
 */
public class CharacterStats {
    public double speed;

    public static class CharacterStatsAbsorb {
        public double cold;
    }

    public static class CharacterStatsResist {
        public double cold;
        public double oxygen;
    }

    public static class CharacterStatsBuff {
        public double oxygen;
    }

    public CharacterStatsAbsorb absorb = new CharacterStatsAbsorb();
    public CharacterStatsResist resist = new CharacterStatsResist();
    public CharacterStatsBuff buff = new CharacterStatsBuff();

    public void reset(CharacterModel character, List<ItemInfo> equipmentInfos) {
        this.absorb.cold = 0;
        this.resist.cold = 0;
        this.resist.oxygen = 0;
        this.buff.oxygen = 0;

        character.addBodyStats(this);

        for (ItemInfo itemInfo: equipmentInfos) {
            if (itemInfo.equipment.effects != null) {
                for (ItemInfo.EquipmentEffect effect: itemInfo.equipment.effects) {
                    // Check absorb
                    if (effect.absorb != null) {
                        this.absorb.cold += effect.absorb.cold;
                    }

                    // Check resist
                    if (effect.resist != null) {
                        this.resist.cold += effect.resist.cold / 4;
                        this.resist.oxygen += effect.resist.oxygen;
                    }

                    // Check buff
                    if (effect.buff != null) {
                        this.buff.oxygen += effect.buff.oxygen;
                    }
                }
            }
        }
    }


}
