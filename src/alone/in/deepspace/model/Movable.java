package alone.in.deepspace.model;

import java.util.HashMap;
import java.util.Vector;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;

import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.PathManager.PathManagerCallback;
import alone.in.deepspace.model.Character.Direction;

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

	private HashMap<Integer, Integer> _points;
	private int _lastY;

	public Movable(int id, int x, int y) {
		  _id = id;
		  _posY = _toX = y;
		  _posX = _toY = x;
		  _points = new HashMap<Integer, Integer>();
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
	  
	  System.out.println(_posX + ", " + _posY + " to " + _toX + ", " + _toY);
	  compute(_posX, _posY, _toX, _toY);
//	  compute(10, 10, 20, 14);
	
	  if (path.size() == 0) {
		sendEvent(CharacterNeeds.Message.MSG_BLOCKED);
		return;
	  }
	
	  _blocked = 0;
	
	  _toX = job.getX();
	  _toY = job.getY();
	
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

	private void compute(int fromX, int fromY, int toX, int toY) {
		_points.clear();
		
		int offsetX = (int)(toX - fromX);
		int offsetY = (int)(toY - fromY);
		
		int centerX = fromX + (toX - fromX) / 2;
		int centerY = fromY + (toY - fromY) / 2;
		
		double x = 0;
		if (fromY > toY) {
			x = centerX + offsetY;
		} else {
			x = centerX - offsetY;
		}
		
		double y = 0; 
		if (fromX > toX) {
			y = centerY + offsetX;
		} else {
			y = centerY - offsetX;
		}

		double x2 = offsetX / 2 + fromX - offsetY;
		double y2 = offsetY / 2 + fromY - offsetX;
		
		System.out.println("-------------------");
		System.out.println("from: " + fromX + " x " + fromY);
		System.out.println("to: " + toX + " x " + toY);
		System.out.println("offset: " + offsetX + " x " + offsetY);
		System.out.println("point: " + x + " x " + y);

//		Sprite sprite = null;
//		
//		sprite = SpriteManager.getInstance().getIcon(ServiceManager.getData().getItemInfo("base.chair"));
//		sprite.setPosition((int)(fromX * 32), (int)(fromY * 32));
//		app.draw(sprite, render);
//		
//		sprite = SpriteManager.getInstance().getIcon(ServiceManager.getData().getItemInfo("base.chair"));
//		sprite.setPosition((int)(toX * 32), (int)(toY * 32));
//		app.draw(sprite, render);
//
//		sprite = SpriteManager.getInstance().getIcon(ServiceManager.getData().getItemInfo("base.light"));
//		sprite.setPosition((int)(x * 32), (int)(y * 32));
//		app.draw(sprite, render);
//		
//		sprite = SpriteManager.getInstance().getIcon(ServiceManager.getData().getItemInfo("base.light"));
//		sprite.setPosition((int)(x2 * 32), (int)(y2 * 32));
//		app.draw(sprite, render);

		double r = Math.sqrt(Math.pow(Math.abs(x - fromX), 2) + Math.pow(Math.abs(y - fromY), 2));

			double rad2OffsetX = (x - fromX)/r;
			double rad2OffsetY = (y - fromY)/r;
			double rad2 = Math.acos((rad2OffsetX));
			//double rad2 = Math.asin(rad2OffsetY);
			
//			sprite = SpriteManager.getInstance().getIcon(ServiceManager.getData().getItemInfo("base.light"));
//			sprite.setPosition((int)(x+radOffsetX*r)*32, (int)((y+radOffsetY*r)*32));
//			app.draw(sprite, render);
			
//			System.out.println("r: " + r);
//			System.out.println("bornes: " + (Math.PI-1.7 )+ " x " + (Math.PI-0.7));
			System.out.println("rad: " + rad2OffsetX + " x " + rad2OffsetY + " = " + rad2 + " x " + rad2);
		
			double rad1OffsetX = (x - toX)/r;
			double rad1OffsetY = (y - toY)/r;
			double rad1 = Math.acos((rad1OffsetX));
			//double rad2 = Math.asin(radOffsetY);
			
//			sprite = SpriteManager.getInstance().getIcon(ServiceManager.getData().getItemInfo("base.light"));
//			sprite.setPosition((int)(x+radOffsetX*r)*32, (int)((y+radOffsetY*r)*32));
//			app.draw(sprite, render);
//
//			System.out.println("bornes: " + (Math.PI-1.7 )+ " x " + (Math.PI-0.7));
			System.out.println("rad: " + rad1OffsetX + " x " + rad1OffsetY + " = " + rad1 + " x " + rad1);
		
		
		double from = Math.min(Math.PI-rad1, Math.PI-rad2);
		double to = Math.max(Math.PI-rad1, Math.PI-rad2);
		for (double i = from; i < to; i += 0.001) {
//				sprite = SpriteManager.getInstance().getIcon(ServiceManager.getData().getItemInfo("base.light"));
//				sprite.setPosition((int)(x*32 + Math.cos(i)*r*32), (int)(y*32 + Math.sin(i)*r*32));
//				app.draw(sprite, render);
			
			//_points.add(new Position((float)(x*32 + Math.cos(i)*r*32), (float)(y*32 + Math.sin(i)*r*32)));
//			System.out.println("x: " + x*32 + ", y: " + y);
			_points.put((int)(x*32 + Math.cos(i)*r*32), (int)(y*32 + Math.sin(i)*r*32));
		}

		//System.exit(0);
	}

	public int getSmoothY(int posX) {
		if (_points != null && _points.get(posX) != null) {
			_lastY = _points.get(posX);
		}
		return _lastY;
	}

	protected void  addMessage(CharacterNeeds.Message msgBlocked, int count) {
	  //_messages[msgBlocked] = count;
	}
	
	protected void  removeMessage(int msg) {
	  //_messages[msg] = MESSAGE_COUNT_INIT;
	}
	
}
