#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterfaceEngineering.h"
#include "BaseItem.h"
#include "ResourceManager.h"
#include "SpriteManager.h"

#define UIRES_POSX		UI_WIDTH
#define UIRES_POSY		0

#define	FONT_SIZE		16
#define LINE_HEIGHT		24
#define TITLE_SIZE		FONT_SIZE + 8

#define MENU_TILE_OPEN_WIDTH	300
#define MENU_TILE_OPEN_HEIGHT	160

UserInterfaceEngineering::UserInterfaceEngineering(sf::RenderWindow* app, int tileIndex, UserInteraction* interaction)
  : UserInterfaceBase(app, tileIndex) {
  _panelMode = MODE_NONE;
  _panelModeHover = MODE_NONE;
  _itemHover = -1;
  _interaction = interaction;

  _textureTile.loadFromFile("../res/bg_tile_engineering.png");
  _texturePanel.loadFromFile("../res/bg_panel_engineering.png");
}

UserInterfaceEngineering::~UserInterfaceEngineering() {
}

void	UserInterfaceEngineering::draw(int frame) {
  if (isOpen()) {
	drawPanel();
  }

  drawTile();
}

void	UserInterfaceEngineering::drawPanel() {
  UserInterfaceBase::drawPanel();

  sf::Text text;
  text.setFont(SpriteManager::getInstance()->getFont());

  // Header structure
  text.setString("Structure");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(_posX + UI_PADDING, _posY + UI_PADDING);
  if (_panelModeHover == MODE_STRUCTURE) {
	text.setStyle(sf::Text::Underlined);
	text.setColor(sf::Color(255, 255, 0));
	_app->draw(text);
  }
  text.setColor(sf::Color(255, 255, 255));
  text.setStyle(sf::Text::Regular);
  _app->draw(text);
  text.setString(_panelMode == MODE_STRUCTURE ? "Structure" : "S");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);

  // Header item
  text.setString("Items");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(_posX + 200 + UI_PADDING, _posY + UI_PADDING);
  if (_panelModeHover == MODE_ITEM) {
	text.setStyle(sf::Text::Underlined);
	text.setColor(sf::Color(255, 255, 0));
	_app->draw(text);
  }
  text.setColor(sf::Color(255, 255, 255));
  text.setStyle(sf::Text::Regular);
  _app->draw(text);
  text.setString(_panelMode == MODE_ITEM ? "Items" : "I");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);

  if (_panelMode == MODE_STRUCTURE) {
	for (int index = 0, i = BaseItem::STRUCTURE_START + 1; i < BaseItem::STRUCTURE_STOP; index++, i++) {
	  drawIcon(index, i);
	}
  } else if (_panelMode == MODE_ITEM) {
	for (int index = 0, i = BaseItem::ITEM_START + 1; i < BaseItem::ITEM_STOP; index++, i++) {
	  drawIcon(index, i);
	}
  }
}

void	UserInterfaceEngineering::drawIcon(int index, int type) {
  int posX = _posX + (index % 9) * 80;
  int posY = _posY + (int)(index / 9) * 100;

  // Background
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(62, 80));
  shape.setFillColor(_itemHover == type ? sf::Color(255, 255, 255) : sf::Color(236, 201, 37));
  shape.setPosition(posX + 20, posY + 60);
  _app->draw(shape);
  // shape.setSize(sf::Vector2f(54, 54));
  // shape.setFillColor(sf::Color(0, 80, 140));

  sf::Texture texture;
  texture.loadFromFile("../res/bg_none.png");
  texture.setRepeated(true);
  sf::Sprite sprite;
  sprite.setTexture(texture);
  sprite.setTextureRect(sf::IntRect(0, 0, 56, 56));
  sprite.setPosition(posX + 23, posY + 63);
  _app->draw(sprite);
	  
  // Icon
  sf::Sprite icon;
  SpriteManager::getInstance()->getSprite(type, &icon);
  icon.setPosition(posX + 23, posY + 63);
  _app->draw(icon);
	  
  // Name
  sf::Text text;
  text.setString(BaseItem::getItemName(type));
  text.setFont(SpriteManager::getInstance()->getFont());
  text.setCharacterSize(12);
  text.setColor(sf::Color(0, 0, 0));
  text.setStyle(sf::Text::Regular);
  text.setPosition(posX + 26, posY + 117);
  _app->draw(text);
}

void	UserInterfaceEngineering::drawTile() {
  UserInterfaceBase::drawTile(sf::Color(249, 195, 63));
 
  sf::Text text;
  text.setFont(SpriteManager::getInstance()->getFont());
  text.setCharacterSize(FONT_SIZE);

  {
	int matter = ResourceManager::getInstance()->getMatter();
    std::ostringstream oss;
    oss << "Matter: " << matter;

	text.setString(oss.str());

	if (matter == 0)
	  text.setColor(sf::Color(255, 0, 0));
	else if (matter < 20)
	  text.setColor(sf::Color(255, 255, 0));
    text.setPosition(_posTileX + UI_PADDING, _posTileY + TITLE_SIZE + UI_PADDING);
    _app->draw(text);
	text.setColor(sf::Color(255, 255, 255));
  }

  text.setString("Engineering");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(_posTileX + UI_PADDING, _posTileY + UI_PADDING);
  _app->draw(text);
  text.setString("E");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);
}

bool	UserInterfaceEngineering::checkKey(sf::Keyboard::Key key) {
  UserInterfaceBase::checkKey(key);

  if (isOpen()) {
	switch (key) {
	case sf::Keyboard::S:
	  _panelMode = MODE_STRUCTURE;
	  return true;
	case sf::Keyboard::I:
	  _panelMode = MODE_ITEM;
	  return true;
	case sf::Keyboard::E:
	  close();
	  return true;
	}
  }

  return false;
}

bool	UserInterfaceEngineering::onMouseMove(int x, int y) {
  _isTileActive = false;
  _panelModeHover = MODE_NONE;

  if (isOpen()) {
	_itemHover = -1;

	if (x > _posX && x < _posX + 800 && y > _posY && y < _posY + 600) {

	  // categories
	  if (y < _posY + 50) {
		_panelModeHover = x < _posX + 200 ? MODE_STRUCTURE : MODE_ITEM;
	  }

	  // items
	  else {
		int row = (y - _posY - 50) / 100;
		int col = (x - _posX - 10) / 80;
		int index = row * 9 + col;

		if (_panelMode == MODE_STRUCTURE) {
		  if (index + BaseItem::STRUCTURE_START + 1 < BaseItem::STRUCTURE_STOP) {
			_itemHover = index + BaseItem::STRUCTURE_START + 1;
		  }
		} else if (_panelMode == MODE_ITEM) {
		  if (index + BaseItem::ITEM_START + 1 < BaseItem::ITEM_STOP) {
			_itemHover = index + BaseItem::ITEM_START + 1;
		  }
		}
	  }
	  return true;
	}
  }

  else if (isOnTile(x, y)) {
	_isTileActive = true;
	return true;
  }

  return false;
}

bool	UserInterfaceEngineering::mousePress(sf::Mouse::Button button, int x, int y) {
  if (_isOpen) {
	return true;
  }
  else if (isOnTile(x, y)) {
	return true;
  }
  return false;
}

bool	UserInterfaceEngineering::mouseRelease(sf::Mouse::Button button, int x, int y) {

  // Panel open
  if (_isOpen && x > _posX && x < _posX + 800 && y > _posY && y < _posY + 600) {
	Info() << "UI Engineering: select item #" << _itemHover;

	if (y < _posY + 50) {
	  _panelMode = _panelModeHover;
	}

	if (_itemHover != -1) {
	  _interaction->selectBuildItem(_itemHover);
	  _isOpen = false;
	  onMouseMove(x, y);
	}

	return true;
  }

  // On tile
  else if (isOnTile(x, y)) {
	_isOpen = !_isOpen;
	_isTileActive = true;
	return true;
  }

  return false;
}
