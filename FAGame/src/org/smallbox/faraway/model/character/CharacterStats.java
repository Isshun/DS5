package org.smallbox.faraway.model.character;

import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.model.EquipmentModel;

import java.util.List;

/**
 * Created by Alex on 16/06/2015.
 */
public class CharacterStats {
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

    public void update(List<EquipmentModel> equipments) {
        this.absorb.cold = 0;
        this.resist.cold = 0;
        this.resist.oxygen = 0;
        this.buff.oxygen = 0;


        // Body absorbs / resists
        this.absorb.cold += Constant.BODY_COLD_ABSORB;
        this.resist.cold += Constant.BODY_COLD_RESIST;

        for (EquipmentModel equipment: equipments) {
            if (equipment.effects != null) {
                for (EquipmentModel.EquipmentEffect effect: equipment.effects) {
                    // Check absorb
                    if (effect.absorb != null) {
                        this.absorb.cold += effect.absorb.cold;
                    }

                    // Check resist
                    if (effect.resist != null) {
                        this.resist.cold += effect.resist.cold;
                    }

                    // Check resist
                    if (effect.resist != null) {
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
