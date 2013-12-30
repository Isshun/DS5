#ifndef _C_JOB_
#define _C_JOB_

#include <ctime>
#include <map>
#include <list>
#include "defines.h"
#include "BaseItem.h"

class	Character;

class	Job {
 public:
  Job(int id, int x, int y);
  ~Job();

  // Gets
  int			getX() { return _posX; }
  int			getY() { return _posY; }
  int			getId() { return _id; }
  int			getAction() { return _action; }
  int			getItemType() { return _itemType; }
  BaseItem*		getItem() { return _item; }
  Character*	getCharacter() { return _character; }

  // Sets
  void			setAction(int action) { _action = action; }
  void			setItemType(int itemType) { _itemType = itemType; }
  void			setItem(BaseItem* item) { _item = item; }
  void			setCharacter(Character* character) { _character = character; }

 private:
  int			_posX;
  int			_posY;
  int			_id;
  int			_action;
  int			_itemType;
  BaseItem*		_item;
  Character*	_character;
};

#endif
