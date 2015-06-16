package org.smallbox.faraway.model.character;

import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.model.GameConfig;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.item.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.model.item.MapObjectModel;

public class CharacterNeeds {
    private final CharacterModel 	_character;
	private final GameData      	_data;

	// Actions
	private boolean _isSleeping;
	private int	_eating;

	// Stats
    public double socialize;
    public double drinking;
    public double food;
    public double happiness;
	public double relation;
    public double security;
    public double oxygen;
    public double energy;
    public double health;
    public double sickness;
    public double injuries;
    public double satiety;

	private MapObjectModel 	_sleepItem;
	private CharacterStats	_stats;

	public CharacterNeeds(CharacterModel character) {
        _data = GameData.getData();
		_stats = character.getStats();
        _character = character;
		_sleepItem = null;
		food = (int) (Constant.CHARACTER_INIT_FOOD + (Math.random() * 100) % 40 - 20);
		oxygen = (int) (Constant.CHARACTER_INIT_OXYGEN + (Math.random() * 100) % 20 - 10);
		happiness = (Constant.CHARACTER_INIT_HAPPINESS + (Math.random() * 100) % 20 - 10);
		health = (float) (Constant.CHARACTER_INIT_HEALTH + (Math.random() * 100) % 20 - 10);
		energy = (int) (Constant.CHARACTER_INIT_ENERGY + (Math.random() * 100) % 100);
		energy = 100;
		relation = 0;
		security = 0;
		injuries = 0;
		sickness = 0;
		satiety = 0;
	}

//	public int	getEating() { return _eating; }
//	public int	getDrinking() { return _drinking; }
//	public int	getSocialize() { return _socialize; }
	public int	getFood() { return (int)Math.ceil(food); }
	public int	getEnergy() { return (int)Math.ceil(energy); }
	public int	getHappiness() { return (int)Math.ceil(happiness); }

	public boolean  isExhausted() { return energy <= Constant.LIMITE_TIRED; }
	public boolean	isStarved() { return food <= Constant.LIMITE_FOOD_STARVE; }
	public boolean	isHungry() { return food <= Constant.LIMITE_FOOD_HUNGRY; }
	public boolean	isSleeping() { return _isSleeping; }
	public boolean 	isLonely() { return relation <= Constant.LIMITE_RELATION_LONELY; }
	public boolean 	isLowOxygen() { return oxygen <= Constant.LIMITE_LOW_OXYGEN; }
	public boolean 	isSuffocating() { return oxygen <= Constant.LIMITE_NO_OXYGEN; }

	public void updateHappiness(double change) {
//		double modifier = 1f - Math.abs((50 - happiness) / 50f);
//		happiness = happiness + change * modifier;
//		Log.info("hapi: " + happiness + ", modif: " + modifier);
        happiness = happiness + change;
	}

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

        if (energy >= 100) {
            _isSleeping = false;
        }

        // Set needs bounds
        food = Math.max(0, Math.min(100, food));
        energy = Math.max(0, Math.min(100, energy));
        oxygen = Math.max(0, Math.min(100, oxygen));
        happiness = Math.max(0, Math.min(100, happiness));
        relation = Math.max(0, Math.min(100, relation));
        security = Math.max(0, Math.min(100, security));

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
//			happiness = Math.max(happiness - 0.5f, 0.0f);
//			health = Math.max(health - 0.1f, 0.0f);
//			if (_isSleeping <= 0) {
//				energy = (int) Math.max(energy - 1.0f, 0.0f);
//			}
//		}
//
//		// Food: hungry
//		else if (isHungry()) {
//			// addMessage(MSG_HUNGRY, quantity);
//			happiness = Math.max(happiness - 0.2f, 0.0f);
//		} else {
//			// removeMessage(MSG_STARVE);
//			// removeMessage(MSG_HUNGRY);
//		}


		// // Oxygen
		// WorldArea* area = WorldMap.Type.getInstance()->getParcel(_x, _y);
		// if (area != NULL) {
		// 	if (area->getOxygen() > oxygen) {
		// 	  oxygen = min(area->getOxygen(), oxygen + 5);
		// 	} else {
		// 	  oxygen = max(area->getOxygen(), oxygen - 5);
		// 	}
		// } else {
		// 	oxygen = max(0, oxygen - 5);
		// }

		// if (oxygen == 0) {
		// 	addMessage(MSG_NEED_OXYGEN, quantity);
		// 	oxygen = 0;
		// } else {
		// 	removeMessage(MSG_NEED_OXYGEN);
		// }


	}

    private void updateNeeds(GameConfig.EffectValues effects) {
        food += effects.food;
        energy += effects.energy;

		// Increase oxygen
		if (effects.oxygen > 0) {
			oxygen += effects.oxygen;
		}
		// Decrease oxygen, use resist
		else {
			oxygen += effects.oxygen * (1 - _stats.resist.oxygen / 100f);
		}

        happiness += effects.happiness;
        relation += effects.relation;
        security += effects.security;
    }

    private void updateNeeds(ItemInfoAction action) {
        food += action.effects.food / (double)action.cost;
        energy += action.effects.energy / (double)action.cost;
        oxygen += action.effects.oxygen / (double)action.cost;
        happiness += action.effects.happiness / (double)action.cost;
        relation += action.effects.relation / (double)action.cost;
        security += action.effects.security / (double)action.cost;
    }

	public void	eat() {
		food = 100;
	}

	void	drink() {
		satiety = 100;
	}

//	public void	sleep(ItemBase item) {
//        if (_character.getHaul() != null) {
//            JobManager.getInstance().removeJob(_character.getHaul());
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
		relation = Math.min(relation + 1, 100);
	}

	public void use(MapObjectModel item, ItemInfoAction action) {
		if (item.isSleepingItem()) {
			_sleepItem = item;
			_isSleeping = true;
		}
		
		if (action != null && action.effects != null) {
			energy = Math.min(energy + (double)action.effects.energy / action.cost, 100);
			food = Math.min(food + (double)action.effects.food / action.cost, 100);
			happiness = Math.min(happiness + (double)action.effects.happiness / action.cost, 100);
			health = Math.min(health + (double)action.effects.health / action.cost, 100);
			relation = Math.min(relation + (double)action.effects.relation / action.cost, 100);
		}
	}

	public void setSleeping(boolean isSleeping) {
		_isSleeping = isSleeping;
	}
}
