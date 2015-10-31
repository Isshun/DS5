package org.smallbox.faraway.core.game.module.world.model.item;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.ItemSlot;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.List;
import java.util.stream.Collectors;

public class ItemModel extends BuildableMapObject {
    private int                     _targetTemperature = 21;
    private boolean                 _isFunctional = true;
    private boolean                 _isActive = true;
    private int                     _potencyUse;
    private List<ItemSlot>          _slots;
    private int                     _nbFreeSlot = -1;
    private int                     _nbSlot;
    private ItemFactoryModel        _factory;
    private int[]                   _storageSlot;


    public ItemModel(ItemInfo info, ParcelModel parcel, int id) {
        super(info, id);
        _parcel = parcel;
        initSlots();
        init(info);
    }

    public ItemModel(ItemInfo info, ParcelModel parcel) {
        super(info);
        _parcel = parcel;
        initSlots();
        init(info);
    }

    public int                  getTargetTemperature() { return _targetTemperature; }
    public int                  getValue() { return 15; }
    public int                  getPotencyUse() { return _potencyUse; }
    public List<ItemSlot>       getSlots() { return _slots; }
    public int                  getNbFreeSlots() { return _nbFreeSlot; }
    public int                  getNbSlots() { return _nbSlot; }
    public ItemFactoryModel     getFactory() { return _factory; }

    public boolean              hasFreeSlot() { return _nbFreeSlot == -1 || _nbFreeSlot > 0; }
    public boolean              isFunctional() { return _isFunctional; }
    public boolean              isActive() { return _isActive; }
    public boolean              isBed() { return _info.isBed; }

    public void                 setTargetTemperature(int targetTemperature) { _targetTemperature = targetTemperature; }
    public void                 setFunctional(boolean isFunctional) { _isFunctional = isFunctional; }
    public void                 setPotencyUse(int potencyUse) { _potencyUse = potencyUse; }

    private void init(ItemInfo info) {
        if (info.factory != null) {
            _factory = new ItemFactoryModel(this, info.factory);
            if (info.factory.outputSlots != null) {
                _storageSlot = info.factory.outputSlots;
            }
        }
    }

    public void initSlots() {
        _nbFreeSlot = -1;

//        if (_info.actions != null) {
//            _slots = new ArrayList<>();
//
//            // Get slot from ItemInfo
//            if (_info.slots != null) {
//                _slots.addAll(_info.slots.stream().map(slot -> new ItemSlot(this, slot[0], slot[1])).collect(Collectors.toList()));
//            }
//
//            // Unique slot at 0x0
//            else {
//                _slots.add(new ItemSlot(this, 0, 0));
//            }
//
//            _nbFreeSlot = _nbSlot = _slots.size();
//        }

        if (_info.slots != null) {
            _slots = _info.slots.stream().map(slot -> new ItemSlot(this, slot[0], slot[1])).collect(Collectors.toList());
        }
    }

    public ItemSlot takeSlot(JobModel job) {
        if (_nbFreeSlot != -1) {
            for (ItemSlot slot : _slots) {
                if (slot.isFree()) {
                    slot.take(job);
                    _nbFreeSlot--;
                    return slot;
                }
            }
        }
        return null;
    }

    public void releaseSlot(ItemSlot slot) {
        if (slot.isFree() == false) {
            slot.free();
        }
        _nbFreeSlot = 0;
        for (ItemSlot s: _slots) {
            if (s.isFree()) {
                _nbFreeSlot++;
            }
        }
    }

    @Override
    public boolean matchFilter(ItemFilter filter) {
        // Filter need free slots but item is busy
        if (filter.needFreeSlot && !hasFreeSlot()) {
            return false;
        }

        return super.matchFilter(filter);
    }

    public boolean isStorageParcel(ParcelModel parcel) {
        return _storageSlot != null && _parcel.x + _storageSlot[0] == parcel.x && _parcel.y + _storageSlot[1] == parcel.y;
    }

    public void freeSlot(JobModel job) {

    }
}
