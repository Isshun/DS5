#include <sstream>
#include "Character.h"
#include "UserInterfaceMenuInfo.h"

#define MENU_AREA_FONT_SIZE 20
#define MENU_AREA_CONTENT_FONT_SIZE 16
#define MENU_AREA_MESSAGE_FONT_SIZE 16

#define MENU_PADDING_TOP 34
#define MENU_PADDING_LEFT 16

UserInterfaceMenuInfo::UserInterfaceMenuInfo(sf::RenderWindow* app) {
  _app = app;
  _area = NULL;
  _item = NULL;
}

UserInterfaceMenuInfo::~UserInterfaceMenuInfo() {

}

void	UserInterfaceMenuInfo::init() {
  _backgroundTexture.loadFromFile("../res/menu1.png");
  _background.setTexture(_backgroundTexture);
  _background.setTextureRect(sf::IntRect(0, 0, 380, 420));

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

void	UserInterfaceMenuInfo::addLine(sf::RenderStates render, const char* label, const char* value) {
  std::ostringstream oss;
  oss << label << ": " << value;
  addLine(render, oss.str().c_str());
}

void	UserInterfaceMenuInfo::addLine(sf::RenderStates render, const char* label, int value) {
  std::ostringstream oss;
  oss << label << ": " << value;
  addLine(render, oss.str().c_str());
}

void	UserInterfaceMenuInfo::addLine(sf::RenderStates render, const char* str) {
  sf::Text text;
  text.setString(str);
  text.setFont(_font);
  text.setCharacterSize(MENU_AREA_CONTENT_FONT_SIZE);
  text.setStyle(sf::Text::Regular);
  text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP + 32 + (_line++ * 24));
  _app->draw(text, render);
}

void	UserInterfaceMenuInfo::refresh(int frame) {

  BaseItem* item = _item != NULL ? _item : _area;

  sf::Transform			transform;
  transform.translate(WINDOW_WIDTH - 380 - 64, 250);
  sf::RenderStates		render(transform);

  // Background
  _app->draw(_background, render);

  if (item != NULL) {

    // Name
	const char* name = item->getName();
	if (name != NULL) {
	  sf::Text text;
	  // oss << item->getType();
	  text.setString(name);
	  text.setFont(_font);
	  text.setCharacterSize(MENU_AREA_FONT_SIZE);
	  text.setStyle(sf::Text::Regular);
	  text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP);
	  _app->draw(text, render);
	}

	_line = 0;
	std::ostringstream oss;
	oss << "Pos: " << item->getX() << " x " << item->getY();
	addLine(render, oss.str().c_str());
	addLine(render, "Oxygen", _area->getOxygen());
	addLine(render, "Owner", item->getOwner() != NULL ? item->getOwner()->getName() : "NULL");
	// addLine(render, "ItemInfo", item->getItemInfo());
	addLine(render, "Width", item->getWidth());
	addLine(render, "Height", item->getHeight());
	addLine(render, "Type", item->getType());
	addLine(render, "ZoneId", item->getZoneId());
	addLine(render, "ZoneIdRequired", item->getZoneIdRequired());
	addLine(render, "RoomId", item->getRoomId());
	addLine(render, "Id", item->getId());
	oss.str("");
	oss << item->matter << " (supply: " << item->_matterSupply << ")";
	addLine(render, "Matter", oss.str().c_str());
	oss.str("");
	oss << item->power << " (supply: " << item->powerSupply << ")";
	addLine(render, "Power", oss.str().c_str());
	addLine(render, "Solid", item->isSolid ? "True" : "False");
	addLine(render, "Free", item->isFree() ? "True" : "False");
	addLine(render, "SleepingItem", item->isSleepingItem() ? "True" : "False");
	addLine(render, "Structure", item->isStructure() ? "True" : "False");
  }
}
