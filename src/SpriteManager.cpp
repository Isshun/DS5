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
  {BaseItem::TRANSPORTATION_TRANSPORTER_SYSTEMS,	1, 8, 0},
  {BaseItem::HOLODECK_GRID,	5, 6, 0},
  {BaseItem::ENGINE_CONTROL_CENTER,	8, 10, 1},
  {BaseItem::QUARTER_BED,	10, 7, 2},
  {BaseItem::QUARTER_CHAIR,	8, 6, 2},
  {BaseItem::NONE,				0, 0, 0},
};

SpriteManager::SpriteManager() {
  _texture[0] = new sf::Texture();
  _texture[0]->loadFromFile("../res/Tilesets/Futuristic_A5.png");
  _texture[0]->setSmooth(true);

  _texture[1] = new sf::Texture();
  _texture[1]->loadFromFile("../res/Tilesets/Futuristic_TileC.png");
  _texture[1]->setSmooth(true);

  _texture[2] = new sf::Texture();
  _texture[2]->loadFromFile("../res/Tilesets/Futuristic_TileB.png");
  _texture[2]->setSmooth(true);
}

SpriteManager::~SpriteManager() {
  delete _texture[0];
  delete _texture[1];
  delete _texture[2];
}

sf::Sprite*		SpriteManager::getSprite(BaseItem* item) {
  if (item != NULL) {
	for (int i = 0; spritesRes[i].type != BaseItem::NONE; i++) {
	  if (spritesRes[i].type == item->type) {
		sf::Sprite* sprite = new sf::Sprite();
		int alpha = 75 + 180 / item->matter * item->progress;
		sprite->setColor(sf::Color(255,255,255,alpha));
		sprite->setTexture(*_texture[spritesRes[i].textureIndex]);
		sprite->setTextureRect(sf::IntRect(spritesRes[i].posX * TILE_SIZE,
										   spritesRes[i].posY * TILE_SIZE,
										   item->getWidth() * TILE_SIZE,
										   item->getHeight() * TILE_SIZE));
		return sprite;
	  }
	}
  }

  sf::Sprite* sprite = new sf::Sprite();
  sprite->setTexture(*_texture[0]);
  sprite->setTextureRect(sf::IntRect(0, 0, TILE_SIZE, TILE_SIZE));
  return sprite;
}

sf::Sprite*		SpriteManager::getSprite(int type) {
  sf::Texture* texture = new sf::Texture();
  texture->loadFromFile("../sprites/battery.png");

  sf::Sprite* sprite = new sf::Sprite();
  sprite->setTexture(*texture);
  sprite->setTextureRect(sf::IntRect(0, 0, 24, 24));

  return sprite;
}
