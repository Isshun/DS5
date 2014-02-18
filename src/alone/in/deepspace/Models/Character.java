package alone.in.deepspace.Models;
import java.util.Vector;

import alone.in.deepspace.Managers.CharacterManager;
import alone.in.deepspace.Managers.JobManager;
import alone.in.deepspace.Managers.PathManager;
import alone.in.deepspace.Managers.ResourceManager;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.World.StructureItem;
import alone.in.deepspace.World.WorldMap;

public class Character implements PathManagerCallback {
	
	final String[] firstname = {
	  // male
	  "Galen",
	  "Lewis",
	  "Benjamin",
	  "Michael",
	  "Jonathan",
	  "Gaius",
	  "Samuel",
	  "Wesley",
	  // female
	  "Jadzia",
	  "Janice",
	  "Alice",
	  "Kathryn",
	  "Beverly",
	  "Willow",
	  "Tasha",
	  "Samantha"
	};
	
	final String[] shortFirstname = {
	  // male
	  "Matt",
	  "Jack",
	  "Adam",
	  "Bill",
	  "Tom",
	  "Saul",
	  "Lee",
	  // female
	  "Tory",
	  "Vic",
	  "Ezri",
	  "Ellen",
	  "Kara",
	  "Emma",
	  "Wade",
	  "Amy"
	};
	
	final String[] middlename = {
	  "Crashdown",
	  "Hardball",
	  "Apollo",
	  "Boomer",
	  "Doc",
	  "Starbuck",
	  "Hotdog",
	  "Jammer",
	  "Trip",
	  "Helo",
	  "Dee",
	  "Oz",
	  "Klaus",
	  "Mac",
	  "Betty",
	  "Six"
	};
	
	final String[] shortLastname = {
	  "Mudd",
	  "Dax",
	  "Nerys",
	  "Laren",
	  "Rand",
	  "McCoy",
	  "Adama",
	  "Tyrol",
	  "Reed",
	  "Sisko",
	  "Riker",
	  "Wells",
	  "Quinn",
	  "Weir",
	  "Rush",
	  "Tyler"
	};
	
	final String[] lastname = {
	  "Zimmerman",
	  "Anders",
	  "Barclay",
	  "Archer",
	  "Thrace",
	  "Summers",
	  "Holmes",
	  "Wildman",
	  "Lawton",
	  "Mallory",
	  "Beckett",
	  "Hammond",
	  "O'Neill",
	  "Sheppard",
	  "Cooper",
	  "Hartness"
	};

	  public enum Direction {
		DIRECTION_BOTTOM,
		DIRECTION_LEFT,
		DIRECTION_RIGHT,
		DIRECTION_TOP,
		DIRECTION_BOTTOM_RIGHT,
		DIRECTION_BOTTOM_LEFT,
		DIRECTION_TOP_RIGHT,
		DIRECTION_TOP_LEFT,
		DIRECTION_NONE
	  };

	  enum Gender {
		GENDER_NONE,
		GENDER_MALE,
		GENDER_FEMALE,
		GENDER_BOTH
	  };
	  
	  Position					_node;
	  CharacterNeeds			_needs;
	  private int				_posX;
	  private int				_posY;
	  private int				_toX;
	  private int				_toY;
	  private int				_id;
	  private int				_frameIndex;
	  private Gender			_gender;
	  private String			_name;
	  private Vector<Position>	_path;
	  private int				_steps;
	  private Profession		_profession;
	  private boolean			_selected;
	  private int				_blocked;
	  private Direction			_direction;
	  private int				_offset;
	  private Job				_job;

//	  private int				_messages[32];

	  public Character(int id, int x, int y, String name) {
		  Log.debug("Character #" + id);

		  _id = id;
		  _gender = Math.random() * 1000 % 2 == 0 ? Character.Gender.GENDER_MALE : Character.Gender.GENDER_FEMALE;
		  // _path = null;
		  _posY = _toX = y;
		  _posX = _toY = x;
		  _selected = false;
		  _blocked = 0;
		  _frameIndex = (int) (Math.random() * 1000 % 20);
		  _direction = Direction.DIRECTION_NONE;
		  _offset = 0;
		  _node = null;
		  _job = null;
		  _needs = new CharacterNeeds();
		  _steps = 0;
		  _name = name;

		  //memset(_messages, MESSAGE_COUNT_INIT, CHARACTER_MAX_MESSAGE * sizeof(int));

		  int offset = (_gender == Character.Gender.GENDER_FEMALE ? 4 : 0);
		  if (name == null) {
			  if ((Math.random() * 1000) % 2 == 0) {
				  _name = shortFirstname[(int) ((Math.random() * 1000) % 8 + offset)]
						  + " ("
						  + middlename[(int) ((Math.random() * 1000) % 16)]
						  + ") "
						  + shortLastname[(int) ((Math.random() * 1000) % 16)];
			  } else {
			    _name = firstname[(int) ((Math.random() * 1000) % 8 + offset)]
						+ " "
			    		+ lastname[(int) ((Math.random() * 1000) % 16)];
			  }
		  }

		  Log.debug("Character done: " + _name + " (" + x + ", " + y + ")");
	}
	  
	public void				setSelected(boolean selected) { _selected = selected; }
	public void				setName(String name) { _name = name; }
	public void				setOffset(int offset) { _offset = offset; }

	public Direction		getDirection() { return _direction; }
	public Profession		getProfession() { return _profession; }
	public Profession.Type	getProfessionId() { return _profession.getType(); }
	public Job				getJob() { return _job; }
	public int				getX() { return _posX; }
	public int				getY() { return _posY; }
	public int				getId() { return _id; }
	public String			getName() { return _name; }
	public CharacterNeeds	getNeeds() { return _needs; }
//	  int[]				getMessages() { return _messages; }
	public boolean			getSelected() { return _selected; }
	public int				getFrameIndex() { return _frameIndex++; }
	public int				getOffset() { return _offset; }
	public int 				getPosX() { return _posX; }
	public int 				getPosY() { return _posY; }
	public int				getProfessionScore(Profession.Type professionEngineer) { return 42; }
	
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
	  // WorldMap.getInstance().buildAbort(job.getItem());
	  JobManager.getInstance().abort(job);
	  _job = null;
	}
	
	void	setDirection(Direction direction) {
	  if (_direction != direction) {
		_direction = direction;
		_offset = 0;
	  }
	}

public void	setJob(Job job) {
  Log.debug("set new job");

  if (_job != null) {
	JobManager.getInstance().abort(_job);
  }
  _job = job;
  _toX = job.getX();
  _toY = job.getY();
  if (_posX != job.getX() || _posY != job.getY()) {
	PathManager.getInstance().getPathAsync(this, job);
  }
}

public void	setProfession(Profession.Type professionId) {
  Profession[] professions = CharacterManager.getInstance().getProfessions();

	for (int i = 0; i < professions.length; i++) {
		Profession profession = professions[i];
		if (profession.getType() == professionId) {
			Log.debug("setProfession: " + professions[i].getName());
			_profession = professions[i];
		}
	}
}

// void		go(AStarSearch<MapSearchNode> astarsearch, Job job) {
//   if (astarsearch == null) {
// 	sendEvent(MSG_BLOCKED);
// 	return;
//   }

//   _blocked = 0;

//   _toX = job.getX();
//   _toY = job.getY();

//   Debug() + "Charactere #" + _id + ": go(" + _posX + ", " + _posY + " to " + toX + ", " + toY + ")";

//   if (_astarsearch != null) {
//   	_astarsearch.FreeSolutionNodes();
//   	Debug() + "free 1";
//   	_astarsearch.EnsureMemoryFreed();
//   	delete _astarsearch;
//   	_astarsearch = null;
//   }

//   _astarsearch = astarsearch;
//   _steps = 0;
// }

// void	use(AStarSearch<MapSearchNode> path, Job job) {
//   Info() + "Character #" + _id +": use item type: " + item.getType();

//   // If character currently building item: abort
//   if (_job != null && _job.getItem() != null && _job.getItem().isComplete() == false) {
// 	WorldMap.getInstance().buildAbort(_job.getItem());
// 	_job = null;
//   }

//   // Go to new item
//   int toX = item.getX();
//   int toY = item.getY();
//   _item = item;
//   go(path, toX, toY);
// }

// void	build(AStarSearch<MapSearchNode> path, Job job) {
//   Info() + "Character #" + _id + ": build item type: " + item.getType();

//   _build = item;
//   _build.setOwner(this);
//   int toX = item.getX();
//   int toY = item.getY();
//   go(path, toX, toY);
// }

// void	setItem(BaseItem item) {
//   BaseItem currentItem = _item;

//   _item = item;

//   if (currentItem != null && currentItem.getOwner() != null) {
// 	currentItem.setOwner(null);
//   }

//   if (item != null && item.getOwner() != this) {
// 	item.setOwner(this);
//   }
// }

void  addMessage(CharacterNeeds.Message msgBlocked, int count) {
  //_messages[msgBlocked] = count;
}

void  removeMessage(int msg) {
  //_messages[msg] = MESSAGE_COUNT_INIT;
}

public void  updateNeeds(int count) {
  _needs.update();
}

void	sendEvent(CharacterNeeds.Message msgBlocked) {
  if (msgBlocked == CharacterNeeds.Message.MSG_BLOCKED) {
	if (++_blocked >= Constant.BLOCKED_COUNT_BEFORE_MESSAGE) {
	  addMessage(CharacterNeeds.Message.MSG_BLOCKED, -1);
	}
  }
}

public void  update() {

  // Character is busy
  if (_job != null) {
	return;
  }

  // Character is sleeping
  if (_needs.isSleeping()) {
	return;
  }

  // // Chatacter moving to a position
  // if (_astarsearch != null) {
  // }

  // Chatacter moving to bedroom
  if (_job != null && _job.getItem() != null && _job.getItem().isSleepingItem()) {
	return;
  }

  // TODO
  // Energy
  if (_needs.getEnergy() < 20) {
	Log.debug("Charactere #" + _id + ": need sleep: " + _needs.getEnergy());

	// Need sleep
	JobManager.getInstance().need(this, BaseItem.Type.QUARTER_BED);

	// // Sleep in chair
	// {
	//   BaseItem item = WorldMap.getInstance().find(BaseItem.QUARTER_CHAIR, true);
	//   if (item != null) {
	// 	PathManager.getInstance().getPathAsync(this, item);
	// 	_goal = GOAL_USE;
	// 	return;
	//   }
	// }

  }

  // If character have a job . do not interrupt
  if (_job != null) {
	PathManager.getInstance().getPathAsync(this, _job);
	return;
  }

  // // Need food
  // if (_food <= LIMITE_FOOD_OK) {

  // 	// If character already go to needed item
  // 	if (_path != null) {
  // 	  BaseItem item = WorldMap.getInstance().getItem(_toX, _toY);
  // 	  if (item != null && item.getType() == BaseItem.BAR_PUB) {
  // 		return;
  // 	  }
  // 	}

  // 	Debug() + "Charactere #" + _id + ": need food";
  // 	BaseItem item = WorldMap.getInstance().find(BaseItem.BAR_PUB, false);
  // 	if (item != null) {
  // 	  Debug() + "Charactere #" + _id + ": Go to pub !";

  // 	  PathManager.getInstance().getPathAsync(this, _job);
	
  // 	  // if (path != null) {
  // 	  // 	use(path, item);
  // 	  // 	return;
  // 	  // } else {
  // 	  // 	sendEvent(MSG_BLOCKED);
  // 	  // }
  // 	} else {
  // 	  Debug() + "Charactere #" + _id + ": no pub :(";
  // 	}
  // }

}

public void		move() {
  _direction = Direction.DIRECTION_NONE;

  // Character is sleeping
  if (_needs.isSleeping()) {
	  Log.debug("Character #" + _id + ": sleeping . move canceled");
	return;
  }

  // Goto node
  if (_node != null) {
	// _node.PrintNodeInfo();

	// Set direction
	if (_node.x > _posX && _node.y > _posY) setDirection(Direction.DIRECTION_BOTTOM_RIGHT);
	else if (_node.x < _posX && _node.y > _posY) setDirection(Direction.DIRECTION_BOTTOM_LEFT);
	else if (_node.x > _posX && _node.y < _posY) setDirection(Direction.DIRECTION_TOP_RIGHT);
	else if (_node.x < _posX && _node.y < _posY) setDirection(Direction.DIRECTION_TOP_LEFT);
	else if (_node.x > _posX) setDirection(Direction.DIRECTION_RIGHT);
	else if (_node.x < _posX) setDirection(Direction.DIRECTION_LEFT);
	else if (_node.y > _posY) setDirection(Direction.DIRECTION_BOTTOM);
	else if (_node.y < _posY) setDirection(Direction.DIRECTION_TOP);

	_posX = (int) _node.x;
	_posY = (int) _node.y;
	_steps++;
	Log.debug("Charactere #" + _id + ": goto " + _posX + " x " + _posY + ", step: " + _steps);
  }

  // Next node
  if (_path != null && (int)_path.size() > _steps) {
	  Log.debug("Character #" + _id + ": move");

	_node = _path.get(_steps);
	
	// if (_steps == 0) {
	// } else {
	//   _node = _path.GetSolutionNext();
	// }

	// // clear path
	// if (_node == null) {
	//   Debug() + "Character #" + _id + ": reached";
	//   _path.FreeSolutionNodes();
	//   Debug() + "free 3";
	//   _path.EnsureMemoryFreed();
	//   delete _path;
	//   _path = null;
	// }
  } else {
	  _path = null;
	  _node = null;
  }
}

// TODO: make objects stats table instead switch
void		actionUse() {
  // Wrong call
  if (_job == null || _job.getItem() == null) {
	Log.error("Character: actionUse on null job or null job's item");
	return;
  }

  // Item is no longer exists
  if (_job.getItem() != WorldMap.getInstance().getItem(_job.getX(), _job.getY())) {
	Log.warning("Character #" + _id + ": actionUse on invalide item");
	return;
  }

  // Character is sleeping
  if (_needs.isSleeping()) {
	  Log.debug("use: sleeping . use canceled");
	return;
  }

  Log.debug("Character #" + _id + ": actionUse");

  BaseItem item = _job.getItem();
  BaseItem.Type type = item.getType();

	// Bar
  if (type == BaseItem.Type.BAR_PUB) {
	_needs.eat();
	  // BaseItem item = WorldMap.getInstance().getRandomPosInRoom(item.getRoomId());
	  // if (item != null) {
	  // 	PathManager.getInstance().getPathAsync(this, item);
	  // 	_goal = GOAL_MOVE;
	  // }
  }

	// Bed
  if (type == BaseItem.Type.QUARTER_BED) {
	_needs.sleep(BaseItem.Type.QUARTER_BED);
	item.setOwner(this);
  }

	// Chair
  if (type == BaseItem.Type.QUARTER_CHAIR) {
	_needs.sleep(BaseItem.Type.QUARTER_CHAIR);
	item.setOwner(this);
  }

  JobManager.getInstance().complete(_job);
  _job = null;
}

	void		actionBuild() {
	  // Wrong call
	  if (_job == null || _job.getItem() == null) {
		  Log.error("Character: actionBuild on null job or null job's item");
		  JobManager.getInstance().cancel(_job);
		  _job = null;
		  return;
	  }
	
	  // Item is no longer exists
	  BaseItem item = _job.getItem();
	  StructureItem currentStructure = WorldMap.getInstance().getStructure(_job.getX(), _job.getY());
	  BaseItem currentItem = WorldMap.getInstance().getItem(_job.getX(), _job.getY());
	  if (item != currentStructure && item != currentItem) {
		  if (item != currentStructure) {
			  Log.warning("Character #" + _id + ": actionBuild on invalide structure");
			  JobManager.getInstance().cancel(_job);
			  _job = null;
		  } else if (item != currentItem) {
			  Log.warning("Character #" + _id + ": actionBuild on invalide item");
			  JobManager.getInstance().cancel(_job);
			  _job = null;
		  }
		  return;
	  }
	
	
	  Log.debug("Character #" + _id + ": actionBuild");
	
	  // Build
	  ResourceManager.Message result = ResourceManager.getInstance().build(item);
	
	  if (result == ResourceManager.Message.NO_MATTER) {
		  Log.debug("Character #" + _id + ": not enough matter");
		  JobManager.getInstance().abort(_job);
		  _job = null;
	  }
	
		if (result == ResourceManager.Message.BUILD_COMPLETE) {
		  Log.debug("Character #" + _id + ": build complete");
		JobManager.getInstance().complete(_job);
		_job = null;
		}
	
		if (result == ResourceManager.Message.BUILD_PROGRESS) {
		  Log.debug("Character #" + _id + ": build progress");
		}
	}

	void		actionGather() {
	  // Wrong call
	  if (_job == null || _job.getItem() == null) {
		Log.error("Character: actionGather on null job or null job's item");
		JobManager.getInstance().cancel(_job);
		_job = null;
		return;
	  }
	
	  int value = WorldMap.getInstance().gather(_job.getItem(), getProfessionScore(Profession.Type.NONE));
	
	  Log.debug("gather: " + value);
	
	  ResourceManager.getInstance().addMatter(value);
	
	  if (_job.getItem().getMatterSupply() == 0) {
		JobManager.getInstance().complete(_job);
		_job = null;
	  }
	}

public void		action() {
  if (_job == null || _posX != _toX || _posY != _toY) {
	return;
  }

  JobManager.Action action = _job.getAction();
  
	if (action == JobManager.Action.MOVE) {
	JobManager.getInstance().complete(_job);
	_job = null;
  }

	if (action == JobManager.Action.USE) {
	actionUse();
  }

	if (action == JobManager.Action.GATHER) {
	actionGather();
  }

	if (action == JobManager.Action.BUILD) {
	actionBuild();
  }

  }

}
