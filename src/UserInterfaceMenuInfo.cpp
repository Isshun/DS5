#include <sstream>
#include "UserInterfaceMenuInfo.h"

#define MENU_AREA_FONT_SIZE 20
#define MENU_AREA_MESSAGE_FONT_SIZE 16

#define MENU_PADDING_TOP 34
#define MENU_PADDING_LEFT 16

UserInterfaceMenuInfo::UserInterfaceMenuInfo(sf::RenderWindow* app) {
  _app = app;
  _area = NULL;

  _backgroundTexture.loadFromFile("../res/menu1.png");

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceMenuInfo::~UserInterfaceMenuInfo() {

}

void	UserInterfaceMenuInfo::refresh(int frame) {

  std::ostringstream oss;
  sf::Transform			transform;
  transform.translate(WINDOW_WIDTH - 380 - 64, 250);
  sf::RenderStates		render(transform);

  // Background
  Debug() << "Game background";
  sf::Sprite background;
  background.setTexture(_backgroundTexture);
  background.setTextureRect(sf::IntRect(0, 0, 380, 420));
  _app->draw(background, render);

  if (_area != NULL) {

    // Name
	const char* name = _area->getName();
	if (name != NULL) {
	  sf::Text text;
	  // oss << _area->getType();
	  text.setString(name);
	  text.setFont(_font);
	  text.setCharacterSize(MENU_AREA_FONT_SIZE);
	  text.setStyle(sf::Text::Regular);
	  text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP);
	  _app->draw(text, render);
	}

    // Oxygen
	{
	  sf::Text text;
	  oss.str("");
	  oss << "Oxygen: " << _area->getOxygen();
	  text.setString(oss.str());
	  text.setFont(_font);
	  text.setCharacterSize(MENU_AREA_FONT_SIZE);
	  text.setStyle(sf::Text::Regular);
	  text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP + 32);
	  _app->draw(text, render);
	}
  }
}
