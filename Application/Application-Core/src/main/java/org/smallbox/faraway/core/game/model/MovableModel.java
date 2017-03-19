package org.smallbox.faraway.core.game.model;

import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.job.JobModel;

public abstract class MovableModel extends ObjectModel {

    public enum Direction {
        BOTTOM,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        TOP_RIGHT,
        TOP_LEFT,
        NONE
    }

    protected int                       _frameIndex;
    protected Direction                 _direction;
    protected JobModel                  _job;
    protected double                    _moveProgress;
    protected ParcelModel               _parcel;

    public MovableModel(int id, ParcelModel parcel) {
        super(id);

        _parcel = parcel;
        _frameIndex = (int) (Math.random() * 1000 % 20);
    }

    public int              getId() { return _id; }
    public ParcelModel      getParcel() { return _parcel; }
    public Direction        getDirection() { return _direction; }
    public int              getFrameIndex() { return _frameIndex++; }

    public void             setParcel(ParcelModel parcel) {
        assert parcel != null;
        _parcel = parcel;
    }
    public void             setDirection(Direction direction) {
        _direction = direction;
    }
}
