package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.GameTask;
import org.smallbox.faraway.common.CharacterPositionCommon;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CharacterModel extends MovableModel {

    public boolean                              _isAlive = true;
    protected int                               _lag;
    public MoveListener                      _moveListener;
    public double                              _moveStep;
    protected CharacterInfo                     _type;
    private boolean                             _isSleeping;
    public CharacterPositionCommon position = new CharacterPositionCommon();
    protected Map<Class<? extends CharacterExtra>, CharacterExtra>  _extra = new ConcurrentHashMap<>();
    public PathModel _path;
    public GameTask _task;

    public CharacterModel(int id, CharacterInfo characterInfo, ParcelModel parcel) {
        super(id, parcel);

        Log.info("Character #" + id);

        _type = characterInfo;
        _lag = (int)(Math.random() * 10);
        _direction = Direction.NONE;
    }

    public <T extends CharacterExtra> T getExtra(Class<T> cls) { return (T) _extra.get(cls); }
    public JobModel                     getJob() { return _job; }
    public ParcelModel                  getParcel() { return _parcel; }
    public CharacterInfo                getType() { return _type; }
    public abstract String              getName();

    public abstract void                addBodyStats(CharacterStatsExtra stats);

    public void                         setIsDead() { _isAlive = false; }
    public void                         setParcel(ParcelModel parcel) {
        if (parcel == null) {
            throw new GameException(CharacterModel.class, "setParcel: cannot be null");
        }
        _parcel = parcel;
    }

    public boolean                      isFree() { return _task == null; }
//    public boolean                      isFree() { return getJob() == null && _path == null; }
    public boolean                      isAlive() { return _isAlive; }
    public boolean                      isDead() { return !_isAlive; }
    public boolean                      isSleeping() { return _isSleeping; }

    /**
     * Déplace le personnage à la position demandée
     *
     * @param parcel Destination
     * @return True si le personnage est déjà à la position voulue
     */
    public boolean moveTo(ParcelModel parcel) {
        return moveTo(parcel, null);
    }

    public boolean moveTo(ParcelModel parcel, MoveListener<CharacterModel> listener) {
        assert parcel != null;

        // Déjà entrain de se déplacer vers la postion
        if (_path != null && _path.getLastParcel() == parcel) {
            return false;
        }

        // Déjà à la position désirée
        if (_path == null && parcel == _parcel) {
            if (listener != null) {
                listener.onReach(this);
            }
            return true;
        }

        Log.info("Move character to " + parcel.x + "x" + parcel.y);

        if (_moveListener != null) {
            Log.debug("[" + getName() + "] Cancel previous move listener");
            _moveListener.onFail(this);
            _moveListener = null;
        }

        _path = Application.dependencyInjector.getObject(PathManager.class).getPath(_parcel, parcel, false, false);
        _moveProgress2 = 0;
        if (_path != null) {
            _moveListener = listener;
        } else if (listener != null) {
            listener.onFail(this);
        }

        return false;
    }

    public void    setJob(JobModel job) {
        assert _job == null;
        assert job != null;

        _job = job;
    }

    public void            action(double hourInterval) {
        if (_job != null) {
            _job.action(this, hourInterval);
        }
    }

    public void clearJob(JobModel job) {
        if (_job != job) {
            throw new GameException(CharacterModel.class, "clearJob: job not match character current job", _job, job, this);
        }

        _job = null;
        _moveListener = null;
        _moveProgress = 0;
        _moveProgress2 = 0;
        _path = null;
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
}
