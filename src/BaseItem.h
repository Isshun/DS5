/*
 * BaseItem.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef BASEITEM_H_
#define BASEITEM_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>

class BaseItem {
 public:
  BaseItem();
  ~BaseItem();

  enum { NONE, HULL, FLOOR };

  int			type;
  bool			isSolid;
};

#endif /* BASEITEM_H_ */
