#ifndef _C_CHARACTER_
#define _C_CHARACTER_

#include <ctime>
#include <map>
#include <list>
#include "defines.h"
#include "WorldMap.h"
#include "MapSearchNode.h"
#include "PathManager.h"
#include "FileManager.h"

using namespace std;

struct {
  int			id;
  const char*	name;
  sf::Color		color;
  sf::Color		textColor;
} typedef		Profession;

class	Character : public IPathManagerCallback, public Serializable {
 public:
  Character(int id, int x, int y);
  ~Character();

  enum {
	PROFESSION_NONE,
	PROFESSION_ENGINEER,
	PROFESSION_MINER,
	PROFESSION_DOCTOR,
	PROFESSION_SCIENCE,
	PROFESSION_SECURITY
  };

  enum {
	DIRECTION_BOTTOM,
	DIRECTION_LEFT,
	DIRECTION_RIGHT,
	DIRECTION_TOP,
	DIRECTION_BOTTOM_RIGHT,
	DIRECTION_BOTTOM_LEFT,
	DIRECTION_TOP_RIGHT,
	DIRECTION_TOP_LEFT,
	DIRECTION_NONE
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

  enum {
	GOAL_NONE,
	GOAL_USE,
	GOAL_BUILD,
	GOAL_MOVE
  };

  // Actions
  void			action();
  void          move();
  void  		update();
  void  		updateNeeds(int count);
  void			draw(sf::RenderWindow* app, sf::Transform transform);
  void			build(AStarSearch<MapSearchNode>* path, BaseItem* item);
  void			use(AStarSearch<MapSearchNode>* path, BaseItem* item);
  bool  		isSleep() { return _sleep > 0; }
  void			sendEvent(int event);
  void			addMessage(int msg, int count);
  void			removeMessage(int msg);
  void			go(AStarSearch<MapSearchNode>* astarsearch, int toX, int toY);
  void			go(int toX, int toY);

  virtual void	load(const char* filePath);
  virtual void	save(const char* filePath);

  virtual void	onPathSearch(Path* path, BaseItem* item);
  virtual void	onPathComplete(Path* path, BaseItem* item);
  virtual void	onPathFailed(BaseItem* item);
  
  // Sets
  void			setProfession(int professionId);
  void			setProfession(Profession profession) { _profession = profession; }
  void			setDirection(int direction);
  void			setRun(int direction, bool run);
  void			setItem(BaseItem* item);
  BaseItem*		getItem() { return _item; }
  void			setDosition(int x, int y);
  void			setSelected(bool selected) { _selected = selected; }
  void			setBuild(BaseItem* item) { _build = item; }
  void			setName(const char* name) { strcpy(_name, name); }
  void			setOffset(int offset) { _offset = offset; }

  // Gets
  sf::Vector2<int>	&get_position();
  sf::Sprite	&get_sprite();
  int           getRun();
  int           getDirection() { return _direction; }
  Profession	getProfession() { return _profession; }
  int			getProfessionId() { return _profession.id; }
  void*			getJob() { return _item; }
  int			getX() { return _posX; }
  int			getY() { return _posY; }
  int			getId() { return _id; }
  const char*	getName() { return _name; }
  int   		getFood() { return _food; }
  int   		getHapiness() { return (int)_hapiness; }
  int   		getOxygen() { return _oxygen; }
  int   		getEnergy() { return _energy; }
  int   		getHealth() { return _health; }
  int*  		getMessages() { return _messages; }
  bool			getSelected() { return _selected; }
  int			getFrameIndex() { return _frameIndex++; }
  int			getOffset() { return _offset; }

 private:
  void			actionUse();
  void			actionBuild();

  MapSearchNode*		_node;

  int			_posX;
  int			_posY;
  int			_toX;
  int			_toY;
  int			_id;
  int			_frameIndex;
  int			_gender;
  char			_name[32];
  AStarSearch<MapSearchNode>* _astarsearch;
  int			_steps;
  BaseItem*		_item;
  BaseItem*		_build;
  Profession	_profession;
  bool			_selected;
  int			_blocked;
  int			_direction;
  int			_offset;

  // Needs
  int   		_food;
  int   		_oxygen;
  float 		_hapiness;
  int			_health;
  int			_energy;

  // States
  int			_sleep;
  int			_eat;
  int			_drink;

  int			_goal;
  bool			_resolvePath;

  int			_messages[CHARACTER_MAX_MESSAGE];
};

#endif
