#ifndef CHARACTER_NEEDS_H_
#define CHARACTER_NEEDS_H_

class CharacterNeeds {

 public:

  enum {
	MSG_HUNGRY,
	MSG_STARVE,
	MSG_NEED_OXYGEN,
	MSG_SLEEP_ON_FLOOR,
	MSG_SLEEP_ON_CHAIR,
	MSG_NO_WINDOW,
	MSG_BLOCKED
  };

  CharacterNeeds();
  void	update();

  int	getSleeping() { return _sleeping; }
  int	getEating() { return _eating; }
  int	getDrinking() { return _drinking; }
  int	getSocialize() { return _socialize; }
  int	getFood() { return _food; }
  int	getEnergy() { return _energy; }
  int	getHappiness() { return _happiness; }
  int	getRelation() { return _relation; }
  int	getSecurity() { return _security; }
  int	getOxygen() { return _oxygen; }
  int	getHealth() { return _health; }
  int	getSickness() { return _sickness; }
  int	getInjuries() { return _injuries; }
  int	getSatiety() { return _satiety; }

  bool	isSleeping() { return _sleeping > 0; }

  void	eat();
  void	drink();
  void	sleep(int itemType);


 private:
  void	updateSleeping();
  void	updateAwakening();

  int	_sleepItem;

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
};

#endif
