package org.smallbox.faraway.game.model.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 14/07/2015.
 */
public class BuildableMapObject extends MapObjectModel {
    private List<ConsumableModel> _components = new ArrayList<>();

    public BuildableMapObject(ItemInfo info, int id) {
        super(info, id);
    }

    public BuildableMapObject(ItemInfo info) {
        super(info);
    }

    @Override
    public void addComponent(ConsumableModel consumable) {
        for (ConsumableModel component: _components) {
            if (component.getInfo() == consumable.getInfo()) {
                component.addQuantity(consumable.getQuantity());
                return;
            }
        }
        _components.add(consumable);
    }

    @Override
    public List<ConsumableModel> 	getComponents() {
        return _components;
    }

}
