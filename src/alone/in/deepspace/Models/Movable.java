package alone.in.deepspace.Models;

import java.util.Vector;

import alone.in.deepspace.Character.Character.Direction;
import alone.in.deepspace.Character.CharacterNeeds;
import alone.in.deepspace.Character.PathManagerCallback;
import alone.in.deepspace.Managers.JobManager;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;

public class Movable implements PathManagerCallback {
	protected Position			_node;
	protected int				_posX;
	protected int				_posY;
	protected int				_toX;
	protected int				_toY;
	protected int				_id;
	protected int				_frameIndex;
	protected int				_blocked;
	protected Direction			_direction;
	protected Vector<Position>	_path;
	protected int				_steps;
	protected Job				_job;

	public Movable(int id, int x, int y) {
		  _id = id;
		  _posY = _toX = y;
		  _posX = _toY = x;
		  _frameIndex = (int) (Math.random() * 1000 % 20);
	}
	
	public int				getX() { return _posX; }
	public int				getY() { return _posY; }
	public int				getId() { return _id; }
	public int 				getPosX() { return _posX; }
	public int 				getPosY() { return _posY; }
	public Direction		getDirection() { return _direction; }
	public int				getFrameIndex() { return _frameIndex++; }

	@Override
	public void	onPathComplete(Vector<Position> path, Job job) {
	  Log.debug("Charactere #" + _id + ": go(" + _posX + ", " + _posY + " to " + _toX + ", " + _toY + ")");
	
	  if (path.size() == 0) {
		sendEvent(CharacterNeeds.Message.MSG_BLOCKED);
		return;
	  }
	
	  _blocked = 0;
	
	  _toX = job.getX();
	  _toY = job.getY();
	
	  Log.debug("Charactere #" + _id + ": go(" + _posX + ", " + _posY + " to " + _toX + ", " + _toY + ")");
	
	  // if (_path != null) {
	  // 	_path.FreeSolutionNodes();
	  // 	Debug() + "free 1";
	  // 	_path.EnsureMemoryFreed();
	  // 	delete _path;
	  // 	_path = null;
	  // }
	
	  _path = path;
	  _steps = 0;
	}
	
	@Override
	public void	onPathFailed(Job job) {
		JobManager.Action action = job.getAction(); 
		if (action == JobManager.Action.MOVE) {
			Log.warning("Move failed (no path)");
		}
		if (action == JobManager.Action.USE) {
		  Log.warning("Use failed (no path)");
		}
		if (action == JobManager.Action.BUILD) {
			Log.warning("Build failed (no path)");
		}
		sendEvent(CharacterNeeds.Message.MSG_BLOCKED);
	
		// Give up job
		JobManager.getInstance().abort(job, Job.Abort.BLOCKED);
		_job = null;
	}
	
	protected void	setDirection(Direction direction) {
		if (_direction != direction) {
			_direction = direction;
		}
	}
	
	void	sendEvent(CharacterNeeds.Message msgBlocked) {
		if (msgBlocked == CharacterNeeds.Message.MSG_BLOCKED) {
			if (++_blocked >= Constant.BLOCKED_COUNT_BEFORE_MESSAGE) {
				addMessage(CharacterNeeds.Message.MSG_BLOCKED, -1);
			}
		}
	}

	void  addMessage(CharacterNeeds.Message msgBlocked, int count) {
	  //_messages[msgBlocked] = count;
	}
	
	void  removeMessage(int msg) {
	  //_messages[msg] = MESSAGE_COUNT_INIT;
	}
	
}
