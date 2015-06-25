package org.smallbox.faraway.game.model.item;

import java.util.ArrayList;
import java.util.List;

public class ItemModel extends MapObjectModel {
	private List<ConsumableModel> 	_components = new ArrayList<>();
	private List<ConsumableModel> 	_crafts = new ArrayList<>();
	private int 					_targetTemperature = 21;
	private boolean 				_isFunctional = true;
    private boolean                 _isActive = true;
	private int 					_potencyUse;

	public ItemModel(ItemInfo info, int id) {
		super(info, id);
	}

	public ItemModel(ItemInfo info) {
		super(info);
	}

	public boolean isBed() {
		return _info.isBed;
	}

	public boolean isLight() {
		return _info.light > 0;
	}

	public void addComponent(ConsumableModel consumable) {
		for (ConsumableModel component: _components) {
			if (component.getInfo() == consumable.getInfo()) {
				component.addQuantity(consumable.getQuantity());
				return;
			}
		}
		_components.add(consumable);
	}

	public void addCraft(ConsumableModel consumable) {
		for (ConsumableModel craft: _crafts) {
			if (craft.getInfo() == consumable.getInfo()) {
				craft.addQuantity(consumable.getQuantity());
				return;
			}
		}
		_crafts.add(consumable);
	}

	public List<ConsumableModel> getComponents() {
		return _components;
	}

	public List<ConsumableModel> getCrafts() {
		return _crafts;
	}

	public int getTargetTemperature() {
		return _targetTemperature;
	}

	public void setTargetTemperature(int targetTemperature) {
		_targetTemperature = targetTemperature;
	}

	public int getValue() {
		return 15;
	}

	public void setFunctional(boolean isFunctional) { _isFunctional = isFunctional; }
	public boolean isFunctional() { return _isFunctional; }
	public boolean isActive() { return _isActive; }

	public void setPotencyUse(int potencyUse) {
		_potencyUse = potencyUse;
	}

	public int getPotencyUse() {
		return _potencyUse;
	}
}
