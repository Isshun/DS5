/*
 * SpriteManager.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include "SpriteManager.h"
#include "BaseItem.h"
#include "defines.h"

SpriteResource	spritesRes[] = {
  {BaseItem::STRUCTURE_HULL,	2, 8, 0},
  {BaseItem::STRUCTURE_WALL,	2, 8, 0},
  {BaseItem::STRUCTURE_FLOOR,	7, 7, 0},
  {BaseItem::NONE,				0, 0, 0},
};

SpriteManager::SpriteManager() {
  _texture = new sf::Texture();
  _texture->loadFromFile("res/Tilesets/Futuristic_A5.png");
  _texture->setSmooth(true);
}

SpriteManager::~SpriteManager() {
  delete _texture;
}

sf::Sprite*		SpriteManager::getSprite(int type) {
  sf::Sprite* sprite = new sf::Sprite();
  sprite->setTexture(*_texture);

  for (int i = 0; spritesRes[i].type != BaseItem::NONE; i++) {
	if (spritesRes[i].type == type) {
	  sprite->setTextureRect(sf::IntRect(spritesRes[i].posX * TILE_SIZE,
										 spritesRes[i].posY * TILE_SIZE,
										 TILE_SIZE,
										 TILE_SIZE));
	  return sprite;
	}
  }

  sprite->setTextureRect(sf::IntRect(0, 0, TILE_SIZE, TILE_SIZE));

  return sprite;
}
