/*
 * Cursor.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef CURSOR_H_
#define CURSOR_H_

class Cursor {
public:
	Cursor();
	virtual ~Cursor();

	void setPos(int x, int y) { _x = x; _y = y; }

	int	_x;
	int	_y;
};

#endif /* CURSOR_H_ */
