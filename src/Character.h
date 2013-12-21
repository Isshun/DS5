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
  int			id;
  const char*	name;
  sf::Color		color;
  sf::Color		textColor;
} typedef		Profession;

class	Character {
 public:
  Character(int id, int x, int y);
  ~Character();

  void	setProfession(int professionId);
  void	setProfession(Profession profession) { _profession = profession; }

  enum {
	PROFESSION_NONE,
	PROFESSION_ENGINEER,
	PROFESSION_MINER,
	PROFESSION_DOCTOR,
	PROFESSION_SCIENCE,
	PROFESSION_SECURITY
  };

  enum {
	GENDER_NONE,
	GENDER_MALE,
	GENDER_FEMALE,
	GENDER_BOTH
  };

  enum {
	MSG_HUNGRY,
	MSG_STARVE,
	MSG_NEED_OXYGEN,
	MSG_SLEEP_ON_FLOOR,
	MSG_SLEEP_ON_CHAIR,
	MSG_NO_WINDOW,
	MSG_BLOCKED
  };
  
  void	draw(sf::RenderWindow* app, sf::Transform transform);
  void	build(AStarSearch<MapSearchNode>* path, BaseItem* item);
  void	use(AStarSearch<MapSearchNode>* path, BaseItem* item);
  
  // Gets
  sf::Vector2<int>	&get_position();
  sf::Sprite	&get_sprite();
  int           getRun();
  int           getDirection();
  Profession	getProfession() { return _profession; }

  void			sendEvent(int event);
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
  void	setSelected(bool selected) { _selected = selected; }

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
  bool	getSelected() { return _selected; }
  int	getFrameIndex() { return _frameIndex++; }

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
  int	_frameIndex;
  int	_gender;
  char	_name[32];
  AStarSearch<MapSearchNode>* _astarsearch;
  int	_steps;
  BaseItem*	_item;
  BaseItem*	_build;
  Profession	_profession;
  bool	_selected;
  int	_blocked;

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
