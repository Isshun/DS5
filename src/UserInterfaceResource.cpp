#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterfaceResource.h"
#include "BaseItem.h"
#include "ResourceManager.h"

#define UIRES_POSX		UI_WIDTH
#define UIRES_POSY		0

#define	FONT_SIZE		16
#define LINE_HEIGHT		24
#define TITLE_SIZE		FONT_SIZE + 8

#define MENU_COLOR		sf::Color(25, 50, 255)

UserInterfaceResource::UserInterfaceResource(sf::RenderWindow* app) {
  _app = app;

  _backgroundTexture.loadFromFile("../res/bg_tile.png");
  _background.setTexture(_backgroundTexture);
  _background.setTextureRect(sf::IntRect(0, 0, 240, 120));

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceResource::~UserInterfaceResource() {
}

void UserInterfaceResource::refreshResources(int frame, long interval) {

  // {
  // 	int matter = ResourceManager::getInstance().getMatter();
  //   std::ostringstream oss;
  //   oss << "Matter: " << matter;

  //   sf::Text text;
  //   text.setString(oss.str());
  //   text.setFont(_font);
  //   text.setCharacterSize(24);
  //   // text.setStyle(sf::Text::Underlined);

  // 	if (matter == 0)
  // 	  text.setColor(sf::Color(255, 0, 0));
  // 	else if (matter < 20)
  // 	  text.setColor(sf::Color(255, 255, 0));
  //   text.setPosition(UIRES_POSX + UI_PADDING + 0, UIRES_POSY + UI_PADDING + 0);
  //   _app->draw(text);
  // }

  // {
  //   std::ostringstream oss;
  //   oss << "Power: " << ResourceManager::getInstance().getPower();

  //   sf::Text text;
  //   text.setString(oss.str());
  //   text.setFont(_font);
  //   text.setCharacterSize(24);
  //   // text.setCharacterSize(UI_FONT_SIZE);
  //   // text.setStyle(sf::Text::Underlined);
  //   // text.setColor(sf::Color(255, 255, 0));
  //   text.setPosition(UIRES_POSX + UI_PADDING + 280 + 0, UIRES_POSY + UI_PADDING + 0);
  //   _app->draw(text);
  // }

  // {
  //   std::ostringstream oss;
  //   oss << "O2: " << ResourceManager::getInstance().getO2();

  //   sf::Text text;
  //   text.setString(oss.str());
  //   text.setFont(_font);
  //   text.setCharacterSize(24);
  //   text.setPosition(UIRES_POSX + UI_PADDING + 540 + 0, UIRES_POSY + UI_PADDING + 0);
  //   _app->draw(text);
  // }

  {
    std::ostringstream oss;
    oss << "FPS: " << (interval > 0 ? (int)(1000 / interval) : 1000);

    sf::Text text;
    text.setString(oss.str());
    text.setFont(_font);
    text.setCharacterSize(24);
    text.setPosition(UIRES_POSX + UI_PADDING + 800 + 0, UIRES_POSY + UI_PADDING + 0);
    _app->draw(text);
  }

}

void	UserInterfaceResource::drawTile(int index) {
  int posX = (MENU_TILE_WIDTH + UI_PADDING + UI_PADDING) * index + UI_PADDING;

  _background.setPosition(posX, UI_PADDING);
  _background.setColor(MENU_COLOR);
  _app->draw(_background);

  // // Background
  // sf::RectangleShape shape;
  // shape.setSize(sf::Vector2f(MENU_TILE_WIDTH, MENU_TILE_HEIGHT));
  // shape.setFillColor(sf::Color(0, 100, 0));
  // shape.setPosition(posX, UI_PADDING);
  // _app->draw(shape);

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

  {
    std::ostringstream oss;
    oss << "Power: " << ResourceManager::getInstance().getPower();

    text.setString(oss.str());
    text.setPosition(posX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING + LINE_HEIGHT);
    _app->draw(text);
  }

  {
    std::ostringstream oss;
    oss << "O2: " << ResourceManager::getInstance().getO2();

    text.setString(oss.str());
    text.setPosition(posX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING + LINE_HEIGHT * 2);
    _app->draw(text);
  }

  text.setString("Resources");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(posX + UI_PADDING, UI_PADDING);
  _app->draw(text);
  text.setString("R");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);
}
