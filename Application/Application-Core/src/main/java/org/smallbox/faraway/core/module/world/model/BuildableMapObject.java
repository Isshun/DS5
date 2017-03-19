package org.smallbox.faraway.core.module.world.model;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.modules.building.BasicBuildJob;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alex on 14/07/2015.
 */
public class BuildableMapObject extends MapObjectModel {

    private int                 _matter;
    private int                 _health;
    private int                 _width;
    private int                 _height;
    private double              _progress;

    public void actionBuild(double value) {
        if (_buildValue < _info.build.cost) {
            _buildValue += value;

            if (_buildValue >= _info.build.cost) {
                _info.build.components.forEach(componentInfo -> removeInventory(componentInfo.component, componentInfo.quantity));
                _buildJob = null;
            }
        }
    }

    public static class BuildableMapObjectComponent {
        public int neededQuantity;
        public int availableQuantity;

        public BuildableMapObjectComponent(int neededQuantity, int availableQuantity) {
            this.neededQuantity = neededQuantity;
            this.availableQuantity = availableQuantity;
        }
    }

    public Map<ItemInfo, BuildableMapObjectComponent> components;

    public BuildableMapObject(ItemInfo info) {
        super(info);
    }

    public BuildableMapObject(ItemInfo info, int id) {
        super(info, id);
    }

    @Override
    protected void init(ItemInfo info) {
        super.init(info);

        _health = info.health;

        if (info.build != null && info.build.components != null) {
            components = info.build.components.stream().collect(Collectors.toConcurrentMap(i -> i.component, i -> new BuildableMapObjectComponent(i.quantity, 0)));
        }

        // Default values
        _width = 1;
        _height = 1;
        _matter = 1;

        _width = info.width;
        _height = info.height;
    }

    public int              getWidth() { return _width; }
    public int              getHeight() { return _height; }
    public int              getHealth() { return _health; }
    public int              getMaxHealth() { return _info.health; }

    public void setHealth(int health) {
        _health = health;
    }

    public void addHealth(int health) {
        _health = _health + health;
    }

    public void addProgress(double value) {
        _progress += value;
    }

//    public static class ComponentModel extends ObjectModel {
//        public ItemInfo         info;
//        public int              currentQuantity;
//        public int              neededQuantity;
//        public JobModel         job;
//
//        public ComponentModel(ItemInfo itemInfo) {
//            this.info = itemInfo;
//        }
//
//        public ComponentModel(ItemInfo itemInfo, int neededQuantity) {
//            this.info = itemInfo;
//            this.neededQuantity = neededQuantity;
//        }
//
//        public ComponentModel(ItemInfo itemInfo, int neededQuantity, int currentQuantity) {
//            this.info = itemInfo;
//            this.neededQuantity = neededQuantity;
//            this.currentQuantity = currentQuantity;
//        }
//    }

    private double _buildValue;
    private BasicBuildJob _buildJob;
//    private List<ComponentModel>        _components = new ArrayList<>();

//    public int addComponent(ConsumableItem consumable) {
//        _components.stream().filter(component -> component.info == consumable.getInfo()).forEach(component -> {
//            if (component.neededQuantity - component.currentQuantity > consumable.getFreeQuantity()) {
//                component.currentQuantity += consumable.getFreeQuantity();
//                consumable.setQuantity(0);
//            } else {
//                consumable.setQuantity(consumable.getFreeQuantity() - (component.neededQuantity - component.currentQuantity));
//                component.currentQuantity = component.neededQuantity;
//            }
//        });
//
//        return consumable.getFreeQuantity();
//    }

    public boolean hasAllComponents() {
        for (Map.Entry<ItemInfo, BuildableMapObjectComponent> entry: components.entrySet()) {
            if (getInventoryQuantity(entry.getKey()) < entry.getValue().neededQuantity) {
                return false;
            }
        }
        return true;
    }

    public void             setBuildJob(BasicBuildJob job) { _buildJob = job; }
    public void             setBuildProgress(double buildProgress) { _buildValue = Math.min(buildProgress, _info.build.cost); }

    public double           getBuildProgress() { return _buildValue / _info.build.cost; }
    public double           getBuildValue() { return _buildValue; }
    public double           getBuildCost() { return _info.build.cost; }
    public BasicBuildJob    getBuildJob() { return _buildJob; }
    public CharacterModel   getBuilder() { return _buildJob != null ? _buildJob.getCharacter() : null; }

    @Override
    public boolean          isWalkable() { return !isBuildComplete() || _info.isWalkable; }
    public boolean          isBuildComplete() { return _buildValue >= _info.build.cost; }

    public void setReceipt(ReceiptGroupInfo.ReceiptInfo receipt) {
        throw new NotImplementedException("");

//        // Drop all existing components on the floor
//        _components.stream().filter(component -> component.currentQuantity > 0)
//                .forEach(component -> ModuleHelper.getWorldModule().putConsumable(_parcel, component.info, component.currentQuantity));
//
//        // Set new receipt
//        _receipt = receipt;
//        _components = receipt.components.stream().map(componentInfo -> new ComponentModel(componentInfo.item, componentInfo.quantity)).collect(Collectors.toList());
    }

}
