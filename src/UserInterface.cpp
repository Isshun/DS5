/*
 * UserInterface.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterface.h"
#include "BaseItem.h"
#include "ResourceManager.h"

Entry	entries_main[] = {
  {UserInterface::CODE_BUILD,	"build",		"b",	sf::Keyboard::B,		0},
  {UserInterface::CODE_ZONE,	"zone",			"z",	sf::Keyboard::Z,		0},
  {UserInterface::CODE_ERASE,	"erase",		"e",	sf::Keyboard::E,		0},
  {UserInterface::CODE_CREW,	"crew",			"c",	sf::Keyboard::C,		0},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build[] = {
  {UserInterface::CODE_BUILD_STRUCTURE,	"structure",		"s",	sf::Keyboard::S,		0},
  {UserInterface::CODE_BUILD_SICKBAY,	"sickbay",			"si",	sf::Keyboard::I,		0},
  {UserInterface::CODE_BUILD_ENGINE,	"engine",			"e",	sf::Keyboard::E,		0},
  {UserInterface::CODE_BUILD_HOLODECK,	"holodeck",			"h",	sf::Keyboard::H,		0},
  {UserInterface::CODE_BUILD_ARBORETUM,	"arboretum",		"a",	sf::Keyboard::A,		0},
  {UserInterface::CODE_BUILD_GYMNASIUM,	"gymnasium",		"g",	sf::Keyboard::G,		0},
  // {UserInterface::CODE_BUILD_SCHOOL,	"school",			"s",	sf::Keyboard::W,		0},
  {UserInterface::CODE_BUILD_BAR,		"bar",				"b",	sf::Keyboard::B,		0},
  {UserInterface::CODE_BUILD_AMPHITHEATER,	"entertainment","en",	sf::Keyboard::N,		0},
  {UserInterface::CODE_BUILD_QUARTER,	"residence",		"r",	sf::Keyboard::R,		0},
  {UserInterface::CODE_BUILD_ENVIRONMENT,	"environment",	"env",	sf::Keyboard::V,		0},
  {UserInterface::CODE_BUILD_TRANSPORTATION,"transportation","t",	sf::Keyboard::T,		0},
  {UserInterface::CODE_BUILD_TACTICAL,	"defense",			"d",	sf::Keyboard::D,		0},
  {UserInterface::CODE_BUILD_SCIENCE,	"science",			"sc",	sf::Keyboard::C,		0},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_structure[] = {
  {UserInterface::CODE_BUILD_ITEM,	"floor",		"f",	sf::Keyboard::F,		BaseItem::STRUCTURE_FLOOR},
  {UserInterface::CODE_BUILD_ITEM,	"wall",			"w",	sf::Keyboard::W,		BaseItem::STRUCTURE_WALL},
  {UserInterface::CODE_BUILD_ITEM,	"hull",			"h",	sf::Keyboard::H,		BaseItem::STRUCTURE_HULL},
  {UserInterface::CODE_BUILD_ITEM,	"window",		"wi",	sf::Keyboard::I,		BaseItem::STRUCTURE_WINDOW},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_sickbay[] = {
  {UserInterface::CODE_BUILD_ITEM,	"biobed",	"b",	sf::Keyboard::B,		BaseItem::SICKBAY_BIOBED},
  {UserInterface::CODE_BUILD_ITEM,	"lab",		"l",	sf::Keyboard::I,		BaseItem::SICKBAY_LAB},
  {UserInterface::CODE_BUILD_ITEM,	"emergency shelters", "e",	sf::Keyboard::E,		BaseItem::SICKBAY_EMERGENCY_SHELTERS},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_engine[] = {
  {UserInterface::CODE_BUILD_ITEM,	"control center",	"c",	sf::Keyboard::C,	BaseItem::ENGINE_CONTROL_CENTER},
  {UserInterface::CODE_BUILD_ITEM,	"reaction chamber",	"r",	sf::Keyboard::R,	BaseItem::ENGINE_REACTION_CHAMBER},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_holodeck[] = {
  {UserInterface::CODE_BUILD_ITEM,	"hologrid",	"h",	sf::Keyboard::H,	BaseItem::HOLODECK_GRID},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_arboretum[] = {
  {UserInterface::CODE_BUILD_ITEM,	"tree 1",	"1",	sf::Keyboard::Num1,	BaseItem::ARBORETUM_TREE_1},
  {UserInterface::CODE_BUILD_ITEM,	"tree 2",	"2",	sf::Keyboard::Num2,	BaseItem::ARBORETUM_TREE_2},
  {UserInterface::CODE_BUILD_ITEM,	"tree 3",	"3",	sf::Keyboard::Num3,	BaseItem::ARBORETUM_TREE_3},
  {UserInterface::CODE_BUILD_ITEM,	"tree 4",	"4",	sf::Keyboard::Num4,	BaseItem::ARBORETUM_TREE_4},
  {UserInterface::CODE_BUILD_ITEM,	"tree 5",	"5",	sf::Keyboard::Num5,	BaseItem::ARBORETUM_TREE_5},
  {UserInterface::CODE_BUILD_ITEM,	"tree 6",	"6",	sf::Keyboard::Num6,	BaseItem::ARBORETUM_TREE_6},
  {UserInterface::CODE_BUILD_ITEM,	"tree 7",	"7",	sf::Keyboard::Num7,	BaseItem::ARBORETUM_TREE_7},
  {UserInterface::CODE_BUILD_ITEM,	"tree 8",	"8",	sf::Keyboard::Num8,	BaseItem::ARBORETUM_TREE_8},
  {UserInterface::CODE_BUILD_ITEM,	"tree 9",	"9",	sf::Keyboard::Num9,	BaseItem::ARBORETUM_TREE_9},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_gymnasium[] = {
  {UserInterface::CODE_BUILD_ITEM,	"gymnasium stuff 1",	"1",	sf::Keyboard::Num1,	BaseItem::GYMNASIUM_STUFF_1},
  {UserInterface::CODE_BUILD_ITEM,	"gymnasium stuff 2",	"2",	sf::Keyboard::Num2,	BaseItem::GYMNASIUM_STUFF_2},
  {UserInterface::CODE_BUILD_ITEM,	"gymnasium stuff 3",	"3",	sf::Keyboard::Num3,	BaseItem::GYMNASIUM_STUFF_3},
  {UserInterface::CODE_BUILD_ITEM,	"gymnasium stuff 4",	"4",	sf::Keyboard::Num4,	BaseItem::GYMNASIUM_STUFF_4},
  {UserInterface::CODE_BUILD_ITEM,	"gymnasium stuff 5",	"5",	sf::Keyboard::Num5,	BaseItem::GYMNASIUM_STUFF_5},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_bar[] = {
  {UserInterface::CODE_BUILD_ITEM,	"pub",	"p",	sf::Keyboard::P,	BaseItem::BAR_PUB},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_amphitheater[] = {
  {UserInterface::CODE_BUILD_ITEM,	"stage",	"s",	sf::Keyboard::S,	BaseItem::AMPHITHEATER_STAGE},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_quarter[] = {
  {UserInterface::CODE_BUILD_ITEM,	"bed",	"b",			sf::Keyboard::B,	BaseItem::QUARTER_BED},
  {UserInterface::CODE_BUILD_ITEM,	"desk",	"d",			sf::Keyboard::D,	BaseItem::QUARTER_DESK},
  {UserInterface::CODE_BUILD_ITEM,	"chair",	"c",		sf::Keyboard::C,	BaseItem::QUARTER_CHAIR},
  {UserInterface::CODE_BUILD_ITEM,	"wardrobe",	"w",		sf::Keyboard::W,	BaseItem::QUARTER_WARDROBE},
  {UserInterface::CODE_BUILD_ITEM,	"chest of drawers",	"ch",sf::Keyboard::H,	BaseItem::QUARTER_CHEST},
  {UserInterface::CODE_BUILD_ITEM,	"bedside table", "be",	sf::Keyboard::E,	BaseItem::QUARTER_BEDSIDE_TABLE},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_environment[] = {
  {UserInterface::CODE_BUILD_ITEM,	"O2 recycler",	"O",	sf::Keyboard::O,	BaseItem::ENVIRONMENT_O2_RECYCLER},
  {UserInterface::CODE_BUILD_ITEM,	"temperature regulation",	"t",	sf::Keyboard::T,	BaseItem::ENVIRONMENT_TEMPERATURE_REGULATION},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_transportation[] = {
  {UserInterface::CODE_BUILD_ITEM,	"shuttlecraft",	"s",	sf::Keyboard::S,	BaseItem::TRANSPORTATION_SHUTTLECRAFT},
  {UserInterface::CODE_BUILD_ITEM,	"cargo",		"c",	sf::Keyboard::C,	BaseItem::TRANSPORTATION_CARGO},
  {UserInterface::CODE_BUILD_ITEM,	"container",	"co",	sf::Keyboard::C,	BaseItem::TRANSPORTATION_CONTAINER},
  {UserInterface::CODE_BUILD_ITEM,	"transporter systems",	"t",	sf::Keyboard::T,	BaseItem::TRANSPORTATION_TRANSPORTER_SYSTEMS},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_tactical[] = {
  {UserInterface::CODE_BUILD_ITEM,	"photon torpedo",	"photon t",	sf::Keyboard::T,	BaseItem::TACTICAL_PHOTON_TORPEDO},
  {UserInterface::CODE_BUILD_ITEM,	"phaser",			"p",		sf::Keyboard::P,	BaseItem::TACTICAL_PHASER},
  {UserInterface::CODE_BUILD_ITEM,	"shield grid",		"s",		sf::Keyboard::S,	BaseItem::TACTICAL_SHIELD_GRID},
  {UserInterface::CODE_BUILD_ITEM,	"cloaking device",	"c",		sf::Keyboard::C,	BaseItem::TACTICAL_CLOAKING_DEVICE},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_science[] = {
  {UserInterface::CODE_BUILD_ITEM,	"hydroponics",	"h",		sf::Keyboard::H,	BaseItem::SCIENCE_HYDROPONICS},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_zone[] = {
  {UserInterface::CODE_ZONE_ENGINE,	"engine",		"e",	sf::Keyboard::E,		0},
  {UserInterface::CODE_ZONE_SICKBAY,"sickbay",		"s",	sf::Keyboard::S,		0},
  {UserInterface::CODE_ZONE_QUARTER,"quarter",		"q",	sf::Keyboard::Q,		0},
  {UserInterface::CODE_ZONE_BAR,	"bar",			"b",	sf::Keyboard::B,		0},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0,		0}
};

UserInterface::UserInterface(sf::RenderWindow* app, WorldMap* worldMap) {
  _app = app;
  _worldMap = worldMap;
  _cursor = new Cursor();
  _entries = entries_main;
  _code = CODE_MAIN;
  _keyLeftPressed = false;
}

UserInterface::~UserInterface() {
}

void	UserInterface::mouseMoved(int x, int y) {
  if (x > UI_WIDTH && _keyLeftPressed) {
	_keyMovePosX = (x - UI_WIDTH) / TILE_SIZE;
	_keyMovePosY = (y - UI_HEIGHT) / TILE_SIZE;
  } else if (x > UI_WIDTH) {
	_cursor->setMousePos(x - UI_WIDTH, y - UI_HEIGHT);
  }
}

void	UserInterface::mousePress(int x, int y) {
  _keyLeftPressed = true;
  _keyMovePosX = _keyPressPosX = (x - UI_WIDTH) / TILE_SIZE;
  _keyMovePosY = _keyPressPosY = (y - UI_HEIGHT) / TILE_SIZE;
}

void	UserInterface::mouseRelease(int x, int y) {
  int startX = std::min(_keyPressPosX, _keyMovePosX);
  int startY = std::min(_keyPressPosY, _keyMovePosY);
  int toX = startX;
  int toY = startY;
  if (_parent_code == CODE_BUILD_STRUCTURE) {
	toX = std::max(_keyPressPosX, _keyMovePosX);
	toY = std::max(_keyPressPosY, _keyMovePosY);
  }

  for (int x = startX; x <= toX; x++) {
	for (int y = startY; y <= toY; y++) {
	  if (_code == CODE_BUILD_ITEM) {
		_worldMap->putItem(x, y, _buildItemType);
	  }
	}
  }

  _keyLeftPressed = false;
}

void UserInterface::drawModeBuild() {
  sf::Font font;
  if (!font.loadFromFile("snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("snap/xolonium/Xolonium-Regular.otf").c_str());

  sf::Text shortcut;
  shortcut.setString(_buildItemText);
  shortcut.setFont(font);
  shortcut.setCharacterSize(UI_FONT_SIZE);
  shortcut.setStyle(sf::Text::Underlined);
  shortcut.setColor(sf::Color(255, 255, 0));
  shortcut.setPosition(UI_PADDING + 0, UI_HEIGHT + UI_PADDING + 0);
  _app->draw(shortcut);
}

void	UserInterface::drawCursor(int startX, int startY, int toX, int toY) {
  sf::Texture texture;
  texture.loadFromFile("sprites/cursor.png");

  sf::Sprite sprite;
  sprite.setTexture(texture);
  sprite.setTextureRect(sf::IntRect(0, 0, 32, 32));

  for (int x = startX; x <= toX; x++) {
	for (int y = startY; y <= toY; y++) {
	  sprite.setPosition(UI_WIDTH + x * TILE_SIZE, UI_HEIGHT + y * TILE_SIZE);
	  _app->draw(sprite);
	}
  }
}

void	UserInterface::refreshMenu() {
  sf::Font font;
  if (!font.loadFromFile("snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("snap/xolonium/Xolonium-Regular.otf").c_str());

  switch (_code) {
  case CODE_BUILD_ITEM:
	drawModeBuild();
	break;
  case CODE_ZONE:
	break;
  default:
	for (int i = 0; _entries[i].code != UserInterface::CODE_NONE; i++) {
	  sf::Text text;
	  text.setString(_entries[i].text);
	  text.setFont(font);
	  text.setCharacterSize(UI_FONT_SIZE);
	  text.setStyle(sf::Text::Regular);
	  text.setPosition(UI_PADDING + 0, UI_HEIGHT + UI_PADDING + i * UI_FONT_SIZE);
	  _app->draw(text);

	  sf::Text shortcut;
	  shortcut.setString(_entries[i].shortcut);
	  shortcut.setFont(font);
	  shortcut.setCharacterSize(UI_FONT_SIZE);
	  shortcut.setStyle(sf::Text::Underlined);
	  shortcut.setColor(sf::Color(255, 255, 0));
	  shortcut.setPosition(UI_PADDING + 0, UI_HEIGHT + UI_PADDING + i * UI_FONT_SIZE);
	  _app->draw(shortcut);
	}
	break;
  }
}

void	UserInterface::refreshCursor() {
  if (_code == CODE_BUILD_ITEM) {

	// Structure: multiple 1x1 tile
	if (_keyLeftPressed && _parent_code == CODE_BUILD_STRUCTURE) {
	  drawCursor(std::min(_keyPressPosX, _keyMovePosX),
				 std::min(_keyPressPosY, _keyMovePosY),
				 std::max(_keyPressPosX, _keyMovePosX),
				 std::max(_keyPressPosY, _keyMovePosY));
	}
	// Single nxn tile: holding mouse button
	else if (_keyLeftPressed) {
	  ItemInfo itemInfo = BaseItem::getItemInfo(_buildItemType);
	  drawCursor(std::min(_keyPressPosX, _keyMovePosX),
				 std::min(_keyPressPosY, _keyMovePosY),
				 std::min(_keyPressPosX, _keyMovePosX) + itemInfo.width - 1,
				 std::min(_keyPressPosY, _keyMovePosY) + itemInfo.height - 1);
	}

	// Single nxn tile: mouse hover
	else {
	  ItemInfo itemInfo = BaseItem::getItemInfo(_buildItemType);
	  drawCursor(_cursor->_x,
				 _cursor->_y,
				 _cursor->_x + itemInfo.width - 1,
				 _cursor->_y + itemInfo.height - 1);
	}
  }
}

void UserInterface::refreshResources() {
  sf::Font font;
  if (!font.loadFromFile("snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("snap/xolonium/Xolonium-Regular.otf").c_str());

  {
	sf::Text text;
	std::ostringstream oss;
	oss << "Matter: " << ResourceManager::getInstance().getMatter();

	sf::Text shortcut;
	shortcut.setString(oss.str());
	shortcut.setFont(font);
	shortcut.setCharacterSize(UI_FONT_SIZE);
	shortcut.setStyle(sf::Text::Underlined);
	shortcut.setColor(sf::Color(255, 255, 0));
	shortcut.setPosition(UI_PADDING + 0, UI_PADDING + 0);
	_app->draw(shortcut);
  }

  {
	sf::Text text;
	std::ostringstream oss;
	oss << "Power: " << ResourceManager::getInstance().getPower();

	sf::Text shortcut;
	shortcut.setString(oss.str());
	shortcut.setFont(font);
	shortcut.setCharacterSize(UI_FONT_SIZE);
	shortcut.setStyle(sf::Text::Underlined);
	shortcut.setColor(sf::Color(255, 255, 0));
	shortcut.setPosition(UI_PADDING + 250 + 0, UI_PADDING + 0);
	_app->draw(shortcut);
  }

}

void UserInterface::refresh() {
  refreshMenu();
  refreshCursor();
  refreshResources();
}

void UserInterface::setBuildItem(int code, const char* text, int type) {
  std::cout << "setBuildItem: " << code << std::endl;

  _buildItemType = type;
  _buildItemText = text;
}

void UserInterface::setBuildMenu(int code) {
  std::cout << "setBuildMenu: " << code << std::endl;

  switch (code) {
  case CODE_BUILD_STRUCTURE:	_entries = entries_build_structure; break;
  case CODE_BUILD_SICKBAY:		_entries = entries_build_sickbay; break;
  case CODE_BUILD_ENGINE:		_entries = entries_build_engine; break;
  case CODE_BUILD_HOLODECK:		_entries = entries_build_holodeck; break;
  case CODE_BUILD_ARBORETUM:	_entries = entries_build_arboretum; break;
  case CODE_BUILD_GYMNASIUM:	_entries = entries_build_gymnasium; break;
  case CODE_BUILD_BAR:			_entries = entries_build_bar; break;
  case CODE_BUILD_AMPHITHEATER: _entries = entries_build_amphitheater; break;
  case CODE_BUILD_QUARTER:		_entries = entries_build_quarter; break;
  case CODE_BUILD_ENVIRONMENT:	_entries = entries_build_environment; break;
  case CODE_BUILD_TRANSPORTATION: _entries = entries_build_transportation; break;
  case CODE_BUILD_TACTICAL:		_entries = entries_build_tactical; break;
  case CODE_BUILD_SCIENCE:		_entries = entries_build_science; break;
  }
}

bool UserInterface::checkKeyboard(sf::Event	event, int frame, int lastInput, WorldMap* worldMap) {
  if (event.type == sf::Event::KeyReleased) {
	std::cout << "checkKeyboard: " << event.key.code << std::endl;

	for (int i = 0; _entries[i].code != UserInterface::CODE_NONE; i++) {
	  if (_entries[i].key == event.key.code) {

		switch (_code) {

		case CODE_BUILD:
		  _parent_code = _code;
		  _code = _entries[i].code;
		  setBuildMenu(_entries[i].code);
		  break;

		case CODE_BUILD_STRUCTURE:
		case CODE_BUILD_SICKBAY:
		case CODE_BUILD_ENGINE:
		case CODE_BUILD_HOLODECK:
		case CODE_BUILD_ARBORETUM:
		case CODE_BUILD_GYMNASIUM:
		case CODE_BUILD_BAR:
		case CODE_BUILD_AMPHITHEATER:
		case CODE_BUILD_QUARTER:
		case CODE_BUILD_ENVIRONMENT:
		case CODE_BUILD_TRANSPORTATION:
		case CODE_BUILD_TACTICAL:
		case CODE_BUILD_SCIENCE:
		  _parent_code = _code;
		  _code = _entries[i].code;
		  setBuildItem(_entries[i].code, _entries[i].text, _entries[i].data);
		  break;

		case CODE_MAIN:
		  _parent_code = _code;
		  _code = _entries[i].code;

		  if (_entries[i].code == CODE_BUILD)
			_entries = entries_build;

		  if (_entries[i].code == CODE_ZONE)
			_entries = entries_zone;
		}

		return true;
	  }
	}
  }


  switch (event.key.code)
    {
    case sf::Keyboard::Up:
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
		lastInput = frame;
  		_cursor->_y--;
	  }
      break;
    case sf::Keyboard::Down:
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
		lastInput = frame;
		_cursor->_y++;
	  }
      break;
    case sf::Keyboard::Right:
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
		lastInput = frame;
  		_cursor->_x++;
	  }
      break;
    case sf::Keyboard::Left:
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
		lastInput = frame;
		_cursor->_x--;
	  }
      break;

	  // PutItem
    case sf::Keyboard::Return:
      if (event.type == sf::Event::KeyReleased) {
		if (_code == CODE_BUILD_ITEM) {
		  worldMap->putItem(_cursor->_x, _cursor->_y, _buildItemType);
		}
	  }
      break;

    case sf::Keyboard::BackSpace:
    case sf::Keyboard::Escape:
      if ((event.type == sf::Event::KeyReleased)) {

		std::cout << "CODE BACK: " << _parent_code << std::endl;

		_code = _parent_code;

		switch(_code) {

		case CODE_BUILD_STRUCTURE:
		case CODE_BUILD_SICKBAY:
		case CODE_BUILD_ENGINE:
		case CODE_BUILD_HOLODECK:
		case CODE_BUILD_ARBORETUM:
		case CODE_BUILD_GYMNASIUM:
		case CODE_BUILD_BAR:
		case CODE_BUILD_AMPHITHEATER:
		case CODE_BUILD_QUARTER:
		case CODE_BUILD_ENVIRONMENT:
		case CODE_BUILD_TRANSPORTATION:
		case CODE_BUILD_TACTICAL:
		case CODE_BUILD_SCIENCE:
		  setBuildMenu(_code);
		  _parent_code = CODE_BUILD;
		  // _entries = entries_build;
		  // _code = CODE_BUILD;
		  // _mode = MODE_MAIN;
		  break;

		case CODE_BUILD:
		  _entries = entries_build;
		  _parent_code = CODE_MAIN;
		  break;

		case CODE_BUILD_ITEM:
		  break;
  
		default:
		  _entries = entries_main;
		  break;
		}
	  }
      break;

    default:
      break;
    }

  return true;
}
