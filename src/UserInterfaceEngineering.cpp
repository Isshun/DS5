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

#define MENU_COLOR		sf::Color(255, 255, 0)

UserInterfaceEngineering::UserInterfaceEngineering(sf::RenderWindow* app) {
  _app = app;
  _tileActive = false;

  _posX = 200;
  _posY = 200;

  _isOpen = false;
  _panelMode = MODE_NONE;

  _textureTile.loadFromFile("../res/bg_tile.png");
  _bgTile.setTexture(_textureTile);
  _bgTile.setTextureRect(sf::IntRect(0, 0, 240, 120));

  _texturePanel.loadFromFile("../res/bg_panel.png");
  _bgPanel.setTexture(_texturePanel);
  _bgPanel.setTextureRect(sf::IntRect(0, 0, 800, 600));

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceEngineering::~UserInterfaceEngineering() {
}

void	UserInterfaceEngineering::draw(int index) {
  if (_isOpen) {
	drawPanel();
  }

  drawTile(index);
}

void	UserInterfaceEngineering::drawPanel() {
  _bgPanel.setPosition(_posX, _posY);
  _bgPanel.setColor(MENU_COLOR);
  _app->draw(_bgPanel);

  sf::Text text;
  text.setFont(_font);

  // Header structure
  text.setString("Structure");
  text.setCharacterSize(TITLE_SIZE);
  text.setColor(sf::Color(255, 255, 255));
  text.setStyle(sf::Text::Regular);
  text.setPosition(_posX + UI_PADDING, _posY + UI_PADDING);
  _app->draw(text);
  text.setString(_panelMode == MODE_STRUCTURE ? "Structure" : "S");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);

  // Header item
  text.setString("Items");
  text.setCharacterSize(TITLE_SIZE);
  text.setColor(sf::Color(255, 255, 255));
  text.setStyle(sf::Text::Regular);
  text.setPosition(_posX + 200 + UI_PADDING, _posY + UI_PADDING);
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

  sf::Text text;

  // Background
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(62, 80));
  shape.setFillColor(sf::Color(0, 50, 100));
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
  text.setString(BaseItem::getItemName(type));
  text.setFont(_font);
  text.setCharacterSize(16);
  text.setColor(sf::Color(255, 255, 255));
  text.setStyle(sf::Text::Regular);
  text.setPosition(posX + 26, posY + 117);
  _app->draw(text);
}

void	UserInterfaceEngineering::drawTile(int index) {

  int posX = (MENU_TILE_WIDTH + UI_PADDING + UI_PADDING) * index + UI_PADDING;

  _bgTile.setPosition(posX, UI_PADDING);
  _bgTile.setColor(MENU_COLOR);
  _app->draw(_bgTile);
  
  sf::Text text;
  text.setFont(_font);
  text.setCharacterSize(FONT_SIZE);

  {
	int matter = ResourceManager::getInstance().getMatter();
    std::ostringstream oss;
    oss << "Matter: " << matter;

	text.setString(oss.str());

	if (matter == 0)
	  text.setColor(sf::Color(255, 0, 0));
	else if (matter < 20)
	  text.setColor(sf::Color(255, 255, 0));
    text.setPosition(posX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING);
    _app->draw(text);
	text.setColor(sf::Color(255, 255, 255));
  }

  text.setString("Engineering");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(posX + UI_PADDING, UI_PADDING);
  _app->draw(text);
  text.setString("E");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);
}

void	UserInterfaceEngineering::openTile() {
  _tileActive = true;
}

void	UserInterfaceEngineering::closeTile() {
  _tileActive = false;
}

void	UserInterfaceEngineering::toogleTile() {
  _tileActive = !_tileActive;
}

void	UserInterfaceEngineering::toogle() {
  _isOpen = !_isOpen;
}

void	UserInterfaceEngineering::open() {
  _isOpen = true;
}

void	UserInterfaceEngineering::close() {
  _isOpen = false;
}

bool	UserInterfaceEngineering::checkKey(sf::Keyboard::Key key) {
  if (_isOpen) {
	switch (key) {
	case sf::Keyboard::S:
	  _panelMode = MODE_STRUCTURE;
	  return true;
	case sf::Keyboard::I:
	  _panelMode = MODE_ITEM;
	  return true;
	case sf::Keyboard::Escape:
	  _isOpen = false;
	  return true;
	}
  }

  return false;
}


