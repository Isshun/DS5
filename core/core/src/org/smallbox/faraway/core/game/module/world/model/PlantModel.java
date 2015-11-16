package org.smallbox.faraway.core.game.module.world.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.area.model.GardenAreaModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;

import static org.smallbox.faraway.core.data.ItemInfo.ItemInfoPlant.GrowingInfo;

public class PlantModel extends MapObjectModel {
    private GrowingInfo             _growingInfo;
    private double                  _maturity;
    private GardenAreaModel         _garden;
    private boolean                 _hasSeed = true;
    private double                  _nourish;
    private int                     _tile;
    private JobModel                _job;

    public PlantModel(ItemInfo info) {
        super(info);
    }

    public PlantModel(ItemInfo info, int id) {
        super(info, id);
    }

    public void         setGrowingInfo(GrowingInfo growState) { _growingInfo = growState; }
    public void         setMaturity(double maturity) { _maturity = maturity; }
    public void         setGarden(GardenAreaModel garden) { _garden = garden; }
    public void         setSeed(boolean hasSeed) { _hasSeed = hasSeed; }
    public void         setNourish(double nourish) { _nourish = nourish; }
    public void         setTile(int tile) { _tile = tile; }
    public void         setJob(JobModel job) { _job = job; }

    public double       getMaturity() { return _maturity; }
    public double       getNourish() { return _nourish; }
    public GrowingInfo  getGrowingInfo() { return _growingInfo; }
    public int          getTile() { return _tile; }
    public JobModel     getJob() { return _job; }

    public boolean      isMature() { return _maturity >= 1; }
    public boolean      isHarvestable() { return _maturity >= _info.plant.minMaturity; }
    public boolean      inGarden() { return _garden != null; }
    public boolean      hasSeed() { return _hasSeed; }
    public boolean      hasGrowingInfo() { return _growingInfo != null; }

    public void grow() {
        if (_growingInfo != null) {
            _maturity = Math.min(1, _maturity + (_info.plant.growing * _growingInfo.value));
        }
    }
}