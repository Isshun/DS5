/*
 * SpriteManager.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef SPRITEMANAGER_H_
#define SPRITEMANAGER_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>

#include "BaseItem.h"

struct {
  int			type;
  int			posX;
  int			posY;
  int			textureIndex;
} typedef		SpriteResource;

class SpriteManager {
 public:

  enum {
	IC_BATTERY
  };

  SpriteManager();
  ~SpriteManager();

  sf::Sprite*		getSprite(BaseItem* item);
  sf::Sprite*		getSprite(int type);

 private:
  sf::Texture*		_texture[8];
};

#endif /* SPRITEMANAGER_H_ */
