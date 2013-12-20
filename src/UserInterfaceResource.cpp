#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterfaceResource.h"
#include "BaseItem.h"
#include "ResourceManager.h"

#define UIRES_POSX		UI_WIDTH
#define UIRES_POSY		0

UserInterfaceResource::UserInterfaceResource(sf::RenderWindow* app) {
  _app = app;

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceResource::~UserInterfaceResource() {
}

void UserInterfaceResource::refreshResources(int frame, long interval) {
  {
	int matter = ResourceManager::getInstance().getMatter();
    std::ostringstream oss;
    oss << "Matter: " << matter;

    sf::Text text;
    text.setString(oss.str());
    text.setFont(_font);
    text.setCharacterSize(24);
    // text.setStyle(sf::Text::Underlined);

	if (matter == 0)
	  text.setColor(sf::Color(255, 0, 0));
	else if (matter < 20)
	  text.setColor(sf::Color(255, 255, 0));
    text.setPosition(UIRES_POSX + UI_PADDING + 0, UIRES_POSY + UI_PADDING + 0);
    _app->draw(text);
  }

  {
    std::ostringstream oss;
    oss << "Power: " << ResourceManager::getInstance().getPower();

    sf::Text text;
    text.setString(oss.str());
    text.setFont(_font);
    text.setCharacterSize(24);
    // text.setCharacterSize(UI_FONT_SIZE);
    // text.setStyle(sf::Text::Underlined);
    // text.setColor(sf::Color(255, 255, 0));
    text.setPosition(UIRES_POSX + UI_PADDING + 280 + 0, UIRES_POSY + UI_PADDING + 0);
    _app->draw(text);
  }

  {
    std::ostringstream oss;
    oss << "O2: " << ResourceManager::getInstance().getO2();

    sf::Text text;
    text.setString(oss.str());
    text.setFont(_font);
    text.setCharacterSize(24);
    text.setPosition(UIRES_POSX + UI_PADDING + 540 + 0, UIRES_POSY + UI_PADDING + 0);
    _app->draw(text);
  }

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
