#include <sstream>

#include "UserInterfaceDebug.h"
#include "WorldMap.h"
#include "BaseItem.h"

UserInterfaceDebug::UserInterfaceDebug(sf::RenderWindow* app, Cursor* cursor) {
  _app = app;
  _cursor = cursor;

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceDebug::~UserInterfaceDebug() {
}

void  UserInterfaceDebug::addDebug(const char* key, std::string value) {
  int y = _index * 32;

  {
	sf::Text text;
	text.setFont(_font);
	text.setCharacterSize(20);
	text.setStyle(sf::Text::Regular);
	text.setString(key);
	text.setPosition(WINDOW_WIDTH - 320 + UI_PADDING,
					 UI_PADDING + y);
	_app->draw(text);
  }

  {
	sf::Text text;
	text.setFont(_font);
	text.setCharacterSize(20);
	text.setStyle(sf::Text::Regular);
	text.setString(value.c_str());
	text.setPosition(WINDOW_WIDTH - 320 + UI_PADDING + 160,
					 UI_PADDING + y);
	_app->draw(text);
  }

  _index++;
}

void	UserInterfaceDebug::refresh(int frame) {
  _index = 0;

  // Background
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(400, WINDOW_HEIGHT));
  shape.setFillColor(sf::Color(200, 200, 200, 200));
  shape.setPosition(sf::Vector2f(WINDOW_WIDTH - 320, 0));
  _app->draw(shape);

  int x = _cursor->getX();
  int y = _cursor->getY();
  BaseItem* item = WorldMap::getInstance()->getItem(x, y);
  std::ostringstream oss;

  Debug() << "pos: " << x << " x " << y;
  Debug() << "item: " << item;

  if (item != NULL) {

	oss << item->getType();
	addDebug("type", oss.str());

	oss.str("");
	oss << item->getX() << " x " << item->getY();
	addDebug("pos", oss.str());

	oss.str("");
	oss << item->getZoneIdRequired();
	addDebug("zone req.", oss.str());

	oss.str("");
	oss << item->getZoneId();
	addDebug("zone", oss.str());

	oss.str("");
	oss << item->getRoomId();
	addDebug("room", oss.str());

  }
}
