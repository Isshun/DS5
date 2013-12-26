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
  {BaseItem::STRUCTURE_WALL,					0, 4, 5},
  // {BaseItem::STRUCTURE_WALL_BELLOW,				1, 4, 5},
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
  {BaseItem::SICKBAY_BIOBED,					11, 4, 1},
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

  _texture[5] = new sf::Texture();
  _texture[5]->loadFromFile("../res/Tilesets/Futuristic_A3.png");
  _texture[5]->setSmooth(true);

  _texture[6] = new sf::Texture();
  _texture[6]->loadFromFile("../res/Tilesets/walls.png");
  _texture[6]->setSmooth(true);

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
  delete _texture[5];
  delete _texture[6];

  delete _spriteBattery;
}

void		SpriteManager::getSprite(BaseItem* item, sf::Sprite* sprite) {

  if (item != NULL) {
	for (int i = 0; spritesRes[i].type != BaseItem::NONE; i++) {
	  if (item->isType(spritesRes[i].type)) {

		// Floor
		if (item->isType(BaseItem::STRUCTURE_FLOOR)) {
		  int choice = 1;

		  if (item->getZoneId() == UserInterfaceMenu::CODE_ZONE_HOLODECK) {
			choice = 3;
		  }

		  sprite->setTexture(*_texture[4]);
		  sprite->setTextureRect(sf::IntRect((item->getRoomId() % choice) * TILE_SIZE,
											 item->getZoneId() * TILE_SIZE,
											 TILE_SIZE,
											 TILE_SIZE));
		}

		// Else
		else {
		  sprite->setScale(0.8f, 0.8f);
		  sprite->setTexture(*_texture[spritesRes[i].textureIndex]);
		  sprite->setTextureRect(sf::IntRect(spritesRes[i].posX * TILE_SIZE,
											 spritesRes[i].posY * TILE_SIZE,
											 item->getWidth() * TILE_SIZE,
											 item->getHeight() * TILE_SIZE));
		}

		int alpha = 75 + 180 / item->matter * item->_matterSupply;
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

void				SpriteManager::getExterior(sf::Sprite* sprite) {
  sprite->setTexture(*_texture[4]);
  sprite->setTextureRect(sf::IntRect(rand() % 8 * TILE_SIZE,
									 7 * (TILE_SIZE + 2) + 1,
									 TILE_SIZE,
									 TILE_SIZE));
}

void				SpriteManager::getFloor(WorldArea* item, int zone, int room, sf::Sprite* sprite) {
  int choice = 1;

  int alpha = 75 + 180 / item->matter * item->_matterSupply;
  sprite->setColor(sf::Color(255,255,255,alpha));

  if (zone == UserInterfaceMenu::CODE_ZONE_HOLODECK) {
	choice = 3;
  }

  sprite->setTexture(*_texture[4]);
  sprite->setTextureRect(sf::IntRect((room % choice) * TILE_SIZE,
									 zone * (TILE_SIZE + 2) + 1,
									 TILE_SIZE,
									 TILE_SIZE));
}

void				SpriteManager::getNoOxygen(sf::Sprite* sprite) {
  sprite->setTexture(*_texture[4]);
  sprite->setTextureRect(sf::IntRect(0,
									 8 * (TILE_SIZE + 2) + 1,
									 TILE_SIZE,
									 TILE_SIZE));
}

void				SpriteManager::getWall(BaseItem* item, int special, sf::Sprite* sprite, int index, int zone) {
  int WALL_HEIGHT = 48;
  int WALL_WIDTH = 32;

  // Door
  if (item->isType(BaseItem::STRUCTURE_DOOR)) {
		int alpha = 75 + 180 / item->matter * item->_matterSupply;
		sprite->setColor(sf::Color(255,255,255,alpha));
		sprite->setTexture(*_texture[6]);
		sprite->setTextureRect(sf::IntRect(WALL_WIDTH * special,
										   WALL_HEIGHT * 7,
										   WALL_WIDTH,
										   WALL_HEIGHT));
  }

  // Wall
  else {
	for (int i = 0; spritesRes[i].type != BaseItem::NONE; i++) {
	  if (spritesRes[i].type == BaseItem::STRUCTURE_WALL) {
		int alpha = 75 + 180 / item->matter * item->_matterSupply;
		sprite->setColor(sf::Color(255,255,255,alpha));

		sprite->setTexture(*_texture[6]);

		// Normal
		if (special == 0) {
		  sprite->setTextureRect(sf::IntRect(0,
											 WALL_HEIGHT * zone,
											 WALL_WIDTH,
											 WALL_HEIGHT));
		}

		// Bellow
		if (special == 1) {
		  sprite->setTextureRect(sf::IntRect(WALL_WIDTH,
											 WALL_HEIGHT * zone,
											 WALL_WIDTH,
											 WALL_HEIGHT));
		}

		// Double normal
		if (special == 4) {
		  sprite->setTextureRect(sf::IntRect(64,
											 WALL_HEIGHT * zone,
											 WALL_WIDTH * 2,
											 WALL_HEIGHT));
		}

		// Double special
		if (special == 2) {
		  sprite->setTextureRect(sf::IntRect(256 + 64 * (index % 4),
											 WALL_HEIGHT * zone,
											 WALL_WIDTH * 2,
											 WALL_HEIGHT));
		}

		// Single special
		if (special == 3) {
		  sprite->setTextureRect(sf::IntRect(128 + WALL_WIDTH * (index % 4),
											 WALL_HEIGHT * zone,
											 WALL_WIDTH,
											 WALL_HEIGHT));
		}

	  }
	}
  }
}
