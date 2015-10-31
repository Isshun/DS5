package org.smallbox.faraway.core.game.module.world.model.resource;

import org.smallbox.faraway.core.data.ItemInfo.ItemInfoPlant.GrowingInfo;
import org.smallbox.faraway.core.game.module.area.model.GardenAreaModel;

import static org.smallbox.faraway.core.data.ItemInfo.ItemInfoPlant;

/**
 * Created by Alex on 31/10/2015.
 */
public class PlantExtra {
    private final ItemInfoPlant     _info;
    private double                  _growRate;
    private GrowingInfo             _growState;
    private double                  _maturity;
    private GardenAreaModel         _garden;
    private boolean                 _hasSeed = true;
    private double                  _nourish;

    public PlantExtra(ItemInfoPlant info) {
        _info = info;
    }

    public void         setGrowRate(double growRate) { _growRate = growRate; }
    public void         setGrowState(GrowingInfo growState) { _growState = growState; }
    public void         setMaturity(double maturity) { _maturity = maturity; }
    public void         setGarden(GardenAreaModel garden) { _garden = garden; }
    public void         setSeed(boolean hasSeed) { _hasSeed = hasSeed; }
    public void         setNourish(double nourish) { _nourish = nourish; }

    public double       getMaturity() { return _maturity; }
    public double       getNourish() { return _nourish; }
    public double       getGrowRate() { return _growRate; }
    public GrowingInfo  getGrowState() { return _growState; }

    public boolean      isMature() { return _maturity >= 1; }
    public boolean      isHarvestable() { return _maturity >= _info.minMaturity; }
    public boolean      inGarden() { return _garden != null; }
    public boolean      hasSeed() { return _hasSeed; }

    public void grow(GrowingInfo growState) {
        _growState = growState;
        _growRate = growState.value;
        _maturity = Math.min(1, _maturity + (_info.growing * growState.value));
    }
}
