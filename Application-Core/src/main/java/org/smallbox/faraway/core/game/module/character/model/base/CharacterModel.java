package org.smallbox.faraway.core.game.module.character.model.base;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.CollectionUtils;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.character.model.*;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.MoveJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class CharacterModel extends MovableModel {

    private GDXDrawable _sleepDrawable = new AnimDrawable("data/res/ic_sleep.png", 0, 0, 32, 32, 6, 10);
    private UILabel _label;
    private PathModel _path;
    private PathModel _lastPath;
    private Map<ItemInfo, Integer> _inventory2 = new HashMap<>();

    public UILabel getLabelDrawable() {
        if (_label == null) {
            _label = new UILabel(null);
            _label.setSize(_personals.getFirstName().trim().length() * 6 + 1, 13);
            _label.setText(_personals.getFirstName().trim());
            _label.setTextSize(10);
            _label.setTextColor(Color.YELLOW);
            _label.setBackgroundColor(Color.BLUE);
            _label.setTextAlign(View.Align.CENTER);
        }
        return _label;
    }

    protected CharacterPersonalsExtra           _personals;
    protected CharacterStatsExtra               _stats;
    protected CharacterTalentExtra              _talents;
    protected CharacterNeedsExtra               _needs;

    private TimeTableModel                      _timeTable;
    protected boolean                           _isSelected;
    protected int                               _lag;
    protected RoomModel                         _quarter;
    protected boolean                           _needRefresh;
    protected ConsumableModel                   _inventory;
    protected MoveListener                      _moveListener;
    protected boolean                           _isFaint;
    private double                              _moveStep;
    private Collection<BuffCharacterModel>      _buffs;
    private Collection<CharacterCheck>          _needsCheck;
    public Collection<DiseaseCharacterModel>    _diseases;
    protected CharacterInfo _type;
    private boolean                             _isSleeping;

    public CharacterModel(int id, ParcelModel parcel, String name, String lastName, double old, CharacterInfo type) {
        super(id, parcel);

        Log.info("Character #" + id);

        _type = type;
        _buffs = new ConcurrentLinkedQueue<>();
        _needsCheck = new ConcurrentLinkedQueue<>();
        _diseases = new ArrayList<>();
        _timeTable = new TimeTableModel(Application.gameManager.getGame().getPlanet().getInfo().dayDuration);
        _lag = (int)(Math.random() * 10);
        _isSelected = false;
        _direction = Direction.NONE;
        _personals = new CharacterPersonalsExtra(name, lastName);
        _personals.setOld(old);

        _talents = new CharacterTalentExtra();

//        _equipments = new ArrayList<>();
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.regular_shirt"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.regular_pants"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.regular_shoes"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.oxygen_bottle"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.fremen_body"));

        _stats = new CharacterStatsExtra();
        _stats.speed = 1;

        _needs = new CharacterNeedsExtra(this, _stats);

//        Log.info("Character done: " + _info.getName() + " (" + x + ", " + y + ")");
    }

    public CharacterNeedsExtra          getNeeds() { return _needs; }
    public CharacterTalentExtra         getTalents() { return _talents; }
    public CharacterStatsExtra          getStats() { return _stats; }
    public CharacterPersonalsExtra      getPersonals() { return _personals; }
    public JobModel                     getJob() { return _job; }
    public int                          getLag() { return _lag; }
    public RoomModel                    getQuarter() { return _quarter; }
    public double                       getBodyHeat() { return _needs.heat; }
    public ParcelModel                  getParcel() { return _parcel; }
    public ConsumableModel              getInventory() { return _inventory; }
    public Map<ItemInfo, Integer>       getInventory2() { return _inventory2; }
    public abstract String[][]          getEquipmentViewIds();
    public abstract String              getEquipmentViewPath();
    public abstract String              getNeedViewPath();
    public CharacterInfo                getType() { return _type; }
    public String                       getTypeName() { return _type.name; }
    public TimeTableModel               getTimetable() { return _timeTable; }
    public abstract String              getName();
    public double                       getMoveStep() { return _moveStep; }
    public GDXDrawable                  getSleepDrawable() { return _sleepDrawable; }
    public int                          getInventoryQuantity() { return _inventory != null ? _inventory.getQuantity() : 0; }
    public Collection<BuffCharacterModel>     getBuffs() { return _buffs; }
    public Collection<DiseaseCharacterModel>  getDiseases() { return _diseases; }

    public abstract void                addBodyStats(CharacterStatsExtra stats);
    public void                         addDisease(DiseaseCharacterModel disease) { _diseases.add(disease); }
    public void                         addBuff(BuffCharacterModel buff) { _buffs.add(buff); }

    public void                         setSelected(boolean selected) { _isSelected = selected; }
    public void                         setIsFaint() { _isFaint = true; }
    public void                         setInventory(ConsumableModel consumable) { _inventory = consumable; }
    public void                         setQuarter(RoomModel quarter) { _quarter = quarter; }
    public void                         setId(int id) { _id = id; }
    public void                         setIsDead() {
        _stats.isAlive = false;
    }
    public void                         setParcel(ParcelModel parcel) {
        assert parcel != null;

        _parcel = parcel;
        if (_inventory != null) {
            _inventory.setParcel(parcel);
        }
    }

    public boolean                      isSelected() { return _isSelected; }
    public boolean                      isAlive() { return _stats.isAlive; }
    public boolean                      isSleeping() { return _isSleeping; }
//    public boolean                      isSleeping() { return _job != null && _job instanceof SleepJob && _job.getTargetParcel() == _parcel; }
    public boolean                      needRefresh() { return _needRefresh; }

    public DiseaseCharacterModel getDisease(String name) {
        for (DiseaseCharacterModel disease: _diseases) {
            if (disease.disease.name.equals(name)) {
                return disease;
            }
        }
        return null;
    }

    public boolean hasDisease(String name) {
        for (DiseaseCharacterModel disease: _diseases) {
            if (disease.disease.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDisease(DiseaseInfo diseaseInfo) {
        for (DiseaseCharacterModel disease: _diseases) {
            if (disease.disease == diseaseInfo) {
                return true;
            }
        }
        return false;
    }

    public ParcelModel moveApprox(ParcelModel targetParcel, MoveListener<CharacterModel> listener) {
        PathModel path = Application.pathManager.getPath(_parcel, targetParcel, true, false);

        // No path to target parcel
        if (path == null) {
            if (listener != null) {
                listener.onFail(this);
            }
            return null;
        }

        // Move character to target parcel
        move(path, listener);
        return path.getLastParcel();
    }

    public void move(PathModel path) {
        move(path, null);
    }

    public void move(PathModel path, MoveListener<CharacterModel> listener) {
        if (_moveListener != null) {
            Log.debug("[" + getName() + "] Cancel previous move listener");
            _moveListener.onFail(this);
            _moveListener = null;
        }

        if (path != null) {
            // Already on position
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

    public void moveTo(ParcelModel parcel, MoveListener<CharacterModel> listener) {
        if (_moveListener != null) {
            Log.debug("[" + getName() + "] Cancel previous move listener");
            _moveListener.onFail(this);
            _moveListener = null;
        }

        // Already on position
        if (parcel == _parcel) {
            if (listener != null) {
                listener.onReach(this);
            }
            return;
        }

        _path = Application.pathManager.getPath(_parcel, parcel, false, false);
        if (_path != null) {
            _moveListener = listener;
        } else if (listener != null) {
            listener.onFail(this);
        }
    }

    public void fixPosition() {
        if (_parcel != null && !_parcel.isWalkable()) {
            Log.error(getName() + " is stuck !");
            setParcel(WorldHelper.getNearestWalkable(_parcel, 1, 20));
            if (_path != null) {
                _path = null;
                _moveListener = null;
            }
            if (_job != null) {
                _job.quit(this);
                _job = null;
            }
        }

        if (_job != null && _job.getTargetParcel() != _parcel && _path == null) {
            _job.quit(this);
        }
    }

    public void update() {
        _needs.environment = _parcel.getEnvironmentScore();
        //TODO
//        _needs.light = ((RoomModule) Application.moduleManager.getModule(RoomModule.class)).getLight(_posX, _posY);
    }

    public void    setJob(JobModel job) {
        assert _job == null;
        assert job != null;

        _job = job;
    }

    public void  longUpdate() {
        _personals.setOld(_personals.getOld() + Constant.CHARACTER_GROW_PER_UPDATE * Constant.SLOW_UPDATE_INTERVAL);

        if (_personals.getOld()> Constant.CHARACTER_MAX_OLD) {
            _stats.isAlive = false;
        }

//        // Find quarter
//        if (_quarter == null) {
//            Game.getRoomManager().take(this, Room.Type.QUARTER);
//        }
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
                setParcel(_path.getCurrentParcel());

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

                // Move state, set path to null and call listener
                else {
                    Log.info(getName() + " Move state (" + _path.getFirstParcel().x + "x" + _path.getFirstParcel().y + "x" + _path.getFirstParcel().z + " to " + _path.getLastParcel().x + "x" + _path.getLastParcel().y + "x" + _path.getLastParcel().z + ")");
                    _lastPath = _path;
                    _path = null;

                    if (_moveListener != null) {
                        Log.info(getName() + " Move state: call onReach");
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

        if (!_job.hasCharacter(this)) {
            _job = null;
            Log.error("Job not owned by this characters");
            return;
        }

        // Check if job location is reached or instance of MoveJob
        if (_parcel == _job.getTargetParcel() || _job.getTargetParcel() == null || _job instanceof MoveJob) {
            _job.action(this);
        }
    }

    public void createInventoryFromConsumable(ConsumableModel consumable, int quantity) {
        if (_inventory != null && _inventory.getInfo() != consumable.getInfo()) {
            Log.error("Character inventory has non-compatible item");
            return;
        }

        if (quantity == 0) {
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

    public void setSleeping(boolean isSleeping) {
        _isSleeping = isSleeping;
    }

    public void clearJob(JobModel job) {
        assert _job == job;

        _job = null;
        _moveListener = null;
        _path = null;
    }

    public void cancelMove() {
        _moveListener = null;
    }

    public String toString() {
        return _personals.getFirstName() + " " + _personals.getLastName();
    }

    public void addNeed(CharacterCheck check) {
        if (CollectionUtils.notContains(_needsCheck, check)) {
            _needsCheck.add(check);
        }
    }

    public void removeNeed(CharacterCheck check) {
        _needsCheck.remove(check);
    }

    public boolean hasNeed(CharacterCheck check) {
        return _needsCheck.contains(check);
    }

    public Collection<CharacterCheck> getChecks() {
        return _needsCheck;
    }

    public void apply(ItemInfo.ItemInfoAction action) {
        getNeeds().use(action.effects, action.cost);
    }

    public void apply(ItemInfo.ItemConsumeInfo consume) {
        getNeeds().use(consume.effects, consume.cost);
    }

    public void addInventory(ItemInfo itemInfo, int quantity) {
        int inventoryQuantity = _inventory2.containsKey(itemInfo) ? _inventory2.get(itemInfo) : 0;
        if (inventoryQuantity + quantity > 0) {
            _inventory2.put(itemInfo, inventoryQuantity + quantity);
        } else {
            _inventory2.remove(itemInfo);
        }
    }
}
