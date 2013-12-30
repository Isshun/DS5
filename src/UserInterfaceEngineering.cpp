#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterfaceEngineering.h"
#include "BaseItem.h"
#include "ResourceManager.h"

#define UIRES_POSX		UI_WIDTH
#define UIRES_POSY		0

#define	FONT_SIZE		16
#define LINE_HEIGHT		24
#define TITLE_SIZE		FONT_SIZE + 8

#define MENU_TILE_OPEN_WIDTH	300
#define MENU_TILE_OPEN_HEIGHT	160

UserInterfaceEngineering::UserInterfaceEngineering(sf::RenderWindow* app) {
  _app = app;
  _tileOpened = false;

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceEngineering::~UserInterfaceEngineering() {
}

void	UserInterfaceEngineering::drawTile(int index) {

  int posX = MENU_TILE_WIDTH * index;

  // Background
  sf::RectangleShape shape;
  if (_tileOpened) {
	shape.setSize(sf::Vector2f(MENU_TILE_OPEN_WIDTH, MENU_TILE_OPEN_HEIGHT));
  } else {
	shape.setSize(sf::Vector2f(MENU_TILE_WIDTH, MENU_TILE_HEIGHT));
  }
  shape.setFillColor(sf::Color(0, 100, 100));
  shape.setPosition(posX, 0);
  _app->draw(shape);

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
  _tileOpened = true;
}

void	UserInterfaceEngineering::closeTile() {
  _tileOpened = false;
}

void	UserInterfaceEngineering::toogleTile() {
  _tileOpened = !_tileOpened;
}

void	UserInterfaceEngineering::open() {

}

void	UserInterfaceEngineering::close() {

}
