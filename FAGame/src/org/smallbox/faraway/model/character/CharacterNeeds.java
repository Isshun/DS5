package org.smallbox.faraway.model.character;

import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.model.GameConfig;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.item.MapObjectModel;
import org.smallbox.faraway.model.item.ItemInfo.ItemInfoAction;

public class CharacterNeeds {
    private final CharacterModel 	_character;
	private final GameData      	_data;

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
	private boolean _isSleeping;
	private int	_eating;
	private int	_drinking;
	private int	_socialize;

	// Stats
	private double	_food;
	private double 	_happiness;
	private double	_relation;
	private double	_security;
	private double	_oxygen;
	private double	_energy;
	private double	_health;
	private double	_sickness;
	private double	_injuries;
	private double	_satiety;

	private MapObjectModel _sleepItem;

	public CharacterNeeds(CharacterModel character) {
        _data = GameData.getData();
        _character = character;
		_sleepItem = null;
		_food = (int) (Constant.CHARACTER_INIT_FOOD + (Math.random() * 100) % 40 - 20);
		_oxygen = (int) (Constant.CHARACTER_INIT_OXYGEN + (Math.random() * 100) % 20 - 10);
		_happiness = (Constant.CHARACTER_INIT_HAPPINESS + (Math.random() * 100) % 20 - 10);
		_health = (float) (Constant.CHARACTER_INIT_HEALTH + (Math.random() * 100) % 20 - 10);
		_energy = (int) (Constant.CHARACTER_INIT_ENERGY + (Math.random() * 100) % 100);
		_energy = 100;
		_relation = 0;
		_security = 0;
		_injuries = 0;
		_sickness = 0;
		_satiety = 0;
	}

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

	public boolean  isExhausted() { return _energy <= Constant.LIMITE_TIRED; }
	public boolean	isStarved() { return _food <= Constant.LIMITE_FOOD_STARVE; }
	public boolean	isHungry() { return _food <= Constant.LIMITE_FOOD_HUNGRY; }
	public boolean	isSleeping() { return _isSleeping; }
	public boolean 	isLonely() { return _relation <= Constant.LIMITE_RELATION_LONELY; }
	public boolean 	isLowOxygen() { return _oxygen <= Constant.LIMITE_LOW_OXYGEN; }
	public boolean 	isSuffocating() { return _oxygen <= Constant.LIMITE_NO_OXYGEN; }

	public void setDrinking(int drinking) { _drinking = drinking; }
	public void setEating(int eating) { _eating = eating; }
	public void setEnergy(double energy) { _energy = energy; }
	public void setFood(double food) { _food = food; }
	public void setHappiness(double happiness) { _happiness = happiness; }
	public void updateHappiness(double change) { _happiness = Math.max(0, Math.min(100, _happiness + change)); }
	public void setHealth(double health) { _health = health; }
	public void setInjuries(double injuries) { _injuries = injuries; }
	public void setOxygen(double oxygen) { _oxygen = oxygen; }
	public void setRelation(double relation) { _relation = relation; }
	public void setSatiety(double satiety) { _satiety = satiety; }
	public void setSecurity(double security) { _security = security; }
	public void setSickness(double sickness) { _sickness = sickness; }
	public void setSleeping(boolean sleeping) { _isSleeping = sleeping; }
	public void setSocialize(int socialize) { _socialize = socialize; }

	public void	update() {
        updateNeeds(_data.config.character.effects.regular);

        // Food: starve
        if (isStarved()) {
            updateNeeds(_data.config.character.effects.starve);
        }

        if (isExhausted()) {
            updateNeeds(_data.config.character.effects.exhausted);
        }

        // Character is sleeping
        if (isSleeping() && _sleepItem != null) {
            updateNeeds(_sleepItem.getInfo().actions.get(0));
        } else if (isSleeping()) {
            updateNeeds(_data.config.character.effects.sleepOnFloor);
        }

        if (_energy >= 100) {
            _isSleeping = false;
        }

        // Set needs bounds
        _food = Math.max(0, Math.min(100, _food));
        _energy = Math.max(0, Math.min(100, _energy));
        _oxygen = Math.max(0, Math.min(100, _oxygen));
        _happiness = Math.max(0, Math.min(100, _happiness));
        _relation = Math.max(0, Math.min(100, _relation));
        _security = Math.max(0, Math.min(100, _security));

        // // Set happiness
		// if (_item && _item->isType(BaseItem.Type.QUARTER_BED)) {
			//   removeMessage(MSG_SLEEP_ON_FLOOR);
		//   removeMessage(MSG_SLEEP_ON_CHAIR);
		// } else if (_item && _item->isType(BaseItem.Type.QUARTER_CHAIR)) {
		//   _hapiness -= 0.1;
		//   addMessage(MSG_SLEEP_ON_CHAIR, quantity);
		//   removeMessage(MSG_SLEEP_ON_FLOOR);
		// } else {
		//   addMessage(MSG_SLEEP_ON_FLOOR, quantity);
		//   _hapiness -= 0.25;
		// }

		// 	// If current item is not under construction: quit
		// 	if (_sleep == 0 && _item != NULL && _item->hasComponents()) {
		// 	  _item->setOwner(NULL);
		// 	  _item = NULL;
		// 	  _job = NULL;
		// 	}
		// 	return;
		// }

//			// addMessage(MSG_STARVE, quantity);
//			// removeMessage(MSG_HUNGRY);
//			_happiness = Math.max(_happiness - 0.5f, 0.0f);
//			_health = Math.max(_health - 0.1f, 0.0f);
//			if (_isSleeping <= 0) {
//				_energy = (int) Math.max(_energy - 1.0f, 0.0f);
//			}
//		}
//
//		// Food: hungry
//		else if (isHungry()) {
//			// addMessage(MSG_HUNGRY, quantity);
//			_happiness = Math.max(_happiness - 0.2f, 0.0f);
//		} else {
//			// removeMessage(MSG_STARVE);
//			// removeMessage(MSG_HUNGRY);
//		}


		// // Oxygen
		// WorldArea* area = WorldMap.Type.getInstance()->getParcel(_x, _y);
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
		// 	addMessage(MSG_NEED_OXYGEN, quantity);
		// 	_oxygen = 0;
		// } else {
		// 	removeMessage(MSG_NEED_OXYGEN);
		// }


	}

    private void updateNeeds(GameConfig.EffectValues effects) {
        _food += effects.food;
        _energy += effects.energy;
        _oxygen += effects.oxygen;
        _happiness += effects.happiness;
        _relation += effects.relation;
        _security += effects.security;
    }

    private void updateNeeds(ItemInfoAction action) {
        _food += action.effects.food / (double)action.cost;
        _energy += action.effects.energy / (double)action.cost;
        _oxygen += action.effects.oxygen / (double)action.cost;
        _happiness += action.effects.happiness / (double)action.cost;
        _relation += action.effects.relation / (double)action.cost;
        _security += action.effects.security / (double)action.cost;
    }

	public void	eat() {
		_food = 100;
	}

	void	drink() {
		_satiety = 100;
	}

//	public void	sleep(ItemBase item) {
//        if (_character.getJob() != null) {
//            JobManager.getInstance().removeJob(_character.getJob());
//        }
//
//		_sleepItem = item;
//		if (item != null) {
//            _isSleeping = Constant.SLEEP_DURATION * Constant.DURATION_MULTIPLIER;
//		} else {
//			_isSleeping = Constant.SLEEP_ON_FLOOR_DURATION * Constant.DURATION_MULTIPLIER;
//		}
//	}

	public void addRelation(int i) {
		_relation = Math.min(_relation + 1, 100);
	}

	public void use(MapObjectModel item, ItemInfoAction action) {
		if (item.isSleepingItem()) {
			_sleepItem = item;
			_isSleeping = true;
		}
		
		if (action != null && action.effects != null) {
			_energy = Math.min(_energy + (double)action.effects.energy / action.cost, 100);
			_food = Math.min(_food + (double)action.effects.food / action.cost, 100);
			_happiness = Math.min(_happiness + (double)action.effects.happiness / action.cost, 100);
			_health = Math.min(_health + (double)action.effects.health / action.cost, 100);
			_relation = Math.min(_relation + (double)action.effects.relation / action.cost, 100);
		}
	}
}
