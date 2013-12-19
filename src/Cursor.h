/*
 * Cursor.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef CURSOR_H_
#define CURSOR_H_

#include "defines.h"

class Cursor {
public:
	Cursor();
	virtual ~Cursor();

	void		setPos(int x, int y) { _x = x; _y = y; }
	void		setMousePos(int x, int y) { _x = x / TILE_SIZE; _y = y / TILE_SIZE; }
	int			getX() { return _x; }
	int			getY() { return _y; }

 private:
	int	_x;
	int	_y;
};

#endif /* CURSOR_H_ */
