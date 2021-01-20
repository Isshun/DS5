package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.GameTask;
import org.smallbox.faraway.common.CharacterPositionCommon;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.util.MoveListener;
import org.smallbox.faraway.util.log.Log;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CharacterModel extends MovableModel {

    public boolean                              _isAlive = true;
    protected int                               _lag;
    public MoveListener                      _moveListener;
    public double                              _moveStep;
    protected CharacterInfo                     _type;
    private boolean                             _isSleeping;
    public Direction lastDirection = Direction.RIGHT;
    public CharacterPositionCommon position = new CharacterPositionCommon();
    protected Map<Class<? extends CharacterExtra>, CharacterExtra>  _extra = new ConcurrentHashMap<>();
    public PathModel _path;
    public GameTask _task;
    private LocalDateTime lastJobDate;

    public CharacterModel(int id, CharacterInfo characterInfo, Parcel parcel) {
        super(id, parcel);

        Log.info("Character #" + id);

        _type = characterInfo;
        _lag = (int)(Math.random() * 10);
        _direction = Direction.NONE;
    }

    public <T extends CharacterExtra> T getExtra(Class<T> cls) { return (T) _extra.get(cls); }
    public JobModel                     getJob() { return job; }
    public Parcel getParcel() { return _parcel; }
    public CharacterInfo                getType() { return _type; }
    public abstract String              getName();

    public abstract void                addBodyStats(CharacterStatsExtra stats);

    public void                         setIsDead() { _isAlive = false; }
    public void                         setParcel(Parcel parcel) {
        if (parcel == null) {
            throw new GameException(CharacterModel.class, "setParcel: cannot be null");
        }
        _parcel = parcel;
    }

    public boolean                      isFree() { return _task == null || (job != null && job.isOptional()); }
//    public boolean                      isFree() { return getJob() == null && _path == null; }
    public boolean                      isAlive() { return _isAlive; }
    public boolean                      isDead() { return !_isAlive; }
    public boolean                      isSleeping() { return _isSleeping; }

    public void    setJob(JobModel job) {
        assert this.job == null;
        assert job != null;

        this.job = job;
    }

    public String toString() {
        if (hasExtra(CharacterPersonalsExtra.class)) {
            return getExtra(CharacterPersonalsExtra.class).getFirstName() + " " + getExtra(CharacterPersonalsExtra.class).getLastName();
        }
        return "no name";
    }

    public PathModel getPath() {
        return _path;
    }

    public boolean hasExtra(Class<?> cls) {
        return _extra.containsKey(cls);
    }

    public void setMoveStep(double v) {
        _moveStep = v;
    }

    public void setMoveProgress(double i) {
        _moveProgress = i;
    }

    public void setPath(PathModel path) {
        _path = path;
        _moveProgress = 0;
        _moveProgress2 = 0;
    }

    public void setLastJobDate(LocalDateTime lastJobDate) {
        this.lastJobDate = lastJobDate;
    }

    public LocalDateTime getLastJobDate() {
        return lastJobDate;
    }

    public void clearMove() {
        _moveListener = null;
        _moveProgress = 0;
        _moveProgress2 = 0;
        _path = null;
    }

    public void clearJob(JobModel job, LocalDateTime endDate) {
        if (this.job == job) {
            this.job = null;
            this.lastJobDate = endDate;
            this.clearMove();
            Log.debug("Job cleared from character: " + this);

            job.clearCharacter(this, endDate);
        }
    }
}
