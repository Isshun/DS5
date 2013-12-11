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
  {BaseItem::STRUCTURE_HULL,					2, 8, 0},
  {BaseItem::STRUCTURE_WALL,					2, 8, 0},
  {BaseItem::STRUCTURE_FLOOR,					7, 7, 0},
  {BaseItem::STRUCTURE_DOOR,					2, 2, 0},
  {BaseItem::STRUCTURE_WINDOW,					2, 3, 0},
  {BaseItem::TRANSPORTATION_TRANSPORTER_SYSTEMS,1, 8, 0},
  {BaseItem::ARBORETUM_TREE_1,					2, 0, 3},
  {BaseItem::ARBORETUM_TREE_2,					3, 0, 3},
  {BaseItem::ARBORETUM_TREE_3,					4, 0, 3},
  {BaseItem::ARBORETUM_TREE_4,					5, 0, 3},
  {BaseItem::ARBORETUM_TREE_5,					0, 0, 3},
  {BaseItem::ARBORETUM_TREE_6,					0, 1, 3},
  {BaseItem::ARBORETUM_TREE_7,					1, 0, 3},
  {BaseItem::ARBORETUM_TREE_8,					1, 1, 3},
  // {BaseItem::ARBORETUM_TREE_9,					0, 1, 3},
  {BaseItem::ENGINE_REACTION_CHAMBER,			12, 12, 1},
  {BaseItem::ENVIRONMENT_O2_RECYCLER,			11, 16, 1},
  {BaseItem::HOLODECK_GRID,						5, 6, 0},
  {BaseItem::ENGINE_CONTROL_CENTER,				8, 10, 1},
  {BaseItem::QUARTER_BED,						10, 7, 2},
  {BaseItem::QUARTER_CHAIR,						8, 6, 2},
  {BaseItem::NONE,								0, 0, 0},
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

  _texture[3] = new sf::Texture();
  _texture[3]->loadFromFile("../res/Tilesets/Futuristic_TileE.png");
  _texture[3]->setSmooth(true);

  // IC battery
  {
    sf::Texture texture;
    texture.loadFromFile("../sprites/battery.png");

    _spriteBattery = new sf::Sprite();
    _spriteBattery->setTexture(texture);
    _spriteBattery->setTextureRect(sf::IntRect(0, 0, 24, 24));
  }

  // Floors
  for (int i = 0; i < 9; i++) {
    _spriteFloor[i] = new sf::Sprite();
    _spriteFloor[i]->setTexture(*_texture[spritesRes[2].textureIndex]);
    _spriteFloor[i]->setTextureRect(sf::IntRect((i+1) * TILE_SIZE,
                                                0 * TILE_SIZE,
                                                TILE_SIZE,
                                                TILE_SIZE));
  }
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
		sf::Sprite* sprite;

		// Floor
		if (item->type == BaseItem::STRUCTURE_FLOOR) {
		  sprite = _spriteFloor[item->room];
		}

		// Else
		else {
		  sprite = new sf::Sprite();
		  sprite->setTexture(*_texture[spritesRes[i].textureIndex]);
		  sprite->setTextureRect(sf::IntRect(spritesRes[i].posX * TILE_SIZE,
											 spritesRes[i].posY * TILE_SIZE,
											 item->getWidth() * TILE_SIZE,
											 item->getHeight() * TILE_SIZE));
		}

		int alpha = 75 + 180 / item->matter * item->progress;
		sprite->setColor(sf::Color(255,255,255,alpha));

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
  switch (type) {

  case BaseItem::STRUCTURE_FLOOR:
    return _spriteFloor[0];

  case SpriteManager::IC_BATTERY:
    return _spriteBattery;
  }

  return NULL;
}
