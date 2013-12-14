#ifndef _C_CHARACTER_
#define _C_CHARACTER_

#include <ctime>
#include <map>
#include <list>
#include "defines.h"
#include "WorldMap.h"
#include "MapSearchNode.h"

using namespace std;

struct {
  int   id;
  const char* name;
} typedef Job;

class	Character {
 public:
  Character(int x, int y);
  ~Character();

  enum {JOB_NONE, JOB_ENGINEER, JOB_MINER, JOB_DOCTOR, JOB_SCIENCE};

  void	draw(sf::RenderWindow* app, sf::Transform transform);
  void	build(BaseItem* item);
  void	use(BaseItem* item);
  
  // Gets
  sf::Vector2<int>	&get_position();
  sf::Sprite	&get_sprite();
  int           getRun();
  int           getDirection();
  const char*   getJobName() { return _jobName; }

  void          go(int toX, int toY);

  void          move();

  // Sets
  void	set_direction(int direction);
  void	setRun(int direction, bool run);
  void	setItem(BaseItem* item);
  BaseItem*	getItem() { return _item; }
  void	set_position(int x, int y);
  void*	getJob() { return _item; }
  int	getX() { return _posX; }
  int	getY() { return _posY; }
  const char*	getName() { return _name; }
  int   getFood() { return _food; }
  int   getHapiness() { return (int)_hapiness; }
  int   getOxygen() { return _oxygen; }
  int   getEnergy() { return _energy; }
  int   getHealth() { return _health; }
  void  update();
  void  updateNeeds();
  bool  isSleep() { return _sleep > 0; }

  map<int,int>	_run;

  int	_last_direction;
  int	current_frame;
  sf::Vector2<int>		position;

  int	_posX;
  int	_posY;

  int	_toX;
  int	_toY;

 private:
  void	action();
  void	actionUse();
  void	actionBuild();


  char	_name[32];
  AStarSearch<MapSearchNode>* _astarsearch;
  int	_steps;
  BaseItem*	_item;
  const char* _jobName;

  // Needs
  int   _food;
  int   _oxygen;
  float _hapiness;
  int	_health;
  int	_energy;

  // States
  int	_sleep;
  int	_eat;
  int	_drink;
};

#endif
