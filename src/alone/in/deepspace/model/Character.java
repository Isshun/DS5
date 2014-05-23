package alone.in.deepspace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jsfml.graphics.Color;

import alone.in.deepspace.manager.CharacterManager;
import alone.in.deepspace.manager.ItemSlot;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.PathManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.CharacterRelation.Relation;
import alone.in.deepspace.model.Job.Abort;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class Character extends Movable {

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

	public enum Gender {
		GENDER_NONE,
		GENDER_MALE,
		GENDER_FEMALE,
		GENDER_BOTH
	}

	private static final Color COLOR_FEMALE = new Color(255, 180, 220);
	private static final Color COLOR_MALE = new Color(110, 200, 255);

	CharacterNeeds					_needs;
	private Gender					_gender;
	private String					_firstName;
	private Profession				_profession;
	private boolean					_selected;
	private List<BaseItem> 			_carry;
	private CharacterStatus 		_status;
	private Color 					_color;
	private int 					_lag;
	private double 					_old;
	private int 					_inventorySpace;
	private int 					_nbChild;
	private double 					_nextChildAtOld;
	private List<CharacterRelation> _relations;
	private Character 				_mate;
	private boolean 				_isGay;
	private String 					_lastName;
	private String 					_birthName;
	private Room					_quarter;

	//	  private int				_messages[32];

	public Character(int id, int x, int y, String name, String lastName, int old) {
		super(id, x, y);

		Log.info("Character #" + id);

		_old = old;
		_profession = CharacterManager.professions[id % CharacterManager.professions.length];
		_relations = new ArrayList<CharacterRelation>();
		_carry = new ArrayList<BaseItem>();
		setGender((int)(Math.random() * 1000) % 2 == 0 ? Character.Gender.GENDER_MALE : Character.Gender.GENDER_FEMALE);
		_lag = (int)(Math.random() * 10);
		_selected = false;
		_blocked = 0;
		_nextChildAtOld = -1;
		_direction = Direction.DIRECTION_NONE;
		_needs = new CharacterNeeds(this);
		_status = new CharacterStatus(this);
		_inventorySpace = Constant.CHARACTER_INVENTORY_SPACE;
		_steps = 0;
		_firstName = name;
		_isGay = (int)(Math.random() * 100) % 10 == 0;
		_lastName = _birthName = lastName;
		
		if (name == null) {
			if ((int)(Math.random() * 1000) % 2 == 0) {
				_firstName = CharacterName.getShortFirstname(_gender)
						+ " \"" + CharacterName.getMiddlename() + "\" ";
				_lastName = lastName != null ? lastName : CharacterName.getShortLastName();
			} else {
				_firstName = CharacterName.getFirstname(_gender) + " ";
				_lastName = lastName != null ? lastName : CharacterName.getLastName();
			}
		}

		Log.info("Character done: " + _firstName + " (" + x + ", " + y + ")" + _gender);
	}

	public void				setSelected(boolean selected) { _selected = selected; }
	public void				setName(String name) { _firstName = name; }
	public void 			setGender(Gender gender) {
		_gender = gender;
		_color = _gender == Gender.GENDER_FEMALE ? COLOR_FEMALE : COLOR_MALE;
	}
	public void 			setMate(Character mate) {
		if (_mate == mate) {
			return;
		}
		
		// Update lastName
		if (_gender == Gender.GENDER_FEMALE && mate.getGender() == Gender.GENDER_MALE) {
			_lastName = mate.getLastName();
		}
		
		// Break up
		if (_mate != null) {
			// Remove quarter
			if (_quarter != null && _quarter.getOwner() == _mate) {
				_quarter.removeOccupant(this);
				_quarter = null;
			}
			
			// Remove relation
			CharacterRelation r = null;
			for (CharacterRelation relation: _relations) {
				if (relation.getRelation() == Relation.MATE) {
					r = relation;
				}
			}
			_relations.remove(r);
			
			// Cancel next child
			_nextChildAtOld = -1;

			_mate.setMate(null);
			_mate = null;
		}
		
		// New mate
		if (mate != null) {
			_mate = mate;
			
			// Add relation
			_relations.add(new CharacterRelation(this, mate, Relation.MATE));
			
			// Schedule next child
			if (_gender == Gender.GENDER_FEMALE) {
				_nextChildAtOld = _old + Constant.CHARACTER_DELAY_BEFORE_FIRST_CHILD;
			}
			
			// Add quarter
			if (mate.getQuarter() != null && mate.getQuarter().getOwner() == mate) {
				if (_quarter != null) {
					_quarter.removeOccupant(this);
				}
				
				mate.getQuarter().addOccupant(this);
				_quarter = mate.getQuarter();
			}
		}
	}
	public void 			addChildren(Character children) {
		_relations.add(new CharacterRelation(this, children, Relation.CHILDREN));
	}
	public void 			addFriend(Character friend) {
		_relations.add(new CharacterRelation(this, friend, Relation.FRIEND));
	}
	public void 			setProfession(Profession profession) { _profession = profession; }

	public Profession		getProfession() { return _profession; }
	public Profession.Type	getProfessionId() { return _profession.getType(); }
	public Job				getJob() { return _job; }
	public String			getName() { return _firstName + _lastName; }
	public CharacterNeeds	getNeeds() { return _needs; }
	//	  int[]				getMessages() { return _messages; }
	public boolean			getSelected() { return _selected; }
	public int				getProfessionScore(Profession.Type professionEngineer) { return 42; }
	public List<BaseItem> 	getCarried() { return _carry; }
	public Vector<Position> getPath() { return _path; }
	public CharacterStatus 	getStatus() { return _status; }
	public Color 			getColor() { return _color; }
	public int 				getLag() { return _lag; }
	public int 				getSpace() { return _inventorySpace - _carry.size(); }
	public Gender 			getGender() { return _gender; }
	public Character 		getMate() { return _mate; }
	public String 			getLastName() { return _lastName; }
	public List<CharacterRelation> getRelations() { return _relations; }
	public double			getOld() { return _old; }
	public double 			getNextChildAtOld() { return _nextChildAtOld; }

	public boolean			isFull() { return _carry.size() == Constant.CHARACTER_INVENTORY_SPACE; }
	public boolean 			isSleeping() { return _needs._sleeping > 0; }
	public boolean 			isGay() { return _isGay; }

	public void	setJob(Job job) {
		if (_job == job) {
			return;
		}

		Log.debug("set new job");

		if (_job != null) {
			JobManager.getInstance().abort(_job, Job.Abort.INTERRUPTE);
		}
		if (job != null) {
			job.setCharacter(this);
			_job = job;
			_toX = job.getX();
			_toY = job.getY();
			if (_posX != job.getX() || _posY != job.getY()) {
				PathManager.getInstance().getPathAsync(this, job);
			}
		}
	}

	public void	setProfession(Profession.Type professionId) {
		Profession[] professions = ServiceManager.getCharacterManager().getProfessions();

		for (int i = 0; i < professions.length; i++) {
			Profession profession = professions[i];
			if (profession.getType() == professionId) {
				Log.debug("setProfession: " + professions[i].getName());
				setProfession(professions[i]);
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
	// 	ServiceManager.getWorldMap().buildAbort(_job.getItem());
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

	public void  updateNeeds(int count) {
		_needs.update();
	}


	public void  slowUpdate() {
		_old += Constant.CHARACTER_GROW_PER_UPDATE * Constant.SLOW_UPDATE_INTERVAL;
		
		if (_old > Constant.CHARACTER_MAX_OLD) {
			ServiceManager.getCharacterManager().remove(this);
		}
		
		// Leave parent quarters
		if (_old > Constant.CHARACTER_LEAVE_HOME_OLD && _quarter != null && (_quarter.getOwner() != this || _quarter.getOwner() != _mate)) {
			_quarter.removeOccupant(this);
			_quarter = null;
		}
		
		// Find quarter
		if (_quarter == null) {
			RoomManager.getInstance().take(this, Room.Type.QUARTER);
		}
		
		// New child
		if (_nbChild < Constant.CHARACTER_MAX_CHILD && _mate != null && _old > Constant.CHARACTER_CHILD_MIN_OLD && _old < Constant.CHARACTER_CHILD_MAX_OLD && _old > _nextChildAtOld && _nextChildAtOld > 0) {
			_nextChildAtOld = _old + Constant.CHARACTER_DELAY_BETWEEN_CHILDS;
			if (ServiceManager.getRelationManager().addChildren(this, _mate) != null) {
				_nbChild++;
			}
		}
		
		// TODO
		// No energy + no job to sleepingItem -> sleep on the ground
		if (_needs.getEnergy() <= 0 && _needs.isSleeping() == false) {
			if (_job == null || _job.getItem() == null || _job.getItem().isSleepingItem() == false) {
				_needs.sleep(null);
			}
		}
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
			Log.debug("Character #" + _id + ": goto " + _posX + " x " + _posY + ", step: " + _steps);
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
			if (_path != null) {
				Log.info("Character #" + _id + ": reached");
			}
			_steps = 0;
			_path = null;
			_node = null;
		}
	}

	private void actionWork() {
		// Wrong call
		if (_job == null || _job.getItem() == null) {
			Log.error("Character: actionUse on null job or null job's item");
			JobManager.getInstance().abort(_job, Job.Abort.INVALID);
			_job = null;
			return;
		}

		// Item is no longer exists
		if (_job.getItem() != ServiceManager.getWorldMap().getItem(_job.getX(), _job.getY())) {
			Log.warning("Character #" + _id + ": actionUse on invalide item");
			JobManager.getInstance().abort(_job, Job.Abort.INVALID);
			_job = null;
			return;
		}

		// Character is sleeping
		if (_needs.isSleeping()) {
			Log.debug("use: sleeping . use canceled");
			return;
		}

		// Work is complete
		if (_needs.getWorkRemain() <= 0) {
			Log.debug("Character #" + _id + ": work complete");
			JobManager.getInstance().complete(_job);
			_job = null;
		}
		
		// Work continue
		else {
			_needs.setWorkRemain(_needs.getWorkRemain() - 1);
			Log.debug("Character #" + _id + ": working");
		}
		
	}
	
	// TODO: make objects stats table instead switch
	private void actionUse() {
		// Wrong call
		if (_job == null || _job.getItem() == null) {
			Log.error("Character: actionUse on null job or null job's item");
			JobManager.getInstance().abort(_job, Job.Abort.INVALID);
			_job = null;
			return;
		}
		
		// Item not reached
		if (_job.getX() != _posX || _job.getY() != _posY) {
			return;
		}

		// Item is no longer exists
		if (_job.getItem() != ServiceManager.getWorldMap().getItem(_job.getItem().getX(), _job.getItem().getY())) {
			Log.warning("Character #" + _id + ": actionUse on invalide item");
			JobManager.getInstance().abort(_job, Job.Abort.INVALID);
			_job = null;
			return;
		}

		// Character is sleeping
		if (_needs.isSleeping() && _job.getItem().isSleepingItem() == false) {
			Log.debug("use: sleeping . use canceled");
			return;
		}

		Log.debug("Character #" + _id + ": actionUse");

		// Character using item
		if (_job.getDurationLeft() > 0) {
			_job.decreaseDurationLeft();

			BaseItem item = _job.getItem();

			// Item is use by 2 or more character
			if (item.getNbFreeSlots() + 1 < item.getNbSlots()) {
				_needs.addRelation(1);
				List<ItemSlot> slots = item.getSlots();
				for (ItemSlot slot: slots) {
					Character slotCharacter = slot.getJob() != null ? slot.getJob().getCharacter() : null;
					ServiceManager.getRelationManager().date(this, slotCharacter);
				}
			}
			
			// Add item effects
			item.use(this, _job.getDurationLeft());
			return;
		}

		JobManager.getInstance().complete(_job);
		
		_job = null;
	}

	private void		actionStore() {
		UserItem item = ServiceManager.getWorldMap().getItem(_job.getX(), _job.getY());
		if (item != null && item.isStorage()) {
			((StorageItem)item).getItems().addAll(_carry);
			_carry.clear();
			JobManager.getInstance().complete(_job);
			_carry.clear();
		} else {
			Log.error("Character: actionStore on non storage item");
			JobManager.getInstance().abort(_job, Abort.INVALID);
		}
		_job = null;
	}

	private void		actionBuild() {
		// Wrong call
		if (_job == null || _job.getItem() == null) {
			Log.error("Character: actionBuild on null job or null job's item");
			JobManager.getInstance().abort(_job, Abort.INVALID);
			_job = null;
			return;
		}

		// Item is no longer exists
		BaseItem item = _job.getItem();
		StructureItem currentStructure = ServiceManager.getWorldMap().getStructure(_job.getX(), _job.getY());
		BaseItem currentItem = ServiceManager.getWorldMap().getItem(_job.getX(), _job.getY());
		if (item != currentStructure && item != currentItem) {
			if (item != currentStructure) {
				Log.warning("Character #" + _id + ": actionBuild on invalide structure");
			} else if (item != currentItem) {
				Log.warning("Character #" + _id + ": actionBuild on invalide item");
			}
			JobManager.getInstance().abort(_job, Abort.INVALID);
			_job = null;
			return;
		}


		Log.debug("Character #" + _id + ": actionBuild");

		// Build
		ResourceManager.Message result = ResourceManager.getInstance().build(item);

		if (result == ResourceManager.Message.NO_MATTER) {
			UserInterface.getInstance().displayMessage("not enough matter", _job.getX(), _job.getY());
			Log.debug("Character #" + _id + ": not enough matter");
			JobManager.getInstance().abort(_job, Job.Abort.NO_MATTER);
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

	private void		actionGather() {
		// Wrong call
		if (_job == null || _job.getItem() == null) {
			Log.error("Character: actionGather on null job or null job's item");
			Log.warning("Character #" + _id + ": actionBuild on invalide item");
			_job = null;
			return;
		}
		
		BaseItem gatheredItem = _job.getItem();

		if (gatheredItem.getInfo().onGather == null) {
			Log.error("Character: actionGather on non gatherable item");
			Log.warning("Character #" + _id + ": actionBuild on invalide item");
			_job = null;
			return;
		}


		// Character is full: cancel current job
		if (_carry.size() == Constant.CHARACTER_INVENTORY_SPACE) {
			JobManager.getInstance().abort(_job, Job.Abort.NO_LEFT_CARRY);
			_job = JobManager.getInstance().storeItem(_carry.get(0));
			return;
		}

		// TODO
		int value = ServiceManager.getWorldMap().gather(_job.getItem(), getProfessionScore(Profession.Type.NONE));

		Log.debug("gather: " + value);

		ResourceManager.getInstance().addMatter(value);

		if (_job.getItem().getMatterSupply() == 0) {
			JobManager.getInstance().complete(_job);
			_job = null;
		}
		
		_carry.add(new BaseItem(gatheredItem.getInfo().onGather.itemProduce));
		//_carry += value;
	}

	private void		actionMine() {
		// Wrong call
		if (_job == null || _job.getItem() == null) {
			Log.error("Character: actionMine on null job or null job's item");
			JobManager.getInstance().abort(_job, Abort.INVALID);
			_job = null;
			return;
		}
		
		BaseItem gatheredItem = _job.getItem();

		if (gatheredItem.getInfo().onMine == null) {
			Log.error("Character: actionMine on non minable item");
			JobManager.getInstance().abort(_job, Abort.INVALID);
			_job = null;
			return;
		}


		// Character is full: cancel current job
		if (_carry.size() == Constant.CHARACTER_INVENTORY_SPACE) {
			JobManager.getInstance().abort(_job, Job.Abort.NO_LEFT_CARRY);
			_job = JobManager.getInstance().storeItem(_carry.get(0));
			return;
		}

		// TODO
		int value = ServiceManager.getWorldMap().gather(_job.getItem(), getProfessionScore(Profession.Type.NONE));

		Log.debug("mine: " + value);

		ResourceManager.getInstance().addMatter(value);

		if (_job.getItem().getMatterSupply() == 0) {
			JobManager.getInstance().complete(_job);
			_job = null;
		}
		
		_carry.add(new BaseItem(gatheredItem.getInfo().onMine.itemProduce));
	}

	private void		actionDestroy() {
		ResourceManager.getInstance().addMatter(1);
		ServiceManager.getWorldMap().removeItem(_job.getItem());
		JobManager.getInstance().complete(_job);
		_job = null;
	}

	public void			action() {
		if (_job == null) {
			return;
		}
		
		// TODO
		if (_job.getCharacter() != null && _job.getCharacter() != this) {
			_job = null;
			return;
		}
		
		if (_needs.isTired() && (_job.getItem() == null || _job.getItem().isSleepingItem() == false)) {
			JobManager.getInstance().abort(_job, Job.Abort.INTERRUPTE);
			_job = null;
			return;
		}
		
		if (_posX != _toX || _posY != _toY) {
			return;
		}

		JobManager.Action action = _job.getAction();

		switch (action) {
		case MOVE: actionMove(); break;
		case USE: actionUse(); break;
		case GATHER: actionGather(); break;
		case MINING: actionMine(); break;
		case DESTROY: actionDestroy(); break;
		case BUILD: actionBuild(); break;
		case STORE: actionStore(); break;
		case WORK: actionWork(); break;
		case NONE: break;
		}
	}

	private void actionMove() {
		if (_job.getDurationLeft() > 0) {
			_job.decreaseDurationLeft();
			return;
		}

		JobManager.getInstance().complete(_job);
		_job = null;
	}

	public List<CharacterRelation> getFamilyMembers() {
		return _relations;
	}

	public void setParent(Character c1, Character c2) {
		_relations.add(new CharacterRelation(this, c1, Relation.PARENT));
		_relations.add(new CharacterRelation(this, c2, Relation.PARENT));
	}

	public Room getQuarter() {
		return _quarter;
	}

	public void setQuarter(Room quarter) {
		_quarter = quarter;
	}

}
