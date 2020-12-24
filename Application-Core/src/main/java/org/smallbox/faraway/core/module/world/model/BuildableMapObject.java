package org.smallbox.faraway.core.module.world.model;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.modules.building.BasicBuildJob;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Map;
import java.util.stream.Collectors;

public class BuildableMapObject extends MapObjectModel {

    private int                 _matter;
    private double              _health;
    private int                 _width;
    private int                 _height;
    private double              _progress;

    /**
     * Fait progresser la construction de l'objet
     *
     * @param value valeur à ajouter
     */
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

    public Map<ItemInfo, BuildableMapObjectComponent> _components;

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
            _components = info.build.components.stream().collect(Collectors.toConcurrentMap(i -> i.component, i -> new BuildableMapObjectComponent(i.quantity, 0)));
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
    public int              getHealth() { return (int) _health; }
    public int              getMaxHealth() { return _info.health; }

    public void setHealth(double health) { _health = health; }
    public void addHealth(double health) { _health = _health + health; }

    public void addProgress(double value) {
        _progress += value;
    }

    private double _buildValue;
    private BasicBuildJob _buildJob;

    public boolean hasAllComponents() {
        for (Map.Entry<ItemInfo, BuildableMapObjectComponent> entry: _components.entrySet()) {
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
    public boolean          isWalkable() { return !isComplete() || _info.isWalkable; }

    @Override
    public boolean          isComplete() { return _buildValue >= _info.build.cost; }

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
