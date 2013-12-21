#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterfaceMenuBase.h"
#include "BaseItem.h"
#include "ResourceManager.h"
#include "WorldMap.h"

#define UIRES_POSX		UI_WIDTH
#define UIRES_POSY		0

#define	FONT_SIZE		16
#define LINE_HEIGHT		24
#define TITLE_SIZE		FONT_SIZE + 8

UserInterfaceMenuBase::UserInterfaceMenuBase(sf::RenderWindow* app) {
  _app = app;

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceMenuBase::~UserInterfaceMenuBase() {
}

void	UserInterfaceMenuBase::drawTile(int index) {
  int posX = MENU_TILE_WIDTH * index;
  std::ostringstream oss;

  // Background
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(MENU_TILE_WIDTH, MENU_TILE_HEIGHT));
  shape.setFillColor(sf::Color(0, 0, 100));
  shape.setPosition(MENU_TILE_WIDTH * index, 0);
  _app->draw(shape);

  sf::Text text;
  text.setFont(_font);

  text.setString("Base");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(posX + UI_PADDING, UI_PADDING);
  _app->draw(text);

  text.setCharacterSize(FONT_SIZE);

  {
	int matter = ResourceManager::getInstance().getMatter();
    oss << "Rooms: " << WorldMap::getInstance()->getRoomCount();

	text.setString(oss.str());
    text.setPosition(posX + UI_PADDING, UI_PADDING + TITLE_SIZE + UI_PADDING);
    _app->draw(text);
  }

}
