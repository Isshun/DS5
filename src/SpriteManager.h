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

class SpriteManager {
 public:
  SpriteManager();
  ~SpriteManager();

  sf::Sprite*		getSprite(int type);

 private:
  sf::Texture*		_texture;
};

#endif /* SPRITEMANAGER_H_ */
