package org.smallbox.faraway.core.game.model.character.base;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.CharacterTypeInfo;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.model.character.BuffModel;
import org.smallbox.faraway.core.game.model.character.DiseaseModel;
import org.smallbox.faraway.core.game.model.character.TimeTableModel;
import org.smallbox.faraway.core.game.model.item.ConsumableModel;
import org.smallbox.faraway.core.game.model.item.ParcelModel;
import org.smallbox.faraway.core.game.model.job.MoveJob;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;
import org.smallbox.faraway.core.game.model.job.UseJob;
import org.smallbox.faraway.core.game.model.room.RoomModel;
import org.smallbox.faraway.core.game.module.ModuleHelper;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.ui.engine.ViewFactory;
import org.smallbox.faraway.core.ui.engine.views.UILabel;
import org.smallbox.faraway.core.ui.engine.views.View;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class CharacterModel extends MovableModel {

    private GDXDrawable _sleepDrawable = new AnimDrawable("data/res/ic_sleep.png", 0, 0, 32, 32, 6, 10);
    private UILabel _label;

    public GDXDrawable getSleepDrawable() {
        return _sleepDrawable;
    }

    public UILabel getLabelDrawable() {
        if (_label == null) {
            _label = ViewFactory.getInstance().createTextView(_info.getFirstName().trim().length() * 6 + 1, 13);
            _label.setText(_info.getFirstName().trim());
            _label.setTextSize(10);
            _label.setTextColor(Color.YELLOW);
            _label.setBackgroundColor(Color.BLUE);
            _label.setTextAlign(View.Align.CENTER);
        }
        return _label;
    }

    public int getZ() {
        return 0;
    }

    public void setParcel(ParcelModel parcel) {
        this._parcel = parcel;
        _posX = parcel.x;
        _posY = parcel.y;
    }

    public enum TalentType {
        HEAL,
        CRAFT,
        COOK,
        GATHER,
        MINE,
        HAUL,
        BUILD,
        CUT,
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
            new TalentEntry(TalentType.HEAL,     "Heal"),
            new TalentEntry(TalentType.CRAFT,     "Craft"),
            new TalentEntry(TalentType.COOK,     "Cook"),
            new TalentEntry(TalentType.GATHER,     "Gather"),
            new TalentEntry(TalentType.CUT,     "Cut"),
            new TalentEntry(TalentType.MINE,     "Mine"),
            new TalentEntry(TalentType.HAUL,     "Haul"),
            new TalentEntry(TalentType.CLEAN,     "Clean"),
            new TalentEntry(TalentType.BUILD,     "Build")
    };

    private CharacterNeeds                _needs;
    private TimeTableModel              _timeTable;
    protected boolean                    _isSelected;
    protected int                         _lag;
    protected double                     _old;
    protected CharacterInfoModel        _info;
    protected RoomModel                 _quarter;
    protected boolean                     _needRefresh;
    protected ConsumableModel             _inventory;
    protected MoveListener _moveListener;
    //    protected List<ItemInfo>             _equipments;
    protected CharacterStats            _stats;
    protected boolean                     _isFaint;

    private HashMap<TalentType, TalentEntry> _talentsMap;
    private List<TalentEntry>           _talents;
    private ParcelModel                 _toParcel;
    private ParcelModel                 _fromParcel;
    private double                      _moveStep;
    private List<BuffModel>             _buffs;
    public List<DiseaseModel>           _diseases;
    protected CharacterTypeInfo         _type;

    public CharacterModel(int id, int x, int y, String name, String lastName, double old, CharacterTypeInfo type) {
        super(id, x, y);

        Log.info("Character #" + id);

        _type = type;
        _buffs = new ArrayList<>();
        _diseases = new ArrayList<>();
        _timeTable = new TimeTableModel(Game.getInstance().getPlanet().getInfo().dayDuration);
        _old = old;
        _lag = (int)(Math.random() * 10);
        _isSelected = false;
        _blocked = 0;
        _direction = Direction.NONE;
        _steps = 0;
        _info = new CharacterInfoModel(name, lastName);
        _parcel = ModuleHelper.getWorldModule().getParcel(x, y);

        _talentsMap = new HashMap<>();
        _talents = new ArrayList<>();
        for (TalentEntry talent: TALENTS) {
            _talents.add(talent);
            _talentsMap.put(talent.type, talent);
            talent.index = _talents.indexOf(talent);
        }

//        _equipments = new ArrayList<>();
//        _equipments.add(GameData.getData().getEquipment("base.equipments.regular_shirt"));
//        _equipments.add(GameData.getData().getEquipment("base.equipments.regular_pants"));
//        _equipments.add(GameData.getData().getEquipment("base.equipments.regular_shoes"));
//        _equipments.add(GameData.getData().getEquipment("base.equipments.oxygen_bottle"));
//        _equipments.add(GameData.getData().getEquipment("base.equipments.fremen_body"));

        _stats = new CharacterStats();
        _stats.speed = 1;

        _needs = new CharacterNeeds(this, _stats);

        Log.info("Character done: " + _info.getName() + " (" + x + ", " + y + ")");
    }

    public JobModel getJob() { return _job; }
    public CharacterNeeds            getNeeds() { return _needs; }
    public GraphPath<ParcelModel>     getPath() { return _path; }
    public int                         getLag() { return _lag; }
    public double                    getOld() { return _old; }
    public RoomModel                getQuarter() { return _quarter; }
    public void                     setQuarter(RoomModel quarter) { _quarter = quarter; }
    public List<TalentEntry>        getTalents() { return _talents; }
    public TalentEntry              getTalent(TalentType type) { return _talentsMap.get(type); }
    public double                   getBodyHeat() { return _needs.heat; }
    public CharacterStats           getStats() { return _stats; }
    public ParcelModel                 getParcel() { return _parcel; }
    public ConsumableModel          getInventory() { return _inventory; }
    public abstract String[][]      getEquipmentViewIds();
    public abstract String          getEquipmentViewPath();
    public abstract String          getNeedViewPath();
    public CharacterTypeInfo        getType() { return _type; }
    public String                   getTypeName() { return _type.name; }
    public TimeTableModel           getTimetable() { return _timeTable; }

    public abstract String            getName();

    public abstract void            addBodyStats(CharacterStats stats);
    public void                     addDisease(DiseaseModel disease) { _diseases.add(disease); }
    public void                     addBuff(BuffModel buff) { _buffs.add(buff); }
    public void                        setSelected(boolean selected) { _isSelected = selected; }
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
    public double                   getMoveStep() { return _moveStep; }
    public void                     setId(int id) { _id = id; }
    public void                     setOld(int old) { _old = old; }
    public void                     setIsDead() {
        _stats.isAlive = false;
    }

    public boolean                    isSelected() { return _isSelected; }
    public boolean                  isAlive() { return _stats.isAlive; }
    public boolean                     isSleeping() { return _needs.isSleeping(); }
    public boolean                     needRefresh() { return _needRefresh; }

    public void move(GraphPath<ParcelModel> path) {
        move(path, null);
    }

    public void move(GraphPath<ParcelModel> path, MoveListener<CharacterModel> moveListener) {
        if (path != null) {

            if (path.getCount() == 0) {
                if (moveListener != null) {
                    moveListener.onReach(this);
                }
                return;
            }

            ParcelModel toParcel = path.get(path.getCount()-1);

            _blocked = 0;

            _toX = toParcel.x;
            _toY = toParcel.y;
            _path = path;
            _steps = 0;

            _moveListener = moveListener;
        }
    }

    public void moveTo(JobModel job, int toX, int toY, MoveListener moveListener) {
        moveTo(job, ModuleHelper.getWorldModule().getParcel(toX, toY), moveListener);
    }

    public void moveApprox(JobModel job, ParcelModel toParcel, MoveListener moveListener) {
        ParcelModel walkableParcel = null;
        for (int offsetX = 0; offsetX <= 1; offsetX++) {
            for (int offsetY = 0; offsetY <= 1; offsetY++) {
                if (walkableParcel == null) {
                    ParcelModel parcel = WorldHelper.getParcel(toParcel.x + offsetX, toParcel.y + offsetY);
                    if (parcel != null && parcel.isWalkable()) {
                        walkableParcel = parcel;
                    }
                }
            }
        }
        moveTo(job, walkableParcel, moveListener);
    }

    public void moveTo(JobModel job, ParcelModel toParcel, MoveListener<CharacterModel> moveListener) {
        _toX = toParcel.x;
        _toY = toParcel.y;

        _fromParcel = ModuleHelper.getWorldModule().getParcel(_posX, _posY);
        _toParcel = toParcel;

        // Already on position
        if (_posX == _toX && _posY == _toY) {
            if (moveListener != null) {
                moveListener.onReach(this);
            }
        } else {
            _moveListener = moveListener;
            Log.debug("move to: " + _toX + "x" + _toY);
            PathManager.getInstance().getPathAsync(moveListener, this, job, _toX, _toY);
        }
    }

    public void fixPosition() {
        if (_parcel != null && !_parcel.isWalkable()) {
            ParcelModel parcel = WorldHelper.getNearestFreeParcel(_posX, _posY, true, false);
            if (parcel != null) {
                this._parcel = parcel;
                _posX = parcel.x;
                _posY = parcel.y;
            }
        }
    }

    public List<BuffModel> getBuffs() {
        return _buffs;
    }

    public void update() {
        _needs.environment = _parcel.getEnvironmentScore();
        //TODO
//        _needs.light = ((RoomModule) ModuleManager.getInstance().getModule(RoomModule.class)).getLight(_posX, _posY);

        // TODO: create JobSleep class with auto-cancel capability
        // Cancel character sleeping
        int timetable = _timeTable.get(Game.getInstance().getHour());
        if (timetable != 0 && timetable != 1 && _needs.isSleeping && _needs.energy > 75) {
            _needs.isSleeping = false;
            if (_job != null && _job instanceof UseJob && _job.getItem() != null && _job.getItem().isSleepingItem()) {
                ModuleHelper.getJobModule().quitJob(_job);
            }
        }
    }

    public void    setJob(JobModel job) {
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

//    public void    setProfession(ProfessionModel.Type professionId) {
//        ProfessionModel[] professions = Game.getCharacterModule().getProfessions();
//
//        for (ProfessionModel profession : professions) {
//            if (profession.getElevation() == professionId) {
//                Log.debug("setProfession: " + profession.getName());
//                setProfession(profession);
//            }
//        }
//    }

    public void  longUpdate() {
        _old += Constant.CHARACTER_GROW_PER_UPDATE * Constant.SLOW_UPDATE_INTERVAL;

        if (_old > Constant.CHARACTER_MAX_OLD) {
            _stats.isAlive = false;
        }

//        // Find quarter
//        if (_quarter == null) {
//            Game.getRoomManager().take(this, Room.Type.QUARTER);
//        }

        // TODO
        // No energy + no job to sleepingItem -> sleep on the ground
        if (_needs.getEnergy() <= 0 && !_needs.isSleeping()) {
            if (_job == null || _job.getItem() == null || !_job.getItem().isSleepingItem()) {
                _needs.setSleeping(true);
            }
        }
    }

    public void        move() {
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
            int x = _node.x;
            int y = _node.y;
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
            _moveStep = 1;

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

            // TODO: why characters sometimes not reach job location
            if (_posX != _toX || _posY != _toY) {
                setJob(null);
            }

            if (_moveListener != null) {
                MoveListener listener = _moveListener;
                _moveListener = null;
                listener.onReach(this);
            }
        }
    }

    public void            action() {
        if (_job == null) {
            return;
        }

        if (!_job.hasCharacter(this)) {
            _job = null;
            Log.error("Job not owned by this characters");
            return;
        }

        if (getNeeds().isSleeping && (!(_job instanceof UseJob) || _job.getItem() == null || !_job.getItem().isSleepingItem())) {
            return;
        }

        // Check if job location is reached or instance of MoveJob
        if (_parcel == _job.getTargetParcel() || _job instanceof MoveJob) {
            JobModel.JobActionReturn ret = _job.action(this);
            if (ret == JobModel.JobActionReturn.FINISH || ret == JobModel.JobActionReturn.ABORT) {
                ModuleHelper.getJobModule().closeJob(_job);
                ModuleHelper.getJobModule().assign(this);
            }
            if (ret == JobModel.JobActionReturn.QUIT) {
                ModuleHelper.getJobModule().quitJob(_job);
                ModuleHelper.getJobModule().assign(this);
            }
        }
    }

    @Override
    public void    onPathFailed(JobModel job, ParcelModel fromParcel, ParcelModel toParcel) {
        if (_fromParcel == fromParcel && _toParcel == toParcel) {
            Log.warning("Job failed (no path)");

            // Abort job
            ModuleHelper.getJobModule().quitJob(job, JobModel.JobAbortReason.BLOCKED);
            _job = null;

            if (_onPathComplete != null) {
                _onPathComplete.onPathFailed(job);
            }
        }
    }

    @Override
    public void    onPathComplete(GraphPath<ParcelModel> path, JobModel job, ParcelModel fromParcel, ParcelModel toParcel) {
        if (_fromParcel == fromParcel && _toParcel == toParcel) {
            Log.debug("Character #" + _id + ": go(" + _posX + ", " + _posY + " to " + _toX + ", " + _toY + ")");

            if (path.getCount() == 0) {
                return;
            }

            _blocked = 0;

            _toX = toParcel.x;
            _toY = toParcel.y;
            _path = path;
            _steps = 0;

            if (_onPathComplete != null) {
                _onPathComplete.onPathComplete(path, job);
            }
        }
    }

    public void addInventory(ConsumableModel consumable, int quantity) {
        if (_inventory != null && _inventory.getInfo() != consumable.getInfo()) {
            Log.error("Character inventory has non-compatible item");
            return;
        }

        // Create inventory item if empty
        if (_inventory == null) {
            _inventory = new ConsumableModel(consumable.getInfo());
            _inventory.setQuantity(0);
        }

        // Add quantity
        _inventory.addQuantity(quantity);
        consumable.addQuantity(-quantity);
    }

}
