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
#include "JobManager.h"
#include "Job.h"
#include "CharacterNeeds.h"

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
	PROFESSION_OPERATION,
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

  // Actions
  void				action();
  void				move();
  void				update();
  void				updateNeeds(int count);
  void				draw(sf::RenderWindow* app, sf::Transform transform);
  void				sendEvent(int event);
  void				addMessage(int msg, int count);
  void				removeMessage(int msg);

  virtual void		create();
  virtual void		load(const char* filePath);
  virtual void		save(const char* filePath);

  virtual void		onPathSearch(Path* path, Job* job);
  virtual void		onPathComplete(Path* path, Job* job);
  virtual void		onPathFailed(Job* job);
  
  // Sets
  void				setProfession(int professionId);
  void				setProfession(Profession profession) { _profession = profession; }
  void				setDirection(int direction);
  void				setRun(int direction, bool run);
  void				setDosition(int x, int y);
  void				setSelected(bool selected) { _selected = selected; }
  void				setName(const char* name) { strcpy(_name, name); }
  void				setOffset(int offset) { _offset = offset; }
  void				setJob(Job* job);

  // Gets
  sf::Vector2<int>	&get_position();
  sf::Sprite		&get_sprite();
  int				getRun();
  int				getDirection() { return _direction; }
  Profession		getProfession() { return _profession; }
  int				getProfessionId() { return _profession.id; }
  Job*				getJob() { return _job; }
  int				getX() { return _posX; }
  int				getY() { return _posY; }
  int				getId() { return _id; }
  const char*		getName() { return _name; }
  CharacterNeeds*	getNeeds() { return _needs; }
  int*				getMessages() { return _messages; }
  bool				getSelected() { return _selected; }
  int				getFrameIndex() { return _frameIndex++; }
  int				getOffset() { return _offset; }
  int				getProfessionScore(int professionId) { return 42; }

 private:
  void				actionUse();
  void				actionBuild();
  void				actionGather();

  MapSearchNode*	_node;
  CharacterNeeds*	_needs;
  int				_posX;
  int				_posY;
  int				_toX;
  int				_toY;
  int				_id;
  int				_frameIndex;
  int				_gender;
  char				_name[32];
  Path*				_path;
  int				_steps;
  Profession		_profession;
  bool				_selected;
  int				_blocked;
  int				_direction;
  int				_offset;
  Job*				_job;

  bool				_resolvePath;

  int				_messages[CHARACTER_MAX_MESSAGE];
};

#endif
