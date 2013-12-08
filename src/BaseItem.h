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

	bool			isSolid;
	sf::Sprite*		sprite;
};

#endif /* BASEITEM_H_ */
