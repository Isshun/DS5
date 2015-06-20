package org.smallbox.faraway.game.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.game.model.job.JobModel;

public abstract class Movable {

	public interface OnPathComplete {
		void	onPathFailed(JobModel job);
		void	onPathComplete(GraphPath<ParcelModel> path, JobModel job);
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

	protected ParcelModel 		_node;
	protected int				_posX;
	protected int				_posY;
	protected int				_toX;
	protected int				_toY;
	protected int				_id;
	protected int				_frameIndex;
	protected int				_blocked;
	protected Direction			_direction;
	protected Direction 		_move;
	protected GraphPath<ParcelModel> _path;
	protected int				_steps;
	protected JobModel 			_job;
	protected OnPathComplete	_onPathComplete;

	public Movable(int id, int x, int y) {
		Utils.useUUID(id);
		_id = id;
		_posY = _toX = y;
		_posX = _toY = x;
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
}
