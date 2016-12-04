package org.smallbox.faraway.core.module.world.model;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.BuildJob;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 14/07/2015.
 */
public class BuildableMapObject extends MapObjectModel {
    public BuildableMapObject(ItemInfo info) {
        super(info);
    }

    public BuildableMapObject(ItemInfo info, int id) {
        super(info, id);
    }

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

    private int                         _buildProgress;
    private BuildJob                    _buildJob;
    private List<ComponentModel>        _components = new ArrayList<>();

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

    /**
     * Build item
     *
     * @return true if item build is complete
     */
    public boolean build() {
        if (_buildProgress < _info.build.cost) {
            _buildProgress++;

            // Item build complete
            if (_buildProgress >= _info.build.cost) {
                Application.notify(observer -> observer.onObjectComplete(this));
                return true;
            }

            return false;
        }

        return true;
    }

    public void             setComponents(List<ComponentModel> components) {
        _components = components;
    }
    public void             setBuildJob(BuildJob job) { _buildJob = job; }
    public void             setBuildProgress(int buildProgress) { _buildProgress = buildProgress; }

    public int              getBuildProgress() { return _buildProgress; }
    public int              getBuildCost() { return _info.build.cost; }
    public BuildJob         getBuildJob() { return _buildJob; }
    public CharacterModel   getBuilder() { return _buildJob != null ? _buildJob.getCharacter() : null; }

    @Override
    public boolean          isWalkable() { return !isComplete() || _info.isWalkable; }
    public boolean          isComplete() { return _buildProgress >= _info.build.cost; }

    public void setReceipt(ItemInfo.ItemInfoReceipt receipt) {
        throw new NotImplementedException("");

//        // Drop all existing components on the floor
//        _components.stream().filter(component -> component.currentQuantity > 0)
//                .forEach(component -> ModuleHelper.getWorldModule().putConsumable(_parcel, component.info, component.currentQuantity));
//
//        // Set new receipt
//        _receipt = receipt;
//        _components = receipt.components.stream().map(componentInfo -> new ComponentModel(componentInfo.item, componentInfo.quantity)).collect(Collectors.toList());
    }

    @Override
    public List<ComponentModel>     getComponents() {
        return _components;
    }

}
