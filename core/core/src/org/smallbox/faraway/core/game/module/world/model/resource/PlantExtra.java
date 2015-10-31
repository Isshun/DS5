package org.smallbox.faraway.core.game.module.world.model.resource;

import org.smallbox.faraway.core.data.ItemInfo.ItemInfoPlant.GrowingInfo;

import static org.smallbox.faraway.core.data.ItemInfo.ItemInfoPlant;

/**
 * Created by Alex on 31/10/2015.
 */
public class PlantExtra {
    private final ItemInfoPlant     _info;
    private double                  _growRate;
    private GrowingInfo             _growState;
    private double                  _maturity;

    public PlantExtra(ItemInfoPlant info) {
        _info = info;
    }

    public void         setGrowRate(double growRate) { _growRate = growRate; }
    public void         setGrowState(GrowingInfo growState) { _growState = growState; }
    public void         setMaturity(double maturity) { _maturity = maturity; }

    public double       getMaturity() { return _maturity; }
    public double       getGrowRate() { return _growRate; }
    public GrowingInfo  getGrowState() { return _growState; }

    public boolean      isMature() { return _maturity >= 1; }
    public boolean      isHarvestable() { return _maturity >= _info.minMaturity; }

    public void grow(GrowingInfo growState) {
        _growState = growState;
        _growRate = growState.value;
        _maturity = Math.min(1, _maturity + (_info.growing * growState.value));
    }
}
