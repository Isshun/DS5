package org.smallbox.faraway.game.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.util.Utils;

public abstract class MovableModel {

	public double getMoveProgress() {
		return _moveProgress;
	}

	public void setY(int y) {
		_posY = y;
	}

	public void setX(int x) {
		_posX = x;
	}

	public interface OnPathComplete {
		void	onPathFailed(BaseJobModel job);
		void	onPathComplete(GraphPath<ParcelModel> path, BaseJobModel job);
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

	protected ParcelModel 				_node;
	protected int						_posX;
	protected int						_posY;
	protected int						_toX;
	protected int						_toY;
	protected int						_id;
	protected int						_frameIndex;
	protected int						_blocked;
	protected Direction					_direction;
	protected Direction 				_move;
	protected GraphPath<ParcelModel> 	_path;
	protected int						_steps;
	protected BaseJobModel 				_job;
	protected OnPathComplete			_onPathComplete;
	protected double                    _moveProgress;

	public MovableModel(int id, int x, int y) {
		Utils.useUUID(id);
		_id = id;
		_posX = _toX = x;
		_posY = _toY = y;
		_frameIndex = (int) (Math.random() * 1000 % 20);
	}
	
	public int				getId() { return _id; }
	public int 				getX() { return _posX; }
	public int 				getY() { return _posY; }
	public Direction		getDirection() { return _direction; }
	public Direction 		getMove() { return _move; }
	public int				getFrameIndex() { return _frameIndex++; }

	public void	setDirection(Direction direction) {
		if (_direction != direction) {
			_direction = direction;
		}
	}

	protected void setMove(Direction move) {
		_move = move;
		setDirection(move);
	}

	public abstract void	onPathFailed(BaseJobModel job, ParcelModel fromParcel, ParcelModel toParcel);
	public abstract void	onPathComplete(GraphPath<ParcelModel> path, BaseJobModel job, ParcelModel fromParcel, ParcelModel toParcel);

}
