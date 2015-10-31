package org.smallbox.faraway.core.game.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Utils;

public abstract class MovableModel extends ObjectModel {

    public double getMoveProgress() {
        return _moveProgress;
    }

    public void setDirection(Direction direction) {
        _direction = direction;
    }

    public interface OnPathComplete {
        void    onPathFailed(JobModel job);
        void    onPathComplete(GraphPath<ParcelModel> path, JobModel job);
    }

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
    };

    protected int                       _id;
    protected int                       _frameIndex;
    protected Direction                 _direction;
    protected JobModel                  _job;
    protected OnPathComplete            _onPathComplete;
    protected double                    _moveProgress;
    protected ParcelModel               _parcel;

    public MovableModel(int id, ParcelModel parcel) {
        Utils.useUUID(id);
        _id = id;
        _parcel = parcel;
        _frameIndex = (int) (Math.random() * 1000 % 20);
    }

    public int              getId() { return _id; }
    public ParcelModel      getParcel() { return _parcel; }
    public Direction        getDirection() { return _direction; }
    public int              getFrameIndex() { return _frameIndex++; }

    public void             setParcel(ParcelModel parcel) { _parcel = parcel; }

    public abstract void    onPathFailed(JobModel job, ParcelModel fromParcel, ParcelModel toParcel);
    public abstract void    onPathComplete(GraphPath<ParcelModel> path, JobModel job, ParcelModel fromParcel, ParcelModel toParcel);

}
