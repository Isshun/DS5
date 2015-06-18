package org.smallbox.faraway.game.model.character.base;

import org.newdawn.slick.util.pathfinding.Path;
import org.smallbox.faraway.PathHelper;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.OnMoveListener;
import org.smallbox.faraway.game.manager.CharacterManager;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.*;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.JobModel;
import org.smallbox.faraway.game.model.job.JobMove;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CharacterModel extends Movable {
	public enum TalentType {
        HEAL,
        CRAFT,
        COOK,
        GATHER,
        MINE,
        HAUL,
        BUILD,
		CLEAN
    }

    public enum Gender {
        NONE,
        MALE,
        FEMALE,
    }

    public static class TalentEntry {
		public final String         name;
        public final TalentType     type;
        public int                  index;
        public double               level;
        public double               learnCoef;

		public TalentEntry(TalentType type, String name) {
            this.type = type;
			this.name = name;
            this.level = 1;
            this.learnCoef = 1;
		}

        public double work() {
            this.level = Math.min(10, this.level + 0.5 * this.learnCoef);
            return this.level / 2;
        }
    }

	private static final Color COLOR_FEMALE = new Color(255, 180, 220);
	private static final Color COLOR_MALE = new Color(110, 200, 255);

	private static final TalentEntry[] TALENTS = new TalentEntry[] {
			new TalentEntry(TalentType.HEAL, 	"Heal"),
			new TalentEntry(TalentType.CRAFT, 	"Craft"),
			new TalentEntry(TalentType.COOK, 	"Cook"),
			new TalentEntry(TalentType.GATHER, 	"Gather"),
			new TalentEntry(TalentType.MINE, 	"Mine"),
			new TalentEntry(TalentType.HAUL, 	"Haul"),
			new TalentEntry(TalentType.CLEAN, 	"Clean"),
			new TalentEntry(TalentType.BUILD, 	"Build")
	};

	CharacterNeeds						_needs;
    protected Gender					_gender;
	protected String					_firstName;
    protected ProfessionModel         	_profession;
    protected boolean					_isSelected;
    protected Color 					_color;
    protected int 						_lag;
    protected double 					_old;
    protected int 						_inventorySpace;
    protected int 						_inventorySpaceLeft;
    protected int 						_nbChild;
    protected double 					_nextChildAtOld;
    protected List<CharacterRelation> 	_relations;
    protected CharacterModel 			_mate;
    protected boolean 					_isGay;
    protected String 					_lastName;
    protected String 					_birthName;
    protected RoomModel 				_quarter;
    protected boolean 					_isDead;
    protected boolean 					_needRefresh;
    protected ConsumableModel 			_inventory;
    protected OnMoveListener 			_moveListener;
    protected List<CharacterBuffModel> 	_buffs;
    protected List<EquipmentModel> 		_equipments;
    protected double 					_bodyHeat = Constant.BODY_TEMPERATURE;
    protected CharacterStats            _stats;
    protected boolean 					_isFaint;
    protected double                    _moveProgress;

	private Map<TalentType, TalentEntry> _talentsMap;
	private List<TalentEntry>       	_talents;

	public CharacterModel(int id, int x, int y, String name, String lastName, double old) {
		super(id, x, y);

		Log.info("Character #" + id);

        _stats = new CharacterStats();
		_old = old;
		_buffs = GameData.getData().buffs.stream().map(CharacterBuffModel::new).collect(Collectors.toList());
        sortBuffs();
		_profession = CharacterManager.professions[id % CharacterManager.professions.length];
		_relations = new ArrayList<>();
		setGender((int)(Math.random() * 1000) % 2 == 0 ? CharacterModel.Gender.MALE : CharacterModel.Gender.FEMALE);
		_lag = (int)(Math.random() * 10);
		_isSelected = false;
		_blocked = 0;
		_nextChildAtOld = -1;
		_direction = Direction.NONE;
		_needs = new CharacterNeeds(this);
		_inventorySpace = Constant.CHARACTER_INVENTORY_SPACE;
		_inventorySpaceLeft = _inventorySpace;
		_steps = 0;
		_firstName = name;
		_isGay = (int)(Math.random() * 100) % 10 == 0;
		_lastName = lastName;
		if (name == null) {
            _firstName = CharacterName.getFirstname(_gender) + " ";
            _lastName = lastName != null ? lastName : CharacterName.getLastName();
		}
		_birthName = _lastName;

        _talentsMap = new HashMap<>();
		_talents = new ArrayList<>();
		for (TalentEntry talent: TALENTS) {
			_talents.add(talent);
            _talentsMap.put(talent.type, talent);
			talent.index = _talents.indexOf(talent);
		}

		_equipments = new ArrayList<>();
		_equipments.add(GameData.getData().getEquipment("base.equipments.regular_shirt"));
		_equipments.add(GameData.getData().getEquipment("base.equipments.regular_pants"));
		_equipments.add(GameData.getData().getEquipment("base.equipments.regular_shoes"));
		_equipments.add(GameData.getData().getEquipment("base.equipments.oxygen_bottle"));

		Log.info("Character done: " + _firstName + _lastName + " (" + x + ", " + y + ")");
	}

    public ProfessionModel          getProfession() { return _profession; }
    public ProfessionModel.Type	    getProfessionId() { return _profession.getType(); }
    public JobModel                 getJob() { return _job; }
    public String			        getName() { return _firstName + _lastName; }
    public CharacterNeeds	        getNeeds() { return _needs; }
    public Path 			        getPath() { return _path; }
    public Color 			        getColor() { return _color; }
    public int 				        getLag() { return _lag; }
    public int 				        getSpace() { return _inventorySpaceLeft; }
    public Gender 			        getGender() { return _gender; }
    public CharacterModel           getMate() { return _mate; }
    public String 			        getLastName() { return _lastName; }
    public List<CharacterRelation>  getRelations() { return _relations; }
    public double			        getOld() { return _old; }
    public double 			        getNextChildAtOld() { return _nextChildAtOld; }
    public double                   getMoveProgress() { return _moveProgress; }
    public RoomModel                getQuarter() { return _quarter; }
    public void                     setQuarter(RoomModel quarter) { _quarter = quarter; }
    public String                   getEnlisted() { return "april 25"; }
    public String                   getBirthName() { return _birthName; }
    public String                   getFirstName() { return _firstName; }
    public int                      getNbRelations() { return _relations.size(); }
    public int                      getInventoryLeftSpace() { return Math.max(_inventorySpaceLeft, 0); }
    public int                      getInventorySpace() { return _inventorySpace; }
    public int                      getNbChild() { return _nbChild; }
    public List<TalentEntry>        getTalents() { return _talents; }
    public TalentEntry              getTalent(TalentType type) { return _talentsMap.get(type); }
    public double                   getBodyHeat() { return _bodyHeat; }
    public CharacterStats           getStats() { return _stats; }
    public List<EquipmentModel>     getEquipments() { return _equipments; }
    public ParcelModel 				getParcel() { return Game.getWorldManager().getParcel(_posX, _posY); }
    public ConsumableModel          getInventory() { return _inventory; }
    public abstract String[][]      getEquipmentViewIds();
    public abstract String          getEquipmentViewPath();
    public abstract String          getNeedViewPath();
    public abstract String          getTypeName();
    public abstract GameConfig.EffectValues getNeedEffects();

	public void 					setNextChildAtOld(double nextChildAtOld) { _nextChildAtOld = nextChildAtOld; }
    public void				        setSelected(boolean selected) { _isSelected = selected; }
    public void				        setName(String name) { _firstName = name; }
    public void 			        setProfession(ProfessionModel profession) { _profession = profession; }
    public void                     setFirstName(String firstName) { _firstName = firstName + " "; }
    public void                     setIsDead() { _isDead = true; }
    public void                     setIsFaint() { _isFaint = true; }
    public void                     setNbChild(int nbChild) { _nbChild = nbChild; }
    public void                     setInventory(ConsumableModel consumable) { _inventory = consumable; }
//    public void 			        addFriend(CharacterModel friend) { _relations.add(new CharacterRelation(this, friend, CharacterRelation.Relation.FRIEND)); }
    public abstract void            addBodyStats(CharacterStats stats);

    public boolean                  hasInventorySpaceLeft() { return _inventorySpaceLeft > 0; }
    public boolean			        isSelected() { return _isSelected; }
    public boolean                  isDead() { return _isDead; }
//    	public boolean			    isFull() { return _inventory.size() == Constant.CHARACTER_INVENTORY_SPACE; }
    public boolean 			        isSleeping() { return _needs.isSleeping(); }
    public boolean 			        isGay() { return _isGay; }
//    public boolean                  isMoving() { return _node != null; }
    public boolean 			        needRefresh() { return _needRefresh; }

    public void moveTo(JobModel job, int toX, int toY, OnMoveListener onMoveListener) {
        _toX = toX;
        _toY = toY;
        _job = job;
        _moveListener = onMoveListener;
        if (_posX != toX || _posY != toY) {
            Log.debug("move to: " + toX + "x" + toY);
            PathHelper.getInstance().getPathAsync(onMoveListener, this, job, toX, toY);
        } else {
            if (onMoveListener != null) {
                onMoveListener.onReach(job, this);
            }
        }
    }

    private CharacterBuffModel getBuff(String buffName) {
        for (CharacterBuffModel characterBuff: _buffs) {
            if (characterBuff.buff.name.equals(buffName)) {
                return characterBuff;
            }
        }
        return null;
    }

    public List<CharacterBuffModel> getBuffs() {
        return _buffs;
    }

    public void update(int tick) {
        if (tick % 10 == 0) {
			// Check buffs
			BuffManager.checkBuffs(this);
            BuffManager.applyBuffs(this);

            // Check room temperature
			_stats.reset(this, _equipments);
            updateBodyHeat(Game.getRoomManager().getRoom(_posX, _posY));
        }
    }

    private void updateBodyHeat(RoomModel room) {
        if (room != null) {
            double minHeat = room.getTemperatureInfo().temperature + _stats.absorb.cold;
            if (minHeat >= Constant.BODY_TEMPERATURE) {
                _bodyHeat = Constant.BODY_TEMPERATURE;
            } else if (minHeat < _bodyHeat) {
                Log.debug("_bodyHeat: " + _bodyHeat + ", (min: " + minHeat + ")");
                _bodyHeat -= 0.1 * (1 - _stats.resist.cold);
            }
        } else {
            Log.debug("_bodyHeat: " + _bodyHeat);
        }
    }

    public EquipmentModel getEquipment(String location) {
        for (EquipmentModel equipment: _equipments) {
            if (equipment.location.equals(location)) {
                return equipment;
            }
        }
        return null;
    }

    private void sortBuffs() {
        Collections.sort(_buffs, (b1, b2) -> {
            if (b2.level == null) return -1;
            if (b1.level == null) return 1;
            return b2.level.effects.mood - b1.level.effects.mood;
        });
    }

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
				if (relation.getRelation() == CharacterRelation.Relation.MATE) {
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
			_relations.add(new CharacterRelation(this, mate, CharacterRelation.Relation.MATE));

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


    public void	setJob(JobModel job) {
		// Cancel previous job
		if (_job != job && _job != null && !_job.isFinish()) {
			JobManager.getInstance().quit(_job, JobModel.JobAbortReason.INTERRUPT);
		}

		// Launch new job if not null
		if (job != null && (_toX != job.getX() || _toY != job.getY())) {
			job.setCharacter(this);
			moveTo(job, job.getX(), job.getY(), new OnMoveListener() {
				@Override
				public void onReach(JobModel job, CharacterModel character) {
				}

				@Override
				public void onFail(JobModel job, CharacterModel character) {
				}
			});
		}

		// Set new job
		_job = job;
	}

//	public void	setProfession(ProfessionModel.Type professionId) {
//		ProfessionModel[] professions = Game.getCharacterManager().getProfessions();
//
//        for (ProfessionModel profession : professions) {
//            if (profession.getType() == professionId) {
//                Log.debug("setProfession: " + profession.getName());
//                setProfession(profession);
//            }
//        }
//	}

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

//		// Find quarter
//		if (_quarter == null) {
//			Game.getRoomManager().take(this, Room.Type.QUARTER);
//		}

		// New child
		if (_nbChild < Constant.CHARACTER_MAX_CHILD && _mate != null && _old > Constant.CHARACTER_CHILD_MIN_OLD && _old < Constant.CHARACTER_CHILD_MAX_OLD && _old > _nextChildAtOld && _nextChildAtOld > 0) {
			_nextChildAtOld = _old + Constant.CHARACTER_DELAY_BETWEEN_CHILDS;
			if (Game.getRelationManager().createChildren(this, _mate) != null) {
				_nbChild++;
			}
		}

		// TODO
		// No energy + no job to sleepingItem -> sleep on the ground
		if (_needs.getEnergy() <= 0 && !_needs.isSleeping()) {
			if (_job == null || _job.getItem() == null || !_job.getItem().isSleepingItem()) {
				_needs.setSleeping(true);
			}
		}
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

            // Increase move progress
            _moveProgress += 1 * (_job != null ? _job.getSpeedModifier() : 1);
            if (_moveProgress < 1) {
                return;
            }
            _moveProgress = 0;

            _posX = x;
			_posY = y;
			_steps++;
			Log.debug("Character #" + _id + ": goto " + _posX + " x " + _posY + ", step: " + _steps);
		}

		// Next node
		if (_path != null && _path.getLength() > _steps) {
			Log.debug("Character #" + _id + ": move");

			_node = _path.getStep(_steps);

		} else {
			if (_path != null) {
				Log.debug("Character #" + _id + ": reached");

				if (_moveListener != null) {
					_moveListener.onReach(_job, this);
				}

				_steps = 0;
				_path = null;
				_node = null;
                _moveProgress = 0;

				// TODO: why character sometimes not reach job location
				if (_posX != _toX || _posY != _toY) {
					setJob(null);
				}
			}
		}
	}

	public void			action() {
		if (_job == null) {
			return;
		}

		if (_job.getCharacter() != null && _job.getCharacter() != this) {
			_job = null;
			Log.error("Job not owned by this character");
			return;
		}

		// If JobMove, call action during moving
		if (_job instanceof JobMove) {
			// If job is close, getRoom new one
			if (_job.action(this)) {
				JobManager.getInstance().assignJob(this);
			}
			return;
		}

		if (_posX != _toX || _posY != _toY) {
			return;
		}

		// If job is close, get new one
		if (_job.action(this)) {
			JobManager.getInstance().assignJob(this);
		}
	}

	@Override
	public void	onPathFailed(JobModel job) {
		Log.warning("Job failed (no path)");
		UserInterface.getInstance().displayMessage("blocked", _posX, _posY);

		// Abort job
		JobManager.getInstance().quit(job, JobModel.JobAbortReason.BLOCKED);
		_job = null;

		if (_onPathComplete != null) {
			_onPathComplete.onPathFailed(job);
		}
	}

	@Override
	public void	onPathComplete(Path rawPath, JobModel job) {
	  Log.debug("Character #" + _id + ": go(" + _posX + ", " + _posY + " to " + _toX + ", " + _toY + ")");

	  if (rawPath.getLength() == 0) {
		return;
	  }

	  _blocked = 0;

	  _toX = job.getX();
	  _toY = job.getY();
	  _path = rawPath;
	  _steps = 0;

	  if (_onPathComplete != null) {
		  _onPathComplete.onPathComplete(rawPath, job);
	  }
	}

	public void movePriority(TalentEntry priority, int index) {
		_talents.get(index).index = priority.index;
		priority.index = index;
		_talents.remove(priority);
		_talents.add(index, priority);
		_needRefresh = true;
	}
}
