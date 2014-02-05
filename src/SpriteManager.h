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
#include "WorldMap.h"

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

  static SpriteManager* getInstance();

  void				getSprite(BaseItem* item, sf::Sprite* sprite);
  void				getSprite(int type, sf::Sprite* sprite);
  void				getNoOxygen(sf::Sprite* sprite);
  void				getFloor(WorldArea* item, int zone, int room, sf::Sprite* sprite);
  void				getWall(BaseItem* item, int special, sf::Sprite* sprite, int index, int zone);
  void				getExterior(sf::Sprite* sprite);
  sf::Font&			getFont() { return _font; }
  void				getRessource(WorldArea* item, sf::Sprite* sprite);

 private:
  SpriteManager();
  ~SpriteManager();
  static SpriteManager* _self;
  sf::Sprite*		_buf;
  sf::Texture*		_texture[NB_TEMPLATES];
  sf::Sprite*       _spriteBattery;
  sf::Sprite*       _spriteFloor[NB_SPRITES_ROOM];
  sf::Font			_font;
};

#endif /* SPRITEMANAGER_H_ */
