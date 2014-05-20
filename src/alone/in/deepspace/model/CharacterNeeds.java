package alone.in.deepspace.model;

import alone.in.deepspace.Utils.Constant;

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
		  int	_food;
		  float	_happiness;
		  int	_relation;
		  int	_security;
		  int	_oxygen;
		  int	_energy;
		  float	_health;
		  int	_sickness;
		  int	_injuries;
		  int	_satiety;

		private BaseItem _sleepItem;
		private Character _character;
		private int _workRemain;

		  public CharacterNeeds(Character character) {
			  _character = character;
			  _sleepItem = null;
			  _sleeping = 0;
			  _food = (int) (Constant.CHARACTER_INIT_FOOD + (Math.random() * 100) % 40 - 20);
			  _oxygen = (int) (Constant.CHARACTER_INIT_OXYGEN + (Math.random() * 100) % 20 - 10);
			  _happiness = (float) (Constant.CHARACTER_INIT_HAPPINESS + (Math.random() * 100) % 20 - 10);
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
		  public int	getFood() { return _food; }
		  public int	getEnergy() { return _energy; }
		  public int	getHappiness() { return (int) _happiness; }
		  public int	getRelation() { return _relation; }
		  public int	getSecurity() { return _security; }
		  public int	getOxygen() { return _oxygen; }
		  public int	getHealth() { return (int) _health; }
		  public int	getSickness() { return _sickness; }
		  public int	getInjuries() { return _injuries; }
		  public int	getSatiety() { return _satiety; }

		  public boolean	isSleeping() { return _sleeping > 0; }

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

		  // Food
		  _food -= 2;

		  // Food: starve
		  if (_food <= Constant.LIMITE_FOOD_STARVE) {
			// addMessage(MSG_STARVE, count);
			// removeMessage(MSG_HUNGRY);
			_happiness = Math.max(_happiness - 0.5f, 0.0f);
			_health = Math.max(_health - 0.1f, 0.0f);
			if (_sleeping <= 0) {
			  _energy = (int) Math.max(_energy - 1.0f, 0.0f);
			}
		  }

		  // Food: hungry
		  else if (_food <= Constant.LIMITE_FOOD_HUNGRY) {
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

		  // Sleep on the ground
		  if (_energy <= 0) {
			sleep(null);
		  }
		}

		void	updateAwake() {
		  // Energy
		  _energy -= 1;
		}

		void	updateSleeping() {
		  _sleeping -= 6;

		  // Strong heal if character in sickbay
		  
		  // TODO
		  if (_sleepItem != null) {
			  if (_sleepItem.getName().equals("base.bed")) {
				_energy = Math.min(_energy + 6, 100);
				_happiness += 0.1;
			  } else if (_sleepItem.getName().equals("base.chair")) {
				_energy = Math.min(_energy + 5, 100);
				_happiness -= 0.1;
			  } else if (_sleepItem.getName().equals("base.biobed")) {
				_energy = Math.min(_energy + 6, 100);
				if (_health > 20) {
				  _health = Math.max(_health + 2, 100.0f);
				}
			  } else if (_sleepItem.getName().equals("base.emergency_shelters")) {
				_energy = Math.min(_energy + 6, 100);
				_health = Math.min(_health + 4, 100.0f);
			  }
		  } else {
			_energy = Math.min(_energy + 5, 100);
			_happiness -= 0.1;
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
		  _sleeping = 100;
		}

		public int getWorkRemain() {
			return _workRemain;
		}

		public void setWorkRemain(int value) {
			_workRemain = value;
		}

}