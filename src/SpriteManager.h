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

#define NB_SPRITES_ROOM 9
#define NB_TEMPLATES 9

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

  void				getSprite(BaseItem* item, sf::Sprite* sprite);
  void				getSprite(int type, sf::Sprite* sprite);
  void				getFloor(int zone, int room, sf::Sprite* sprite);
  void				getWall(int special, sf::Sprite* sprite, int index);

 private:
  sf::Sprite*		_buf;
  sf::Texture*		_texture[NB_TEMPLATES];
  sf::Sprite*       _spriteBattery;
  sf::Sprite*       _spriteFloor[NB_SPRITES_ROOM];
};

#endif /* SPRITEMANAGER_H_ */
