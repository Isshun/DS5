package alone.in.deepspace.model;

import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.model.ItemInfo.ItemInfoAction;

public class CharacterNeeds {
	public enum Message {
		MSG_HUNGRY,
		MSG_STARVE,
		MSG_NEED_OXYGEN,
		MSG_SLEEP_ON_FLOOR,
		MSG_SLEEP_ON_CHAIR,
		MSG_NO_WINDOW,
		MSG_BLOCKED
	}

	// Actions
	int	_sleeping;
	int	_eating;
	int	_drinking;
	int	_socialize;

	// Stats
	double	_food;
	double 	_happiness;
	double	_relation;
	double	_security;
	double	_oxygen;
	double	_energy;
	double	_health;
	double	_sickness;
	double	_injuries;
	double	_satiety;

	private BaseItem _sleepItem;
	private Character _character;
	private int _workRemain;

	public CharacterNeeds(Character character) {
		_character = character;
		_sleepItem = null;
		_sleeping = 0;
		_food = (int) (Constant.CHARACTER_INIT_FOOD + (Math.random() * 100) % 40 - 20);
		_oxygen = (int) (Constant.CHARACTER_INIT_OXYGEN + (Math.random() * 100) % 20 - 10);
		_happiness = (Constant.CHARACTER_INIT_HAPPINESS + (Math.random() * 100) % 20 - 10);
		_health = (float) (Constant.CHARACTER_INIT_HEALTH + (Math.random() * 100) % 20 - 10);
		_energy = (int) (Constant.CHARACTER_INIT_ENERGY + (Math.random() * 100) % 100);
		_relation = 0;
		_security = 0;
		_injuries = 0;
		_sickness = 0;
		_satiety = 0;
		_workRemain = 100;
	}

	public int	getSleeping() { return _sleeping; }
	public int	getEating() { return _eating; }
	public int	getDrinking() { return _drinking; }
	public int	getSocialize() { return _socialize; }
	public int	getFood() { return (int)Math.ceil(_food); }
	public int	getEnergy() { return (int)Math.ceil(_energy); }
	public int	getHappiness() { return (int)Math.ceil(_happiness); }
	public int	getRelation() { return (int)_relation; }
	public int	getSecurity() { return (int)_security; }
	public int	getOxygen() { return (int)_oxygen; }
	public int	getHealth() { return (int)_health; }
	public int	getSickness() { return (int)_sickness; }
	public int	getInjuries() { return (int)_injuries; }
	public int	getSatiety() { return (int)_satiety; }

	public boolean	isTired() { return _energy <= Constant.LIMITE_TIRED; }
	public boolean	isStarved() { return _food <= Constant.LIMITE_FOOD_STARVE; }
	public boolean	isHungry() { return _food <= Constant.LIMITE_FOOD_HUNGRY; }
	public boolean	isSleeping() { return _sleeping > 0; }
	public boolean 	isLonely() { return _relation <= Constant.LIMITE_RELATION_LONELY; }
	public boolean 	isLowOxygen() { return _oxygen <= Constant.LIMITE_LOW_OXYGEN; }
	public boolean 	isSuffocating() { return _oxygen <= Constant.LIMITE_NO_OXYGEN; }


	public void	update() {

		// // Set hapiness
		// if (_item && _item->isType(BaseItem.Type.QUARTER_BED)) {
			//   removeMessage(MSG_SLEEP_ON_FLOOR);
		//   removeMessage(MSG_SLEEP_ON_CHAIR);
		// } else if (_item && _item->isType(BaseItem.Type.QUARTER_CHAIR)) {
		//   _hapiness -= 0.1;
		//   addMessage(MSG_SLEEP_ON_CHAIR, count);
		//   removeMessage(MSG_SLEEP_ON_FLOOR);
		// } else {
		//   addMessage(MSG_SLEEP_ON_FLOOR, count);
		//   _hapiness -= 0.25;
		// }

		// 	// If current item is not under construction: abort
		// 	if (_sleep == 0 && _item != NULL && _item->isComplete()) {
		// 	  _item->setOwner(NULL);
		// 	  _item = NULL;
		// 	  _job = NULL;
		// 	}
		// 	return;
		// }

		// Food: starve
		if (isStarved()) {
			// addMessage(MSG_STARVE, count);
			// removeMessage(MSG_HUNGRY);
			_happiness = Math.max(_happiness - 0.5f, 0.0f);
			_health = Math.max(_health - 0.1f, 0.0f);
			if (_sleeping <= 0) {
				System.out.println("change 1");
				_energy = (int) Math.max(_energy - 1.0f, 0.0f);
			}
		}

		// Food: hungry
		else if (isHungry()) {
			// addMessage(MSG_HUNGRY, count);
			_happiness = Math.max(_happiness - 0.2f, 0.0f);
		} else {
			// removeMessage(MSG_STARVE);
			// removeMessage(MSG_HUNGRY);
		}


		// // Oxygen
		// WorldArea* area = WorldMap.Type.getInstance()->getArea(_posX, _posY);
		// if (area != NULL) {
		// 	if (area->getOxygen() > _oxygen) {
		// 	  _oxygen = min(area->getOxygen(), _oxygen + 5);
		// 	} else {
		// 	  _oxygen = max(area->getOxygen(), _oxygen - 5);
		// 	}
		// } else {
		// 	_oxygen = max(0, _oxygen - 5);
		// }

		// if (_oxygen == 0) {
		// 	addMessage(MSG_NEED_OXYGEN, count);
		// 	_oxygen = 0;
		// } else {
		// 	removeMessage(MSG_NEED_OXYGEN);
		// }

		// Character is sleeping
		if (_sleeping > 0) {
			updateSleeping();
		} else {
			updateAwake();
		}
	}

	void	updateAwake() {
		// Energy
		
		System.out.println("update awake");

		// Food
		_food -= 2;
		_energy -= 1;
	}

	void	updateSleeping() {

		// Sleep on floor
		if (_sleepItem == null) {
			_sleeping -= 10;
			_energy = Math.min(_energy + (double)Constant.SLEEP_ON_FLOOR_ENERGY_RESTORE / Constant.SLEEP_ON_FLOOR_DURATION, 100);
			_happiness = Math.min(_happiness + (double)Constant.SLEEP_ON_FLOOR_HAPINESS_RESTORE / Constant.SLEEP_ON_FLOOR_DURATION, 100);
		}

		// Minor health gain
		if (_health > 40) {
			_health = Math.min(_health + 1, 100.0f);
		}

		if (_sleeping <= 0) {
			onAwakening();
		}
	}

	private void onAwakening() {
		_workRemain = 100;
	}

	public void	eat() {
		_food = 100;
	}

	void	drink() {
		_satiety = 100;
	}

	public void	sleep(BaseItem item) {
		_sleepItem = item;
		if (item != null) {
			_sleeping = item.getInfo().onAction.duration;
		} else {
			_sleeping = Constant.SLEEP_ON_FLOOR_DURATION;
		}
	}

	public int getWorkRemain() {
		return _workRemain;
	}

	public void setWorkRemain(int value) {
		_workRemain = value;
	}

	public void addRelation(int i) {
		_relation = Math.min(_relation + 1, 100);
	}

	public void use(BaseItem item, ItemInfoAction action, int durationLeft) {
		if (item.isSleepingItem()) {
			_sleepItem = item;
			_sleeping = durationLeft;
		}
		
		if (action != null && action.effects != null) {
			_energy = Math.min(_energy + (double)action.effects.energy / (double)action.duration, 100);
			_food = Math.min(_food + (double)action.effects.food / action.duration, 100);
			_happiness = Math.min(_happiness + (double)action.effects.hapiness / action.duration, 100);
			_health = Math.min(_health + (double)action.effects.health / action.duration, 100);
			_relation = Math.min(_relation + (double)action.effects.relation / action.duration, 100);
		}
	}

}
