package org.smallbox.faraway.game.model.item;

import org.smallbox.faraway.game.model.ObjectModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.base.BuildJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 14/07/2015.
 */
public class BuildableMapObject extends MapObjectModel {
    public static class ComponentModel extends ObjectModel {
        public ItemInfo         info;
        public int              currentQuantity;
        public int              neededQuantity;
        public BaseJobModel     job;

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

    private int                     _totalBuild = 10;
    private int                     _currentBuild;
    private boolean                 _isComplete;
    private BuildJob                _buildJob;
    private List<ComponentModel>    _components = new ArrayList<>();

    public BuildableMapObject(ItemInfo info, int id) {
        super(info, id);
    }

    public BuildableMapObject(ItemInfo info) {
        super(info);
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

    public boolean          build() { return _isComplete = (_isComplete || ++_currentBuild >= _totalBuild); }

    @Override
    public boolean          isComplete() { return _isComplete; }

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
    public List<ComponentModel> 	getComponents() {
        return _components;
    }

}
