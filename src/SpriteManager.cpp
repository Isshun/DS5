/*
 * SpriteManager.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include "SpriteManager.h"
#include "BaseItem.h"
#include "defines.h"
#include "UserInterfaceMenu.h"

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
  {BaseItem::BAR_PUB,							0, 8, 3},
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

  _texture[4] = new sf::Texture();
  _texture[4]->loadFromFile("../res/Tilesets/zones.png");
  _texture[4]->setSmooth(true);

  // IC battery
  {
    sf::Texture texture;
    texture.loadFromFile("../sprites/battery.png");

    _spriteBattery = new sf::Sprite();
    _spriteBattery->setTexture(texture);
    _spriteBattery->setTextureRect(sf::IntRect(0, 0, 24, 24));
  }
}

SpriteManager::~SpriteManager() {
  delete _texture[0];
  delete _texture[1];
  delete _texture[2];
  delete _texture[3];
  delete _texture[4];

  delete _spriteBattery;
}

void		SpriteManager::getSprite(BaseItem* item, sf::Sprite* sprite) {

  if (item != NULL) {
	for (int i = 0; spritesRes[i].type != BaseItem::NONE; i++) {
	  if (spritesRes[i].type == item->type) {

		// Floor
		if (item->type == BaseItem::STRUCTURE_FLOOR) {
		  int choice = 1;

		  if (item->zone == UserInterfaceMenu::CODE_ZONE_HOLODECK) {
			choice = 3;
		  }

		  sprite->setTexture(*_texture[4]);
		  sprite->setTextureRect(sf::IntRect((item->room % choice) * TILE_SIZE,
											 item->zone * TILE_SIZE,
											 TILE_SIZE,
											 TILE_SIZE));
		}

		// Else
		else {
		  sprite->setTexture(*_texture[spritesRes[i].textureIndex]);
		  sprite->setTextureRect(sf::IntRect(spritesRes[i].posX * TILE_SIZE,
											 spritesRes[i].posY * TILE_SIZE,
											 item->getWidth() * TILE_SIZE,
											 item->getHeight() * TILE_SIZE));
		}

		int alpha = 75 + 180 / item->matter * item->progress;
		sprite->setColor(sf::Color(255,255,255,alpha));

		return;
	  }
	}
  }

  sprite->setTexture(*_texture[0]);
  sprite->setTextureRect(sf::IntRect(0, 0, TILE_SIZE, TILE_SIZE));
}

void		SpriteManager::getSprite(int type, sf::Sprite* sprite) {
  switch (type) {

  case BaseItem::STRUCTURE_FLOOR:
    sprite = _spriteFloor[0];

  case SpriteManager::IC_BATTERY:
    sprite = _spriteBattery;
  }
}
