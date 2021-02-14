package org.smallbox.faraway.core.game.model;

import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.ObjectModel;
import org.smallbox.faraway.game.world.Parcel;

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
        UNDER,
        OVER,
        NONE
    }

    protected int                       _frameIndex;
    public Direction                 _direction;
    protected JobModel job;
    public double                       _moveProgress;
    public double                    _moveProgress2;
    public Parcel _parcel;

    public MovableModel(int id, Parcel parcel) {
        super(id);

        setParcel(parcel);
        _frameIndex = (int) (Math.random() * 1000 % 20);
    }

    public int              getId() { return _id; }
    public Parcel getParcel() { return _parcel; }
    public Direction        getDirection() { return _direction; }
    public int              getFrameIndex() { return _frameIndex++; }
    public double           getMoveProgress2() { return _moveProgress2; }

    public void             setParcel(Parcel parcel) {
        assert parcel != null;
        _parcel = parcel;
    }
    public void             setDirection(Direction direction) {
        _direction = direction;
    }
}
