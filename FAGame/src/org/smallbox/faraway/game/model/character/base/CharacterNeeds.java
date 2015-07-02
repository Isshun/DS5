package org.smallbox.faraway.game.model.character.base;

import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.util.Constant;

public class CharacterNeeds {
    private final CharacterModel 	_character;
	private final GameData      	_data;

	// Actions
	public boolean isSleeping;
	private int	_eating;

	// Stats
    public double 	socialize;
    public double 	drinking;
    public double 	food;
    public double 	happiness;
    public double   happinessChange;
	public double 	relation;
    public double 	security;
    public double 	oxygen;
    public double 	energy;
    public double 	health;
    public double 	sickness;
    public double 	injuries;
    public double 	satiety;
    public double 	joy;
	public int 		environment;
	public int 		light;
	public int 		pain = 80;

	private MapObjectModel 	_sleepItem;
	private CharacterStats	_stats;
    private boolean isFainting;

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
		joy = 0;
	}

	public int	getFood() { return (int)Math.ceil(food); }
	public int	getEnergy() { return (int)Math.ceil(energy); }

	public boolean	isSleeping() { return isSleeping; }

	public void	update() {
        updateNeeds(_character.getNeedEffects());

//        // Food: starve
//        if (isStarved()) {
//            updateNeeds(_data.config.character.effects.starve);
//        }
//
//        if (isExhausted()) {
//            updateNeeds(_data.config.character.effects.exhausted);
//        }

        happiness += happinessChange / 10;

        // Character is sleeping
        if (isSleeping() && _sleepItem != null) {
            updateNeeds(_sleepItem.getInfo().actions.get(0));
        } else if (isSleeping()) {
			energy += 10;
        }

        if (energy >= 100) {
            isSleeping = false;
        }

        // Set needs bounds
        food = Math.max(0, Math.min(100, food));
        energy = Math.max(0, Math.min(100, energy));
        oxygen = Math.max(0, Math.min(100, oxygen));
        happiness = Math.max(0, Math.min(100, happiness));
        relation = Math.max(0, Math.min(100, relation));
        security = Math.max(0, Math.min(100, security));

		// 	// If current item is not under construction: quit
		// 	if (_sleep == 0 && _item != NULL && _item->hasComponentsOnMap()) {
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
//			if (isSleeping <= 0) {
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

    public void updateNeeds(GameConfig.EffectValues effects) {
        food += effects.food;
        energy += effects.energy;

		// Increase oxygen
		if (_character.getParcel() != null) {
			int oxygenLevel = (int)(_character.getParcel().getOxygen() * 100);
			if (oxygen < oxygenLevel) {
				oxygen += 1;
			}
			// Decrease oxygen, use resist
			else {
				oxygen -= 1 * (1 - _stats.resist.oxygen / 100f);
			}
		}

        happiness += effects.happiness;
        relation += effects.relation;
        security += effects.security;
        joy += effects.joy;
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
//            isSleeping = Constant.SLEEP_DURATION * Constant.DURATION_MULTIPLIER;
//		} else {
//			isSleeping = Constant.SLEEP_ON_FLOOR_DURATION * Constant.DURATION_MULTIPLIER;
//		}
//	}

	public void addRelation(int i) {
		relation = Math.min(relation + 1, 100);
	}

	public void use(MapObjectModel item, ItemInfoAction action) {
		if (item.isSleepingItem()) {
			_sleepItem = item;
			isSleeping = true;
		} else {
			_sleepItem = null;
		}
		
		if (action != null && action.effects != null) {
			energy = Math.min(energy + (double)action.effects.energy / action.cost, 100);
			food = Math.min(food + (double)action.effects.food / action.cost, 100);
			happiness = Math.min(happiness + (double)action.effects.happiness / action.cost, 100);
			health = Math.min(health + (double)action.effects.health / action.cost, 100);
			relation = Math.min(relation + (double)action.effects.relation / action.cost, 100);
			joy = Math.min(joy + (double)action.effects.joy / action.cost, 100);
		}
	}

	public void setSleeping(boolean isSleeping) {
		this.isSleeping = isSleeping;
	}

    // TODO: magic number
    public boolean isHungry() {
        return this.food < 20;
    }

    // TODO: magic number
    public boolean isExhausted() {
        return this.energy < 20;
    }

    public void setFainting(boolean isFainting) {
        this.isFainting = isFainting;
    }
}
