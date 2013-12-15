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
  Character(int id, int x, int y);
  ~Character();

  enum {JOB_NONE, JOB_ENGINEER, JOB_MINER, JOB_DOCTOR, JOB_SCIENCE};

  enum {
	MSG_HUNGRY,
	MSG_STARVE,
	MSG_NEED_OXYGEN,
	MSG_SLEEP_ON_FLOOR,
	MSG_SLEEP_ON_CHAIR,
	MSG_NO_WINDOW
  };
  
  void	draw(sf::RenderWindow* app, sf::Transform transform);
  void	build(AStarSearch<MapSearchNode>* path, BaseItem* item);
  void	use(AStarSearch<MapSearchNode>* path, BaseItem* item);
  
  // Gets
  sf::Vector2<int>	&get_position();
  sf::Sprite	&get_sprite();
  int           getRun();
  int           getDirection();
  const char*   getJobName() { return _jobName; }

  void			addMessage(int msg, int count);
  void			removeMessage(int msg);
  void			go(AStarSearch<MapSearchNode>* astarsearch, int toX, int toY);

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
  int	getId() { return _id; }
  const char*	getName() { return _name; }
  int   getFood() { return _food; }
  int   getHapiness() { return (int)_hapiness; }
  int   getOxygen() { return _oxygen; }
  int   getEnergy() { return _energy; }
  int   getHealth() { return _health; }
  int*  getMessages() { return _messages; }

  void	action();
  void  update();
  void  updateNeeds(int count);
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
  void	actionUse();
  void	actionBuild();

  int	_id;
  char	_name[32];
  AStarSearch<MapSearchNode>* _astarsearch;
  int	_steps;
  BaseItem*	_item;
  BaseItem*	_build;
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

  int	_messages[CHARACTER_MAX_MESSAGE];
};

#endif
