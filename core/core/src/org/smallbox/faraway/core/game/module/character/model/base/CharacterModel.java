package org.smallbox.faraway.core.game.module.character.model.base;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.CharacterTypeInfo;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.module.character.model.*;
import org.smallbox.faraway.core.game.module.character.model.TalentExtra.TalentEntry;
import org.smallbox.faraway.core.game.module.job.SleepJob;
import org.smallbox.faraway.core.game.module.job.model.MoveJob;
import org.smallbox.faraway.core.game.module.job.model.UseJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class CharacterModel extends MovableModel {

    private GDXDrawable _sleepDrawable = new AnimDrawable("data/res/ic_sleep.png", 0, 0, 32, 32, 6, 10);
    private UILabel _label;
    private PathModel _path;

    public UILabel getLabelDrawable() {
        if (_label == null) {
            _label = new UILabel(_info.getFirstName().trim().length() * 6 + 1, 13);
            _label.setText(_info.getFirstName().trim());
            _label.setTextSize(10);
            _label.setTextColor(Color.YELLOW);
            _label.setBackgroundColor(Color.BLUE);
            _label.setTextAlign(View.Align.CENTER);
        }
        return _label;
    }

    public void setParcel(ParcelModel parcel) {
        this._parcel = parcel;
    }

    public boolean hasDisease(String name) {
        for (DiseaseCharacterModel disease: _diseases) {
            if (disease.disease.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private CharacterNeeds                      _needs;
    private TimeTableModel                      _timeTable;
    protected boolean                           _isSelected;
    protected int                               _lag;
    protected double                            _old;
    protected CharacterInfoModel                _info;
    protected RoomModel                         _quarter;
    protected boolean                           _needRefresh;
    protected ConsumableModel                   _inventory;
    protected MoveListener                      _moveListener;
    protected CharacterStats                    _stats;
    protected TalentExtra                       _talents;
    protected boolean                           _isFaint;
    private double                              _moveStep;
    private List<BuffModel>                     _buffs;
    public List<DiseaseCharacterModel>          _diseases;
    protected CharacterTypeInfo                 _type;

    public CharacterModel(int id, ParcelModel parcel, String name, String lastName, double old, CharacterTypeInfo type) {
        super(id, parcel);

        Log.info("Character #" + id);

        _type = type;
        _buffs = new ArrayList<>();
        _diseases = new ArrayList<>();
        _timeTable = new TimeTableModel(Game.getInstance().getPlanet().getInfo().dayDuration);
        _old = old;
        _lag = (int)(Math.random() * 10);
        _isSelected = false;
        _direction = Direction.NONE;
        _info = new CharacterInfoModel(name, lastName);

        _talents = new TalentExtra();

//        _equipments = new ArrayList<>();
//        _equipments.add(GameData.getData().getEquipment("base.equipments.regular_shirt"));
//        _equipments.add(GameData.getData().getEquipment("base.equipments.regular_pants"));
//        _equipments.add(GameData.getData().getEquipment("base.equipments.regular_shoes"));
//        _equipments.add(GameData.getData().getEquipment("base.equipments.oxygen_bottle"));
//        _equipments.add(GameData.getData().getEquipment("base.equipments.fremen_body"));

        _stats = new CharacterStats();
        _stats.speed = 1;

        _needs = new CharacterNeeds(this, _stats);

//        Log.info("Character done: " + _info.getName() + " (" + x + ", " + y + ")");
    }

    public JobModel                     getJob() { return _job; }
    public CharacterNeeds               getNeeds() { return _needs; }
    public int                          getLag() { return _lag; }
    public double                       getOld() { return _old; }
    public RoomModel                    getQuarter() { return _quarter; }
    public TalentExtra                  getTalents() { return _talents; }
    public double                       getBodyHeat() { return _needs.heat; }
    public CharacterStats               getStats() { return _stats; }
    public ParcelModel                  getParcel() { return _parcel; }
    public ConsumableModel              getInventory() { return _inventory; }
    public abstract String[][]          getEquipmentViewIds();
    public abstract String              getEquipmentViewPath();
    public abstract String              getNeedViewPath();
    public CharacterTypeInfo            getType() { return _type; }
    public String                       getTypeName() { return _type.name; }
    public TimeTableModel               getTimetable() { return _timeTable; }
    public abstract String              getName();
    public CharacterInfoModel           getInfo() { return _info; }
    public double                       getMoveStep() { return _moveStep; }
    public GDXDrawable                  getSleepDrawable() { return _sleepDrawable; }
    public List<BuffModel>              getBuffs() { return _buffs; }
    public List<DiseaseCharacterModel>  getDiseases() { return _diseases; }

    public abstract void                addBodyStats(CharacterStats stats);
    public void                         addDisease(DiseaseCharacterModel disease) { _diseases.add(disease); }
    public void                         addBuff(BuffModel buff) { _buffs.add(buff); }

    public void                         setSelected(boolean selected) { _isSelected = selected; }
    public void                         setIsFaint() { _isFaint = true; }
    public void                         setInventory(ConsumableModel consumable) { _inventory = consumable; }
    public void                         setQuarter(RoomModel quarter) { _quarter = quarter; }
    public void                         setId(int id) { _id = id; }
    public void                         setOld(int old) { _old = old; }
    public void                         setIsDead() {
        _stats.isAlive = false;
    }

    public boolean                      isSelected() { return _isSelected; }
    public boolean                      isAlive() { return _stats.isAlive; }
    public boolean                      isSleeping() { return _job != null && _job instanceof SleepJob; }
    public boolean                      needRefresh() { return _needRefresh; }


//    public void moveTalent(TalentEntry talent, int offset) {
//        Optional<TalentEntry> optionalEntry = _talents.stream().filter(entry -> entry == talent).findFirst();
//        if (optionalEntry.isPresent()) {
//            int position = _talents.indexOf(optionalEntry.get()) + offset;
//            _talents.remove(optionalEntry.get());
//            _talents.add(Math.min(Math.max(position, 0), _talents.size()), optionalEntry.get());
//        }
//    }

    public DiseaseCharacterModel getDisease(String name) {
        for (DiseaseCharacterModel disease: _diseases) {
            if (disease.disease.name.equals(name)) {
                return disease;
            }
        }
        return null;
    }

    public void move(PathModel path) {
        move(path, null);
    }

    public void move(PathModel path, MoveListener<CharacterModel> listener) {
        if (path != null) {

            if (path.getLength() == 0) {
                if (listener != null) {
                    listener.onReach(this);
                }
                return;
            }

            _path = path;
            _moveListener = listener;
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
        // Already on position
        if (toParcel == _parcel) {
            if (moveListener != null) {
                moveListener.onReach(this);
            }
            return;
        }

        _path = PathManager.getInstance().getPath(_parcel, toParcel);
        _moveListener = moveListener;
    }

    public void fixPosition() {
        if (_parcel != null && !_parcel.isWalkable()) {
            _parcel = WorldHelper.getNearestFreeParcel(_parcel, true, false);
        }
    }

    public void update() {
        _needs.environment = _parcel.getEnvironmentScore();
        //TODO
//        _needs.light = ((RoomModule) ModuleManager.getInstance().getModule(RoomModule.class)).getLight(_posX, _posY);

        // TODO: create JobSleep class with auto-cancel capability
        // Cancel model sleeping
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
        if (_path != null) {
            // Character is sleeping
            if (_needs.isSleeping()) {
                Log.debug("Character #" + _id + ": sleeping . move canceled");
                return;
            }

            // Increase move progress
            _moveStep = 1 * _stats.speed * (_job != null ? _job.getSpeedModifier() : 1);
            _moveProgress += _moveStep;

            // Character has reach next parcel
            if (_moveProgress >= 1 && _path.getCurrentParcel() != null) {
                _moveProgress = 0;
                _parcel = _path.getCurrentParcel();

                // Move continue, set next parcel + direction
                if (_path.next()) {
                    int fromX = _parcel.x;
                    int fromY = _parcel.y;
                    int toX = _path.getCurrentParcel().x;
                    int toY = _path.getCurrentParcel().y;
                    if (toX > fromX && toY > fromY) _direction = Direction.BOTTOM_RIGHT;
                    else if (toX < fromX && toY > fromY) _direction = Direction.BOTTOM_LEFT;
                    else if (toX > fromX && toY < fromY) _direction = Direction.TOP_RIGHT;
                    else if (toX < fromX && toY < fromY) _direction = Direction.TOP_LEFT;
                    else if (toX > fromX) _direction = Direction.RIGHT;
                    else if (toX < fromX) _direction = Direction.LEFT;
                    else if (toY > fromY) _direction = Direction.BOTTOM;
                    else if (toY < fromY) _direction = Direction.TOP;
                    else _direction = Direction.NONE;
                }

                // Move complete, set path to null and call listener
                else {
                    _path = null;

                    if (_moveListener != null) {
                        MoveListener listener = _moveListener;
                        _moveListener = null;
                        listener.onReach(this);
                    }
                }
            }


        }
    }

    public void            action() {
        if (_job == null) {
            return;
        }

//        if (_job.getTargetParcel())

        if (!_job.hasCharacter(this)) {
            _job = null;
            Log.error("Job not owned by this characters");
            return;
        }

        // Check if job location is reached or instance of MoveJob
        if (_parcel == _job.getTargetParcel() || _job.getTargetParcel() == null || _job instanceof MoveJob) {
            JobModel.JobActionReturn ret = _job.action(this);
            if (_job != null) {
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
    }

    @Override
    public void    onPathFailed(JobModel job, ParcelModel fromParcel, ParcelModel toParcel) {
        Log.warning("Job failed (no path)");

        // Abort job
        ModuleHelper.getJobModule().quitJob(job, JobModel.JobAbortReason.BLOCKED);
        _job = null;

        if (_onPathComplete != null) {
            _onPathComplete.onPathFailed(job);
        }
    }

    @Override
    public void    onPathComplete(GraphPath<ParcelModel> path, JobModel job, ParcelModel fromParcel, ParcelModel toParcel) {
//            Log.debug("Character #" + _id + ": go(" + _posX + ", " + _posY + " to " + _toX + ", " + _toY + ")");

        if (_onPathComplete != null) {
            _onPathComplete.onPathComplete(path, job);
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
