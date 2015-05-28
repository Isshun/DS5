package org.smallbox.faraway.model.character;

import org.newdawn.slick.util.pathfinding.Path;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.CharacterManager;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.PathManager;
import org.smallbox.faraway.model.Movable;
import org.smallbox.faraway.model.ProfessionModel;
import org.smallbox.faraway.model.character.CharacterRelation.Relation;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.job.Job;
import org.smallbox.faraway.model.room.Room;
import org.smallbox.faraway.ui.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class CharacterModel extends Movable {

	public enum Gender {
		NONE,
		MALE,
		FEMALE,
		BOTH
	}

	private static final Color COLOR_FEMALE = new Color(255, 180, 220);
	private static final Color COLOR_MALE = new Color(110, 200, 255);

	CharacterNeeds					_needs;
	private Gender					_gender;
	private String					_firstName;
	private ProfessionModel _profession;
	private boolean					_isSelected;
	private CharacterStatus 		_status;
	private Color 					_color;
	private int 					_lag;
	private double 					_old;
	private List<UserItem> 			_inventory;
	private int 					_inventorySpace;
	private int 					_inventorySpaceLeft;
	private int 					_nbChild;
	private double 					_nextChildAtOld;
	private List<CharacterRelation> _relations;
	private CharacterModel _mate;
	private boolean 				_isGay;
	private String 					_lastName;
	private String 					_birthName;
	private Room					_quarter;
	private boolean _isDead;

	public CharacterModel(int id, int x, int y, String name, String lastName, double old) {
		super(id, x, y);

		Log.info("Character #" + id);

		_old = old;
		_profession = CharacterManager.professions[id % CharacterManager.professions.length];
		_relations = new ArrayList<>();
		_inventory = new ArrayList<>();
		setGender((int)(Math.random() * 1000) % 2 == 0 ? CharacterModel.Gender.MALE : CharacterModel.Gender.FEMALE);
		_lag = (int)(Math.random() * 10);
		_isSelected = false;
		_blocked = 0;
		_nextChildAtOld = -1;
		_direction = Direction.NONE;
		_needs = new CharacterNeeds(this);
		_status = new CharacterStatus(this);
		_inventorySpace = Constant.CHARACTER_INVENTORY_SPACE;
		_inventorySpaceLeft = _inventorySpace;
		_steps = 0;
		_firstName = name;
		_isGay = (int)(Math.random() * 100) % 10 == 0;
		_lastName = lastName;
		if (name == null) {
//			if ((int)(Math.random() * 1000) % 2 == 0) {
//				_firstName = CharacterName.getShortFirstname(_gender)
//						+ " \"" + CharacterName.getMiddlename() + "\" ";
//				_lastName = lastName != null ? lastName : CharacterName.getShortLastName();
//			} else {
				_firstName = CharacterName.getFirstname(_gender) + " ";
				_lastName = lastName != null ? lastName : CharacterName.getLastName();
//			}
		}
		_birthName = _lastName;

		Log.info("Character done: " + _firstName + _lastName + " (" + x + ", " + y + ")");
	}

	public void				setSelected(boolean selected) { _isSelected = selected; }
	public void				setName(String name) { _firstName = name; }
	public void 			setGender(Gender gender) {
		_gender = gender;
		_color = _gender == Gender.FEMALE ? COLOR_FEMALE : COLOR_MALE;
	}
	public void 			addMateRelation(CharacterModel mate) {
		if (_mate == mate) {
			return;
		}
		
		// Update lastName
		if (_gender == Gender.FEMALE && mate.getGender() == Gender.MALE) {
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
			
			// Restore birtName
			_lastName = _birthName;
			
			// Cancel next child
			_nextChildAtOld = -1;

			_mate.addMateRelation(null);
			_mate = null;
		}
		
		// New mate
		if (mate != null) {
			_mate = mate;
			
			// Add relation
			_relations.add(new CharacterRelation(this, mate, Relation.MATE));
			
			// Schedule next child
			if (_gender == Gender.FEMALE) {
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
	public void 			addFriend(CharacterModel friend) {
		_relations.add(new CharacterRelation(this, friend, Relation.FRIEND));
	}
	public void 			setProfession(ProfessionModel profession) { _profession = profession; }

	public ProfessionModel getProfession() { return _profession; }
	public ProfessionModel.Type	getProfessionId() { return _profession.getType(); }
	public Job				getJob() { return _job; }
	public String			getName() { return _firstName + _lastName; }
	public CharacterNeeds	getNeeds() { return _needs; }
	//	  int[]				getMessages() { return _messages; }
	public boolean			isSelected() { return _isSelected; }
	public int				getProfessionScore(ProfessionModel.Type professionEngineer) { return 42; }
	public List<UserItem> 	getInventory() { return _inventory; }
	public Path 			getPath() { return _path; }
	public CharacterStatus 	getStatus() { return _status; }
	public Color 			getColor() { return _color; }
	public int 				getLag() { return _lag; }
	public int 				getSpace() { return _inventorySpaceLeft; }
	public Gender 			getGender() { return _gender; }
	public CharacterModel getMate() { return _mate; }
	public String 			getLastName() { return _lastName; }
	public List<CharacterRelation> getRelations() { return _relations; }
	public double			getOld() { return _old; }
	public double 			getNextChildAtOld() { return _nextChildAtOld; }

	public boolean			isFull() { return _inventory.size() == Constant.CHARACTER_INVENTORY_SPACE; }
	public boolean 			isSleeping() { return _needs.getSleeping() > 0; }
	public boolean 			isGay() { return _isGay; }

	public void	setJob(Job job) {
		// Cancel previous job
		if (_job != null && _job != job && _job.isFinish() == false) {
			JobManager.getInstance().abort(_job, Job.JobAbortReason.INTERRUPTE);
		}

		// Set new job
		_job = job;

		// Launch new job if not null
		if (job != null) {
			Log.debug("set new job");
			job.setCharacter(this);
			_toX = job.getX();
			_toY = job.getY();
			if (_posX != job.getX() || _posY != job.getY()) {
				PathManager.getInstance().getPathAsync(this, job);
			}
		}
	}

	public void	setProfession(ProfessionModel.Type professionId) {
		ProfessionModel[] professions = Game.getCharacterManager().getProfessions();

		for (int i = 0; i < professions.length; i++) {
			ProfessionModel profession = professions[i];
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

	//   Debug() + "Charactere #" + _id + ": go(" + _x + ", " + _y + " to " + toX + ", " + toY + ")";

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


	public void  longUpdate() {
		_old += Constant.CHARACTER_GROW_PER_UPDATE * Constant.SLOW_UPDATE_INTERVAL;
		
		if (_old > Constant.CHARACTER_MAX_OLD) {
			_isDead = true;
		}
		
		// Leave parent quarters
		if (_old > Constant.CHARACTER_LEAVE_HOME_OLD && _quarter != null && (_quarter.getOwner() != this || _quarter.getOwner() != _mate)) {
			_quarter.removeOccupant(this);
			_quarter = null;
		}
		
		// Find quarter
		if (_quarter == null) {
			Game.getRoomManager().take(this, Room.Type.QUARTER);
		}
		
		// New child
		if (_nbChild < Constant.CHARACTER_MAX_CHILD && _mate != null && _old > Constant.CHARACTER_CHILD_MIN_OLD && _old < Constant.CHARACTER_CHILD_MAX_OLD && _old > _nextChildAtOld && _nextChildAtOld > 0) {
			_nextChildAtOld = _old + Constant.CHARACTER_DELAY_BETWEEN_CHILDS;
			if (Game.getRelationManager().createChildren(this, _mate) != null) {
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
		
		// Refresh status
		_status.refreshThoughts();
	}

	public boolean isMoving() {
		return _node != null;
	}

	public void		move() {
		_move = Direction.NONE;
		
		// Character is sleeping
		if (_needs.isSleeping()) {
			Log.debug("Character #" + _id + ": sleeping . move canceled");
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

			_posX = (int) x;
			_posY = (int) y;
			_steps++;
			Log.debug("Character #" + _id + ": goto " + _posX + " x " + _posY + ", step: " + _steps);
		}

		// Next node
		if (_path != null && (int)_path.getLength() > _steps) {
			Log.debug("Character #" + _id + ": move");

			_node = _path.getStep(_steps);

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
				Log.debug("Character #" + _id + ": reached");
				_steps = 0;
				_path = null;
				_node = null;
				
				// TODO: why character sometimes not reach job location
				if (_posX != _toX || _posY != _toY) {
					setJob(null);
				}
			}
		}
	}
	
	public void removeInventory(UserItem item) {
		if (item != null && _inventory.remove(item)) {
			_inventorySpaceLeft++;
		}
	}

	public void			action() {
		if (_posX != _toX || _posY != _toY) {
			return;
		}

		if (_job == null) {
			return;
		}
		
		if (_job.getCharacter() != null && _job.getCharacter() != this) {
			_job = null;
			return;
		}
		
		if (_needs.isTired() && (_job.getItem() == null || _job.getItem().isSleepingItem() == false)) {
			JobManager.getInstance().abort(_job, Job.JobAbortReason.INTERRUPTE);
			_job = null;
			return;
		}

		if (_job.action(this)) {
			// If job is complete, get new one
			JobManager.getInstance().assignJob(this);
		}
	}

	public List<CharacterRelation> getFamilyMembers() {
		return _relations;
	}

	public Room getQuarter() {
		return _quarter;
	}

	public void setQuarter(Room quarter) {
		_quarter = quarter;
	}

	public void addInventory(UserItem item) {
		if (item != null) {
			_inventory.add(item);
			_inventorySpaceLeft--;
		}
	}

	public ItemBase find(ItemFilter filter) {
		for (ItemBase item: _inventory) {
			if (item.matchFilter(filter)) {
				return item;
			}
		}
		return null;
	}

	public String getEnlisted() {
		return "april 25";
	}

	public String getBirthName() {
		return _birthName;
	}

	public String getFirstName() {
		return _firstName;
	}

	public int getNbRelations() {
		return _relations.size();
	}

	public int getInventoryLeftSpace() {
		return Math.max(_inventorySpaceLeft, 0);
	}

	public int getInventorySpace() {
		return _inventorySpace;
	}

	public boolean hasInventorySpaceLeft() {
		return _inventorySpaceLeft > 0;
	}

	public void clearInventory() {
		_inventory.clear();
		_inventorySpaceLeft = _inventorySpace;
	}

	public void removeInventory(List<UserItem> items) {
		_inventory.removeAll(items);
		_inventorySpaceLeft = _inventorySpace - _inventory.size();
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
		UserInterface.getInstance().displayMessage("blocked", _posX, _posY);
	
		// Abort job
		JobManager.getInstance().abort(job, Job.JobAbortReason.BLOCKED);
		_job = null;

		if (_onPathComplete != null) {
			_onPathComplete.onPathFailed(job);
		}
	}
	
	@Override
	public void	onPathComplete(Path rawpath, Job job) {
	  Log.debug("Charactere #" + _id + ": go(" + _posX + ", " + _posY + " to " + _toX + ", " + _toY + ")");

	  if (rawpath.getLength() == 0) {
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
	
	  _path = rawpath;
	  _steps = 0;
	  
	  if (_onPathComplete != null) {
		  _onPathComplete.onPathComplete(rawpath, job);
	  }
	}

	public void setFirstname(String firstName) {
		_firstName = firstName + " ";
	}

	public int getLeftSpace() {
		return _inventorySpaceLeft;
	}

	public boolean isDead() {
		return _isDead;
	}

	public void setIsDead() {
		_isDead = true;
	}

	public void setNextChildAtOld(double nextChildAtOld) {
		_nextChildAtOld = nextChildAtOld;
	}

	public int getNbChild() {
		return _nbChild;		
	}

	public void setNbChild(int nbChild) {
		_nbChild = nbChild;
	}
	
}
