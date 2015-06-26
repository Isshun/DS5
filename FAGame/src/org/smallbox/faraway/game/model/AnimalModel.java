package org.smallbox.faraway.game.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.PathManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.planet.RegionInfo;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 26/06/2015.
 */
public class AnimalModel extends MovableModel {
    private final RegionInfo.RegionFauna    _info;
    private ParcelModel                     _fromParcel;
    private ParcelModel                     _toParcel;
    private double                          _speed = 0.25;
    private ParcelModel                     _parcel;

    public AnimalModel(int id, RegionInfo.RegionFauna faunaInfo, int x, int y) {
        super(id, x, y);

        _parcel = Game.getWorldManager().getParcel(x, y);
        _info = faunaInfo;
        _direction = Direction.NONE;
    }

    public boolean isMoving() {
        return _path != null;
    }

    public void moveTo(int x, int y) {
        _toX = x;
        _toY = x;

        _fromParcel = Game.getWorldManager().getParcel(_posX, _posY);
        _toParcel = Game.getWorldManager().getParcel(_toX, _toY);

        // Already on position
        if (_posX != _toX || _posY != _toY) {
            Log.debug("move to: " + _toX + "x" + _toY);
            PathManager.getInstance().getPathAsync(null, this, null, _toX, _toY);
        }
    }

    public void		move() {
        _move = Direction.NONE;

        if (_path == null) {
            return;
        }

        // Goto node
        if (_node != null) {
            // _node.PrintNodeInfo();

            // Set direction
            int x = _node.getX();
            int y = _node.getY();
            if (x > _posX && y > _posY) setMove(Direction.BOTTOM_RIGHT);
            else if (x < _posX && y > _posY) setMove(Direction.BOTTOM_LEFT);
            else if (x > _posX && y < _posY) setMove(Direction.TOP_RIGHT);
            else if (x < _posX && y < _posY) setMove(Direction.TOP_LEFT);
            else if (x > _posX) setMove(Direction.RIGHT);
            else if (x < _posX) setMove(Direction.LEFT);
            else if (y > _posY) setMove(Direction.BOTTOM);
            else if (y < _posY) setMove(Direction.TOP);

            // Increase move progress
            _moveProgress += 0.75 * _speed * (_job != null ? _job.getSpeedModifier() : 1);
            if (_moveProgress < 1) {
                return;
            }
            _moveProgress = 0;

            _parcel = _node;
            _posX = x;
            _posY = y;
            _steps++;
            Log.debug("Character #" + _id + ": goto " + _posX + " x " + _posY + ", step: " + _steps);
        }

        // Next node
        if (_path.getCount() > _steps) {
            Log.debug("Character #" + _id + ": move");
            _node = _path.get(_steps);
        } else {
            Log.debug("Character #" + _id + ": reached");
            _steps = 0;
            _path = null;
            _node = null;
            _moveProgress = 0;
        }
    }

    @Override
    public void	onPathFailed(BaseJobModel job, ParcelModel fromParcel, ParcelModel toParcel) {
        if (_fromParcel == fromParcel && _toParcel == toParcel) {
            Log.warning("Job failed (no path)");
            UserInterface.getInstance().displayMessage("blocked", _posX, _posY);

            if (_onPathComplete != null) {
                _onPathComplete.onPathFailed(job);
            }
        }
    }

    @Override
    public void	onPathComplete(GraphPath<ParcelModel> path, BaseJobModel job, ParcelModel fromParcel, ParcelModel toParcel) {
        if (_fromParcel == fromParcel && _toParcel == toParcel) {
            Log.debug("Character #" + _id + ": go(" + _posX + ", " + _posY + " to " + _toX + ", " + _toY + ")");

            if (path.getCount() == 0) {
                return;
            }

            _blocked = 0;

            _toX = toParcel.getX();
            _toY = toParcel.getY();
            _path = path;
            _steps = 0;

            if (_onPathComplete != null) {
                _onPathComplete.onPathComplete(path, job);
            }
        }
    }

    public void action() {
    }
}
