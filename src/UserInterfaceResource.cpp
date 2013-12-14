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
}

UserInterfaceResource::~UserInterfaceResource() {
}

void UserInterfaceResource::refreshResources(int frame) {
  sf::Font font;
  if (!font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());

  {
	int matter = ResourceManager::getInstance().getMatter();
    std::ostringstream oss;
    oss << "Matter: " << matter;

    sf::Text text;
    text.setString(oss.str());
    text.setFont(font);
    // text.setCharacterSize(UI_FONT_SIZE);
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
    text.setFont(font);
    // text.setCharacterSize(UI_FONT_SIZE);
    // text.setStyle(sf::Text::Underlined);
    // text.setColor(sf::Color(255, 255, 0));
    text.setPosition(UIRES_POSX + UI_PADDING + 250 + 0, UIRES_POSY + UI_PADDING + 0);
    _app->draw(text);
  }

  {
    std::ostringstream oss;
    oss << "O2: " << ResourceManager::getInstance().getO2();

    sf::Text text;
    text.setString(oss.str());
    text.setFont(font);
    text.setPosition(UIRES_POSX + UI_PADDING + 500 + 0, UIRES_POSY + UI_PADDING + 0);
    _app->draw(text);
  }

}
