package org.smallbox.faraway.core.game.module.world.model.resource;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;

public class ResourceModel extends MapObjectModel {
    private PlantExtra          _plant;
    private RockExtra           _rock;
    private int                 _tile;
    private JobModel            _job;

    public ResourceModel(ItemInfo info, int id) {
        super(info, id);

        if (_info.isPlant) {
            _plant = new PlantExtra(_info.plant);
        }

        if (_info.isRock) {
            _rock = new RockExtra();
        }
    }

    public ResourceModel(ItemInfo info) {
        super(info);

        if (_info.isPlant) {
            _plant = new PlantExtra(_info.plant);
        }

        if (_info.isRock) {
            _rock = new RockExtra();
        }
    }

    public int          getTile() { return _tile; }
    public PlantExtra   getPlant() { return _plant; }
    public RockExtra    getRock() { return _rock; }
    public JobModel     getJob() { return _job; }

    public boolean      canBeMined() { return _info.isRock; }
    public boolean      canBeHarvested() { return _info.isPlant; }
    public boolean      isRock() { return _info.isRock; }
    public boolean      isSolid() { return _info.isRock; }
    public boolean      isPlant() { return _info.isPlant; }

    public void         setTile(int tile) { _tile = tile; }
    public void         setJob(JobModel job) { _job = job; }
}
