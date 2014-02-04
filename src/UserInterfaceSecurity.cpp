#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterfaceSecurity.h"
#include "BaseItem.h"
#include "ResourceManager.h"
#include "SpriteManager.h"

#define UIRES_POSX		UI_WIDTH
#define UIRES_POSY		0

#define	FONT_SIZE		16
#define LINE_HEIGHT		24
#define TITLE_SIZE		FONT_SIZE + 8

#define TILE_ACTIVE_COLOR	sf::Color(0, 0, 0)

UserInterfaceSecurity::UserInterfaceSecurity(sf::RenderWindow* app, int tileIndex)
  : UserInterfaceBase(app, tileIndex) {
  _textureTile.loadFromFile("../res/bg_tile_security.png");
  _texturePanel.loadFromFile("../res/bg_panel_security.png");
}

UserInterfaceSecurity::~UserInterfaceSecurity() {
}

void UserInterfaceSecurity::refreshSecuritys(int frame, long interval) {

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
    text.setFont(SpriteManager::getInstance()->getFont());
    text.setCharacterSize(24);
    text.setPosition(UIRES_POSX + UI_PADDING + 800 + 0, UIRES_POSY + UI_PADDING + 0);
    _app->draw(text);
  }

}

void	UserInterfaceSecurity::draw(int frame) {
  // if (_isOpen) {
  // 	drawPanel(frame);
  // }
  drawTile();
}

void	UserInterfaceSecurity::drawTile() {
  UserInterfaceBase::drawTile(TILE_ACTIVE_COLOR);

  sf::Text text;
  text.setFont(SpriteManager::getInstance()->getFont());
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
    text.setPosition(_posTileX + UI_PADDING,
					 _posTileY + TITLE_SIZE + UI_PADDING);
    _app->draw(text);
	text.setColor(sf::Color(255, 255, 255));
  }

  {
    std::ostringstream oss;
    oss << "Power: " << ResourceManager::getInstance().getPower();

    text.setString(oss.str());
    text.setPosition(_posTileX + UI_PADDING,
					 _posTileY + TITLE_SIZE + UI_PADDING + LINE_HEIGHT);
    _app->draw(text);
  }

  {
    std::ostringstream oss;
    oss << "O2: " << ResourceManager::getInstance().getO2();

    text.setString(oss.str());
    text.setPosition(_posTileX + UI_PADDING,
					 _posTileY + TITLE_SIZE + UI_PADDING + LINE_HEIGHT * 2);
    _app->draw(text);
  }

  text.setString("Security");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(_posTileX + UI_PADDING, _posTileY + UI_PADDING);
  _app->draw(text);
  text.setString("S");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);
}

bool	UserInterfaceSecurity::checkKey(sf::Keyboard::Key key) {
  UserInterfaceBase::checkKey(key);

  if (key == sf::Keyboard::S) {
	toogle();
	return true;
  }

  return false;
}
