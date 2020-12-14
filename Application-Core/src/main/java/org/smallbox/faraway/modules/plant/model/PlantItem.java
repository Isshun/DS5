package org.smallbox.faraway.modules.plant.model;

import org.smallbox.faraway.GameSerializer;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.plant.GardenArea;
import org.smallbox.faraway.modules.plant.PlantGrowTask;
import org.smallbox.faraway.util.Utils;

import static org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoPlant.GrowingInfo;

@GameSerializer(PlantSerializer.class)
public class PlantItem extends MapObjectModel {
    private GrowingInfo         _growingInfo;
    private double              _maturity;
    private GardenArea          _garden;
    private boolean             _hasSeed = true;
    private double              _nourish;
    private int                 _tile;
    private JobModel            _job;
    public PlantGrowTask        task;

    public PlantItem(ItemInfo info) {
        super(info);
    }

    public PlantItem(ItemInfo info, int id) {
        super(info, id);
    }

    public void                 setGrowingInfo(GrowingInfo growState) { _growingInfo = growState; }
    public void                 setMaturity(double maturity) { _maturity = maturity; }
    public void                 setGarden(GardenArea garden) { _garden = garden; }
    public void                 setSeed(boolean hasSeed) { _hasSeed = hasSeed; }
    public void                 setNourish(double nourish) { _nourish = nourish; }
    public void                 setTile(int tile) { _tile = tile; }
    public void                 setJob(JobModel job) { _job = job; }

    public double               getMaturity() { return _maturity; }
    public double               getNourish() { return _nourish; }
    public GrowingInfo          getGrowingInfo() { return _growingInfo; }
    public GardenArea           getGarden() { return _garden; }
    public int                  getTile() { return _tile; }
    public JobModel             getJob() { return _job; }

    public boolean              isMature() { return _maturity >= 1; }
    public boolean              inGarden() { return _garden != null; }
    public boolean              hasSeed() { return _hasSeed; }
    public boolean              hasGrowingInfo() { return _growingInfo != null; }

    public void grow(double hourInterval) {
        if (_growingInfo != null) {
            double growingAddValue = hourInterval / _info.plant.growing;
            _maturity = Utils.bound(0, 1, _maturity + (growingAddValue * _growingInfo.value));
        }
    }
}