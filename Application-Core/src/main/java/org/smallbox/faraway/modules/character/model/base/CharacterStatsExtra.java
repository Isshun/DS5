package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

import java.util.List;

public class CharacterStatsExtra extends CharacterExtra {
    public double       speed = 1;
    public boolean      isFaint = false;

    public CharacterStatsExtra(CharacterModel character) {
        super(character);
    }

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

        this.resist.cold = Math.min(100, Math.max(0, Math.log(this.resist.coldScore * 10.0))) / 10;
        this.resist.heat = Math.min(100, Math.max(0, Math.log(this.resist.heatScore * 10.0))) / 10;

        this.resist.oxygen = Math.min(100, Math.max(0, Math.log(this.resist.oxygenScore * 10.0))) / 10;

        this.buff.cold = this.buff.coldScore;
        this.buff.heat = this.buff.heatScore;
        this.buff.oxygen = this.buff.oxygenScore;

        this.debuff.cold = this.debuff.coldScore;
        this.debuff.heat = this.debuff.heatScore;
        this.debuff.oxygen = this.debuff.oxygenScore;
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
