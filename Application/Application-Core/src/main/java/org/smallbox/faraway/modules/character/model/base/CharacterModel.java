package org.smallbox.faraway.modules.character.model.base;

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
    private PathModel                           _path;
    protected int                               _lag;
    protected MoveListener                      _moveListener;
    private double                              _moveStep;
    protected CharacterInfo                     _type;
    private boolean                             _isSleeping;
    protected Map<Class<? extends CharacterExtra>, CharacterExtra>  _extra = new ConcurrentHashMap<>();

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

    public boolean                      isAlive() { return _isAlive; }
    public boolean                      isDead() { return !_isAlive; }
    public boolean                      isSleeping() { return _isSleeping; }
    public boolean                      isFree() { return getJob() == null && _path == null; }

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

    public void        move() {
        if (_path != null) {
            // Character is sleeping
            if (_isSleeping) {
                Log.debug("Character #" + _id + ": sleeping . move canceled");
                return;
            }

            // Increase move progress
            _moveStep = Application.config.game.characterSpeed / Application.gameManager.getGame().getTickPerHour();
//            _moveStep = 1 * getExtra(CharacterStatsExtra.class).speed * (_job != null ? _job.getSpeedModifier() : 1);

            // Character has reach next parcel
            if (_moveProgress >= 1 && _path.getCurrentParcel() != null) {
                _moveProgress = 0;

                // Move continue, set next parcel + direction
                if (_path.next()) {
                    int fromX = _parcel.x;
                    int fromY = _parcel.y;
                    int toX = _path.getCurrentParcel().x;
                    int toY = _path.getCurrentParcel().y;
                    _direction = getDirection(fromX, fromY, toX, toY);

                    setParcel(_path.getCurrentParcel());
                }

                // Move state, set path to null and call listener
                else {
                    Log.info(getName() + " Move state (" + _path.getFirstParcel().x + "x" + _path.getFirstParcel().y + "x" + _path.getFirstParcel().z + " to " + _path.getLastParcel().x + "x" + _path.getLastParcel().y + "x" + _path.getLastParcel().z + ")");
                    _path = null;

                    if (_moveListener != null) {
                        Log.info(getName() + " Move state: call onReach");
                        MoveListener listener = _moveListener;
                        _moveListener = null;
                        listener.onReach(this);
                    }
                }
            }

            _moveProgress += _moveStep;
            _moveProgress2 += _moveStep;
        }
    }

    private Direction getDirection(int fromX, int fromY, int toX, int toY) {
        if (toX > fromX && toY > fromY) return Direction.BOTTOM_RIGHT;
        if (toX < fromX && toY > fromY) return Direction.BOTTOM_LEFT;
        if (toX > fromX && toY < fromY) return Direction.TOP_RIGHT;
        if (toX < fromX && toY < fromY) return Direction.TOP_LEFT;
        if (toX > fromX) return Direction.RIGHT;
        if (toX < fromX) return Direction.LEFT;
        if (toY > fromY) return Direction.BOTTOM;
        if (toY < fromY) return Direction.TOP;
        return Direction.NONE;
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
}
