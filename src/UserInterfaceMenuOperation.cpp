#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterfaceMenuOperation.h"
#include "BaseItem.h"
#include "ResourceManager.h"
#include "WorldMap.h"

#define UIRES_POSX		UI_WIDTH
#define UIRES_POSY		0

#define	FONT_SIZE		16
#define LINE_HEIGHT		24
#define TITLE_SIZE		FONT_SIZE + 8

#define MENU_COLOR		sf::Color(80, 255, 200)

UserInterfaceMenuOperation::UserInterfaceMenuOperation(sf::RenderWindow* app) {
  _app = app;

  _backgroundTexture.loadFromFile("../res/bg_tile.png");
  _background.setTexture(_backgroundTexture);
  _background.setTextureRect(sf::IntRect(0, 0, 240, 120));

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceMenuOperation::~UserInterfaceMenuOperation() {
}

void	UserInterfaceMenuOperation::drawTile(int index) {
  int posX = (MENU_TILE_WIDTH + UI_PADDING + UI_PADDING) * index + UI_PADDING;
  int posY = UI_PADDING;

  std::ostringstream oss;

  _background.setPosition(posX, UI_PADDING);
  _background.setColor(MENU_COLOR);
  _app->draw(_background);

  // // Background
  // sf::RectangleShape shape;
  // shape.setSize(sf::Vector2f(MENU_TILE_WIDTH, MENU_TILE_HEIGHT));
  // shape.setFillColor(sf::Color(0, 0, 100));
  // shape.setPosition(posX, posY);
  // _app->draw(shape);

  sf::Text text;
  text.setFont(_font);
  text.setCharacterSize(FONT_SIZE);

  {
    std::ostringstream oss;
    oss << "Power: " << ResourceManager::getInstance().getPower();

    text.setString(oss.str());
    text.setPosition(posX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING + LINE_HEIGHT * 0);
    _app->draw(text);
  }

  {
    std::ostringstream oss;
    oss << "O2: " << ResourceManager::getInstance().getO2();

    text.setString(oss.str());
    text.setPosition(posX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING + LINE_HEIGHT * 1);
    _app->draw(text);
  }

  text.setString("Operation");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(posX + UI_PADDING, UI_PADDING);
  _app->draw(text);
  text.setString("O");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);
}
