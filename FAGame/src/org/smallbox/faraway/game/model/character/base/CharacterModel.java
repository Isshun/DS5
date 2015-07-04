package org.smallbox.faraway.game.model.character.base;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.PathManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.OnMoveListener;
import org.smallbox.faraway.game.model.*;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.model.character.BuffModel;
import org.smallbox.faraway.game.model.character.CharacterRelationModel;
import org.smallbox.faraway.game.model.character.DiseaseModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobMove;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

import java.util.*;

public abstract class CharacterModel extends MovableModel {

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
    protected boolean					_isSelected;
    protected int 						_lag;
    protected double 					_old;
    protected CharacterRelationModel    _relations;
    protected CharacterInfoModel        _info;
    protected RoomModel 				_quarter;
    protected boolean 					_needRefresh;
    protected ConsumableModel 			_inventory;
    protected OnMoveListener 			_moveListener;
    protected List<ItemInfo> 			_equipments;
    protected CharacterStats            _stats;
    protected boolean 					_isFaint;

    private HashMap<TalentType, TalentEntry> _talentsMap;
    private List<TalentEntry>       	_talents;
    private ParcelModel                 _toParcel;
    private ParcelModel                 _fromParcel;
    private double                      _moveStep;
    public List<BuffModel>              _buffs;
    public List<DiseaseModel>           _diseases;
    protected CharacterTypeInfo         _type;

    public CharacterModel(int id, int x, int y, String name, String lastName, double old, CharacterTypeInfo type) {
        super(id, x, y);

        Log.info("Character #" + id);

        _type = type;
        _buffs = new ArrayList<>();
        _diseases = new ArrayList<>();
        _stats = new CharacterStats();
        _stats.speed = 1;
        _old = old;
        _relations = new CharacterRelationModel();
        _lag = (int)(Math.random() * 10);
        _isSelected = false;
        _blocked = 0;
        _direction = Direction.NONE;
        _needs = new CharacterNeeds(this);
        _steps = 0;
        _info = new CharacterInfoModel(name, lastName);
        parcel = Game.getWorldManager().getParcel(x, y);

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

        Log.info("Character done: " + _info.getName() + " (" + x + ", " + y + ")");
    }

    public BaseJobModel             getJob() { return _job; }
    public CharacterNeeds	        getNeeds() { return _needs; }
    public GraphPath<ParcelModel> 	getPath() { return _path; }
    public int 				        getLag() { return _lag; }
    public double			        getOld() { return _old; }
    public RoomModel                getQuarter() { return _quarter; }
    public void                     setQuarter(RoomModel quarter) { _quarter = quarter; }
    public List<TalentEntry>        getTalents() { return _talents; }
    public TalentEntry              getTalent(TalentType type) { return _talentsMap.get(type); }
    public double                   getBodyHeat() { return _stats.bodyHeat; }
    public CharacterStats           getStats() { return _stats; }
    public List<ItemInfo>     		getEquipments() { return _equipments; }
    public ParcelModel 				getParcel() { return parcel; }
    public ConsumableModel          getInventory() { return _inventory; }
    public abstract String[][]      getEquipmentViewIds();
    public abstract String          getEquipmentViewPath();
    public abstract String          getNeedViewPath();
    public CharacterTypeInfo        getType() { return _type; }
    public String                   getTypeName() { return _type.name; }
    public abstract String		    getName();

    public abstract void            addBodyStats(CharacterStats stats);
    public void                     addDisease(DiseaseModel disease) { _diseases.add(disease); }
    public void                     addBuff(BuffModel buff) { _buffs.add(buff); }
    public void				        setSelected(boolean selected) { _isSelected = selected; }
    public void                     setIsFaint() { _isFaint = true; }
    public void                     setInventory(ConsumableModel consumable) { _inventory = consumable; }

    public DiseaseModel getDisease(String name) {
        for (DiseaseModel disease: _diseases) {
            if (disease.name.equals(name)) {
                return disease;
            }
        }
        return null;
    }

    public CharacterInfoModel       getInfo() { return _info; }
    public CharacterRelationModel   getRelations() { return _relations; }
    public double                   getMoveStep() { return _moveStep; }
    public void                     setId(int id) { _id = id; }
    public void                     setOld(int old) { _old = old; }
    public void                     setIsDead() {
        _stats.isAlive = false;
    }

    public boolean			        isSelected() { return _isSelected; }
    public boolean                  isAlive() { return _stats.isAlive; }
    public boolean 			        isSleeping() { return _needs.isSleeping(); }
    public boolean 			        needRefresh() { return _needRefresh; }

    public void moveTo(BaseJobModel job, int toX, int toY, OnMoveListener onMoveListener) {
        moveTo(job, Game.getWorldManager().getParcel(toX, toY), onMoveListener);
    }

    public void moveTo(BaseJobModel job, ParcelModel toParcel, OnMoveListener onMoveListener) {
        fixPosition();

        _toX = toParcel.getX();
        _toY = toParcel.getY();

        _fromParcel = Game.getWorldManager().getParcel(_posX, _posY);
        _toParcel = toParcel;

        // Already on position
        if (_posX == _toX && _posY == _toY) {
            if (onMoveListener != null) {
                onMoveListener.onReach(job, this);
            }
        } else {
            _moveListener = onMoveListener;
            Log.debug("move to: " + _toX + "x" + _toY);
            PathManager.getInstance().getPathAsync(onMoveListener, this, job, _toX, _toY);
        }
    }

    private void fixPosition() {
        if (parcel != null && !parcel.isWalkable()) {
            ParcelModel parcel = Game.getWorldManager().getNearestFreeSpace(_posX, _posY, true, false);
            if (parcel != null) {
                this.parcel = parcel;
                _posX = parcel.getX();
                _posY = parcel.getY();
            }
        }
    }

    public List<BuffModel> getBuffs() {
        return _buffs;
    }

    public void update() {
        _needs.environment = parcel.getEnvironment();
        _needs.light = ((RoomManager)Game.getInstance().getManager(RoomManager.class)).getLight(_posX, _posY);

        // Check room temperature
        _stats.reset(this, _equipments);
        updateBodyHeat(((RoomManager)Game.getInstance().getManager(RoomManager.class)).getRoom(_posX, _posY));
    }

    private void updateBodyHeat(RoomModel room) {
        if (room != null) {
            double minHeat = room.getTemperatureInfo().temperature + _stats.absorb.cold;
            if (minHeat >= Constant.BODY_TEMPERATURE) {
                _stats.bodyHeat = Constant.BODY_TEMPERATURE;
            } else if (minHeat < _stats.bodyHeat) {
                Log.debug("_bodyHeat: " + _stats.bodyHeat + ", (min: " + minHeat + ")");
                _stats.bodyHeat -= 0.1 * (1 - _stats.resist.cold);
            }
        } else {
            Log.debug("_bodyHeat: " + _stats.bodyHeat);
        }
    }

    public ItemInfo getEquipment(String location) {
        for (ItemInfo equipment: _equipments) {
            if (equipment.equipment.location.equals(location)) {
                return equipment;
            }
        }
        return null;
    }

    public void	setJob(BaseJobModel job) {
        // This characters already working on this job
        if (_job == job) {
            Log.warning("This job already exists on characters");
            return;
        }

        // Character has already a job
        if (_job != null && job != null) {
            Log.error("Character already working on other job");
            return;
        }

        // Set new job
        _job = job;
    }

//	public void	setProfession(ProfessionModel.Type professionId) {
//		ProfessionModel[] professions = Game.getCharacterManager().getProfessions();
//
//        for (ProfessionModel profession : professions) {
//            if (profession.getElevation() == professionId) {
//                Log.debug("setProfession: " + profession.getName());
//                setProfession(profession);
//            }
//        }
//	}

    public void  longUpdate() {
        _old += Constant.CHARACTER_GROW_PER_UPDATE * Constant.SLOW_UPDATE_INTERVAL;

        if (_old > Constant.CHARACTER_MAX_OLD) {
            _stats.isAlive = false;
        }

        // Leave parent quarters
        if (_old > Constant.CHARACTER_LEAVE_HOME_OLD && _quarter != null && (_quarter.getOwner() != this || _quarter.getOwner() != _relations.getMate())) {
            _quarter.removeOccupant(this);
            _quarter = null;
        }

//		// Find quarter
//		if (_quarter == null) {
//			Game.getRoomManager().take(this, Room.Type.QUARTER);
//		}

        // New child
        _relations.longUpdate(this);

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

        if (_path == null) {
            return;
        }

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
            _moveStep = 1 * _stats.speed * (_job != null ? _job.getSpeedModifier() : 1);
            _moveProgress += _moveStep;
            if (_moveProgress < 1) {
                return;
            }
            _moveProgress = 0;

            parcel = _node;
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

            if (_moveListener != null) {
                _moveListener.onReach(_job, this);
                _moveListener = null;
            }

            _steps = 0;
            _path = null;
            _node = null;
            _moveProgress = 0;

            // TODO: why characters sometimes not reach job location
            if (_posX != _toX || _posY != _toY) {
                setJob(null);
            }
        }
    }

    public void			action() {
        if (_job == null) {
            return;
        }

        if (!_job.hasCharacter(this)) {
            _job = null;
            Log.error("Job not owned by this characters");
            return;
        }

        if (getNeeds().isSleeping) {
            return;
        }

        // Check if job location is reached or instance of JobMove
        if ((_posX == _toX && _posY == _toY) || _job instanceof JobMove) {
            BaseJobModel.JobActionReturn ret = _job.action(this);
            if (ret == BaseJobModel.JobActionReturn.FINISH || ret == BaseJobModel.JobActionReturn.ABORT) {
                JobManager.getInstance().close(_job);
                JobManager.getInstance().assignJob(this);
            }
            if (ret == BaseJobModel.JobActionReturn.QUIT) {
                JobManager.getInstance().quit(_job);
                JobManager.getInstance().assignJob(this);
            }
        }
    }

    @Override
    public void	onPathFailed(BaseJobModel job, ParcelModel fromParcel, ParcelModel toParcel) {
        if (_fromParcel == fromParcel && _toParcel == toParcel) {
            Log.warning("Job failed (no path)");

            // Abort job
            JobManager.getInstance().quit(job, BaseJobModel.JobAbortReason.BLOCKED);
            _job = null;

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

}
