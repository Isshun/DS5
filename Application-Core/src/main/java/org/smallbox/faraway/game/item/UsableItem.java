package org.smallbox.faraway.game.item;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.world.model.BuildableMapObject;
import org.smallbox.faraway.core.world.model.ItemFilter;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.itemFactory.ItemFactoryModel;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UsableItem extends BuildableMapObject {
    private int                             _targetTemperature = 21;
    private boolean                         _isFunctional = true;
    private final boolean                         _isActive = true;
    private int                             _potencyUse;
    private List<ItemSlot>                  _slots;
    private int                             _nbFreeSlot = -1;
    private int                             _nbSlot;
    private ItemFactoryModel                _factory;
    private int[]                           _storageSlot;
    private List<NetworkConnectionModel>    _networkConnections;
    private int glue;

    public UsableItem(ItemInfo info, int id) {
        super(info, id);
    }

    public UsableItem(ItemInfo info) {
        super(info);
    }

    public int getGlue() {
        return glue;
    }

    public void setGlue(int glue) {
        this.glue = glue;
    }

    public int                          getTargetTemperature() { return _targetTemperature; }
    public int                          getValue() { return 15; }
    public int                          getPotencyUse() { return _potencyUse; }
    public List<ItemSlot>               getSlots() { return _slots; }
    public int                          getNbFreeSlots() { return _nbFreeSlot; }
    public int                          getNbSlots() { return _nbSlot; }
    public ItemFactoryModel             getFactory() { return _factory; }
    public List<NetworkConnectionModel> getNetworkConnections() { return _networkConnections; }

    public boolean                      hasFreeSlot() { return _nbFreeSlot == -1 || _nbFreeSlot > 0; }
    public boolean                      isFunctional() { return _isFunctional; }
    public boolean                      isActive() { return _isActive; }
    public boolean                      isBed() { return _info.isBed; }
    public boolean                      isFactory() { return _factory != null; }

    public void                         setTargetTemperature(int targetTemperature) { _targetTemperature = targetTemperature; }
    public void                         setFunctional(boolean isFunctional) { _isFunctional = isFunctional; }
    public void                         setPotencyUse(int potencyUse) { _potencyUse = potencyUse; }

    public void use(CharacterModel character, int durationLeft) {
        // Add buffEffect on characters
//        if (CollectionUtils.isNotEmpty(_info.actions)) {
//            _info.actions.stream()
//                    .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.USE)
//                    .forEach(character::apply);
//        }
    }

    // TODO: this method must only be used by world serializer, createGame pack/unpack method for in-game use
    @Override
    public void setParcel(Parcel parcel) {
        super.setParcel(parcel);
        initSlots();

        if (_networkConnections != null) {
            _networkConnections.forEach(networkConnection -> networkConnection.setParcel(parcel));
        }
    }

    public void init() {
        initSlots();

//        if (_info.actions != null) {
//            _info.actions.stream()
//                    .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.CRAFT)
//                    .forEach(action -> {
//                        if (_factory == null) {
//                            _factory = new ItemFactoryModel(this);
//                        }
//                        _factory.addAction(action);
//                    });
//        }

        // Initialize factory extra object
        if (_info.factory != null) {
            _factory = new ItemFactoryModel(this);
            if (_info.factory.outputSlots != null) {
                _storageSlot = _info.factory.outputSlots;
            }
        }
//
//        // Initialize network extra objects
//        if (_info.networks != null) {
//            _networkConnections = _info.networks.stream().map(networkItemInfo -> new NetworkConnectionModel(networkItemInfo.network, networkItemInfo.distance)).collect(Collectors.toList());
//        }
    }

    /**
     * Initialise les slots depuis l'objet ItemInfo ou crée un slot unique
     * sur l'emplacement de l'objet si ItemInfo ne contient pas de données
     */
    public void initSlots() {
        if (_info.slots != null) {
            _slots = _info.slots.stream()
                    .filter(slot -> WorldHelper.getParcelOffset(_parcel, slot[0], slot[1], 0) != null)
                    .map(slot -> new ItemSlot(this, WorldHelper.getParcelOffset(_parcel, slot[0], slot[1], 0)))
                    .collect(Collectors.toList());
        } else {
            _slots = Collections.singletonList(new ItemSlot(this, _parcel));
        }
        _nbSlot = _nbFreeSlot = _slots.size();
    }

    public ItemSlot takeSlot(JobModel job) {
        if (_slots != null) {
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
        slot.free();
        _nbFreeSlot = (int)_slots.stream().filter(ItemSlot::isFree).count();
    }

    @Override
    public boolean matchFilter(ItemFilter filter) {
        // Filter looking for item
        if (filter.lookingForItem) {

            // Filter isJobNeeded free slots but item is busy
            if (filter.needFreeSlot && !hasFreeSlot()) {
                return false;
            }

            if (!isComplete()) {
                return false;
            }

            // Filter on item
            if (filter.itemNeeded == _info) {
                filter.itemMatched = _info;
                return true;
            }

            if (_info.actions != null) {
                for (ItemInfo.ItemInfoAction action: _info.actions) {
                    if (action.type == ItemInfo.ItemInfoAction.ActionType.USE && _info.matchFilter(action.effects, filter) && canLaunchAction(action)) {
                        filter.itemMatched = _info;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean canLaunchAction(ItemInfo.ItemInfoAction action) {
        if (action.inputs != null) {
            for (ItemInfo.ActionInputInfo inputInfo: action.inputs) {
                // TODO
                // Item isJobNeeded consumable to be used
                if (inputInfo.item != null) {
                    return false;
                }

                // Item isJobNeeded consumable through network connection
                if (inputInfo.network != null) {
                    boolean haveRequirement = false;
                    if (_networkConnections != null) {
                        for (NetworkConnectionModel networkConnection: _networkConnections) {
                            if (networkConnection.getNetwork() != null
                                    && networkConnection.getNetwork().getInfo() == inputInfo.network
                                    && networkConnection.getNetwork().getQuantity() >= inputInfo.quantity) {
                                haveRequirement = true;
                            }
                        }
                    }
                    if (!haveRequirement) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isStorageParcel(Parcel parcel) {
        return _storageSlot != null && _parcel.x + _storageSlot[0] == parcel.x && _parcel.y + _storageSlot[1] == parcel.y;
    }

    public boolean hasFactory() {
        return _factory != null;
    }

    @Override
    public String toString() { return _info + " at " + _parcel; }

}
