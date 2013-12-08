/*
 * SpriteManager.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include "SpriteManager.h"
#include "BaseItem.h"

SpriteManager::SpriteManager() {
  _texture = new sf::Texture();
  _texture->loadFromFile("sprites/house_in_1.png");
  _texture->setSmooth(true);
}

SpriteManager::~SpriteManager() {
  delete _texture;
}

sf::Sprite*		SpriteManager::getSprite(int type) {
  sf::Sprite* sprite = new sf::Sprite();
  sprite->setTexture(*_texture);

  switch (type) {
  case BaseItem::HULL:
	sprite->setTextureRect(sf::IntRect(32, 32, 30, 30));
	break;
  case BaseItem::WALL:
	sprite->setTextureRect(sf::IntRect(32, 32, 30, 30));
	break;
  case BaseItem::FLOOR:
	sprite->setTextureRect(sf::IntRect(96, 32, 30, 30));
	break;
  default:
	sprite->setTextureRect(sf::IntRect(0, 0, 30, 30));
	break;
  }

   return sprite;
}
