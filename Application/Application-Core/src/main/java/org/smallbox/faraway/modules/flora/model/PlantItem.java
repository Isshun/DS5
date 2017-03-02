package org.smallbox.faraway.modules.flora.model;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.area.model.GardenAreaModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;

import static org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoPlant.GrowingInfo;

public class PlantItem extends MapObjectModel {
    private GrowingInfo             _growingInfo;
    private double                  _maturity;
    private GardenAreaModel         _garden;
    private boolean                 _hasSeed = true;
    private double                  _nourish;
    private int                     _tile;
    private JobModel                _job;

    public PlantItem(ItemInfo info) {
        super(info);
    }

    public PlantItem(ItemInfo info, int id) {
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
    public GardenAreaModel getGarden() { return _garden; }
    public int          getTile() { return _tile; }
    public JobModel     getJob() { return _job; }

    public boolean      isMature() { return _maturity >= 1; }
    public boolean      inGarden() { return _garden != null; }
    public boolean      hasSeed() { return _hasSeed; }
    public boolean      hasGrowingInfo() { return _growingInfo != null; }

    public void grow() {
        if (_growingInfo != null) {
            _maturity = Math.min(1, _maturity + (_info.plant.growing * _growingInfo.value));
        }
    }
}