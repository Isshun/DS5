#ifndef _C_ROOM_
#define _C_ROOM_

#include <list>
#include "defines.h"
#include "BaseItem.h"

class	Room
{
 public:
	Room();
	~Room();
	static Room*	createFromPos(int x, int y);
	static int		checkZone(int x, int y, int id);
	/* static int		setZone(int x, int y); */
	void			setId(int id) { _id = id; }
	static void		setZone(int x, int y, int id);

 private:
	int					_id;
	list<BaseItem*>*	_doors;
    static int			_roomCount;
	static int			_roomTmpId;
};

#endif
