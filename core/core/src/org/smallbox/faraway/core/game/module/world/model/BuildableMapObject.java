package org.smallbox.faraway.core.game.module.world.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.BuildJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 14/07/2015.
 */
public class BuildableMapObject extends MapObjectModel {
    public static class ComponentModel extends ObjectModel {
        public ItemInfo         info;
        public int              currentQuantity;
        public int              neededQuantity;
        public JobModel         job;

        public ComponentModel(ItemInfo itemInfo) {
            this.info = itemInfo;
        }

        public ComponentModel(ItemInfo itemInfo, int neededQuantity) {
            this.info = itemInfo;
            this.neededQuantity = neededQuantity;
        }

        public ComponentModel(ItemInfo itemInfo, int neededQuantity, int currentQuantity) {
            this.info = itemInfo;
            this.neededQuantity = neededQuantity;
            this.currentQuantity = currentQuantity;
        }
    }

    protected boolean                   _isComplete = false;
    private ItemInfo.ItemInfoReceipt    _receipt;
    private int                         _totalBuild;
    private int                         _currentBuild;
    private BuildJob                    _buildJob;
    private List<ComponentModel>        _components = new ArrayList<>();

    public BuildableMapObject(ItemInfo info, int id) {
        super(info, id);
        init(info);
    }

    public BuildableMapObject(ItemInfo info) {
        super(info);
        init(info);
    }

    private void init(ItemInfo info) {
        _totalBuild = info.build != null ? info.build.cost : Data.config.defaultBuildCost;
    }

    @Override
    public int addComponent(ConsumableModel consumable) {
        _components.stream().filter(component -> component.info == consumable.getInfo()).forEach(component -> {
            if (component.neededQuantity - component.currentQuantity > consumable.getQuantity()) {
                component.currentQuantity += consumable.getQuantity();
                consumable.setQuantity(0);
            } else {
                consumable.setQuantity(consumable.getQuantity() - (component.neededQuantity - component.currentQuantity));
                component.currentQuantity = component.neededQuantity;
            }
        });

        return consumable.getQuantity();
    }

    public boolean hasAllComponents() {
        for (ComponentModel component: _components) {
            if (component.currentQuantity < component.neededQuantity) {
                return false;
            }
        }
        return true;
    }

    public boolean          build() {
        _currentBuild++;
        if (!_isComplete && _currentBuild >= _totalBuild) {
            _isComplete = true;
            if (this instanceof ItemModel) {
                Game.getInstance().notify(observer -> observer.onItemComplete((ItemModel)this));
            } else if (this instanceof StructureModel) {
                Game.getInstance().notify(observer -> observer.onStructureComplete((StructureModel)this));
            }
        }
        return _isComplete;
    }

    public void             setComponents(List<ComponentModel> components) {
        _components = components;
    }
    public void             setBuild(int currentBuild, int totalBuild) { _currentBuild = currentBuild; _totalBuild = totalBuild; }
    public void             setBuildJob(BuildJob job) { _buildJob = job; }
    public void             setComplete(boolean complete) { _isComplete = complete; }

    public int              getCurrentBuild() { return _currentBuild; }
    public int              getTotalBuild() { return _totalBuild; }
    public BuildJob         getBuildJob() { return _buildJob; }
    public CharacterModel   getBuilder() { return _buildJob != null ? _buildJob.getCharacter() : null; }
    public double           getBuildProgress() { return (double)_currentBuild / _totalBuild; }

    @Override
    public boolean          isWalkable() { return !_isComplete || _info.isWalkable; }
    public boolean          isComplete() { return _isComplete; }

    public void setReceipt(ItemInfo.ItemInfoReceipt receipt) {

        // Drop all existing components on the floor
        _components.stream().filter(component -> component.currentQuantity > 0)
                .forEach(component -> ModuleHelper.getWorldModule().putConsumable(_parcel, component.info, component.currentQuantity));

        // Set new receipt
        _receipt = receipt;
        _components = receipt.components.stream().map(componentInfo -> new ComponentModel(componentInfo.item, componentInfo.quantity)).collect(Collectors.toList());
    }

    @Override
    public List<ComponentModel>     getComponents() {
        return _components;
    }

}
