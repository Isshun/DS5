/*
 * UserInterface.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include <iostream>
#include <stdio.h>
#include <string.h>
#include "UserInterface.h"
#include "BaseItem.h"

Entry	entries_main[] = {
  {UserInterface::CODE_BUILD,	"build",		"b",	sf::Keyboard::B},
  {UserInterface::CODE_ZONE,	"zone",			"z",	sf::Keyboard::Z},
  {UserInterface::CODE_ERASE,	"erase",		"e",	sf::Keyboard::E},
  {UserInterface::CODE_CREW,	"crew",			"c",	sf::Keyboard::C},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0}
};

Entry	entries_build[] = {
  {UserInterface::CODE_BUILD_FLOOR,	"floor",		"f",	sf::Keyboard::F},
  {UserInterface::CODE_BUILD_WALL,	"wall",			"w",	sf::Keyboard::W},
};

Entry	entries_zone[] = {
  {UserInterface::CODE_ZONE_ENGINE,	"engine",		"e",	sf::Keyboard::E},
  {UserInterface::CODE_ZONE_SICKBAY,"sickbay",		"s",	sf::Keyboard::S},
  {UserInterface::CODE_ZONE_QUARTER,"quarter",		"q",	sf::Keyboard::Q},
  {UserInterface::CODE_ZONE_BAR,	"bar",			"b",	sf::Keyboard::B},
  {UserInterface::CODE_NONE,	NULL,			NULL,	0}
};

UserInterface::UserInterface() {
  _cursor = new Cursor();
  _entries = entries_main;
  _mode = MODE_MAIN;
  _code = CODE_MAIN;
}

UserInterface::~UserInterface() {
}

void UserInterface::drawModeBuild(sf::RenderWindow* app) {
  sf::Font font;
  if (!font.loadFromFile("snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("snap/xolonium/Xolonium-Regular.otf").c_str());

  sf::Text shortcut;
  shortcut.setString(_buildItemText);
  shortcut.setFont(font);
  shortcut.setCharacterSize(UI_FONT_SIZE);
  shortcut.setStyle(sf::Text::Underlined);
  shortcut.setColor(sf::Color(255, 255, 0));
  shortcut.setPosition(UI_PADDING + 0, UI_PADDING + 0);
  app->draw(shortcut);
}

void UserInterface::draw(sf::RenderWindow* app) {

  sf::Font font;
  if (!font.loadFromFile("snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("snap/xolonium/Xolonium-Regular.otf").c_str());

  switch (_mode) {
  case MODE_BUILD:
	drawModeBuild(app);
	break;
  case MODE_ZONE:
	break;
  default:
	for (int i = 0; _entries[i].code != UserInterface::CODE_NONE; i++) {
	  sf::Text text;
	  text.setString(_entries[i].text);
	  text.setFont(font);
	  text.setCharacterSize(UI_FONT_SIZE);
	  text.setStyle(sf::Text::Regular);
	  text.setPosition(UI_PADDING + 0, UI_PADDING + i * UI_FONT_SIZE);
	  app->draw(text);

	  sf::Text shortcut;
	  shortcut.setString(_entries[i].shortcut);
	  shortcut.setFont(font);
	  shortcut.setCharacterSize(UI_FONT_SIZE);
	  shortcut.setStyle(sf::Text::Underlined);
	  shortcut.setColor(sf::Color(255, 255, 0));
	  shortcut.setPosition(UI_PADDING + 0, UI_PADDING + i * UI_FONT_SIZE);
	  app->draw(shortcut);
	}
	break;
  }

  // Cursor
  sf::Texture texture;
  texture.loadFromFile("sprites/cursor.png");
  sf::Sprite sprite;
  sprite.setTexture(texture);
  sprite.setTextureRect(sf::IntRect(0, 0, 32, 32));
  sprite.setPosition(UI_WIDTH + _cursor->_x * TILE_SIZE, UI_HEIGHT + _cursor->_y * TILE_SIZE);
  app->draw(sprite);
}

void UserInterface::setBuildItem(int code, const char* text) {
  switch(code) {
  case CODE_BUILD_WALL: _buildItemType = BaseItem::WALL; break;
  case CODE_BUILD_FLOOR: _buildItemType = BaseItem::FLOOR; break;
  }
  _buildItemText = text;
  _mode = UserInterface::MODE_BUILD;
}

bool UserInterface::checkKeyboard(sf::Event	event, int frame, int lastInput, WorldMap* worldMap) {
  if (!event.key.code)
	return false;

  if (event.type == sf::Event::KeyReleased) {
	for (int i = 0; _entries[i].code != UserInterface::CODE_NONE; i++) {
	  if (_entries[i].key == event.key.code) {

		switch (_code) {

		case CODE_BUILD:
		  _code = _entries[i].code;
		  setBuildItem(_entries[i].code, _entries[i].text);
		  break;

		case CODE_MAIN:
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
//      if ((event.type == sf::Event::KeyPressed))
//		_cursor->_y--;
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
		lastInput = frame;
  		_cursor->_y--;
	  }
      break;
    case sf::Keyboard::Down:
//      if ((event.type == sf::Event::KeyPressed))
//		_cursor->setRun(DOWN, true);
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
		lastInput = frame;
		_cursor->_y++;
	  }
      break;
    case sf::Keyboard::Right:
//      if ((event.type == sf::Event::KeyPressed))
//		_cursor->setRun(RIGHT, true);
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
		lastInput = frame;
  		_cursor->_x++;
	  }
      break;
    case sf::Keyboard::Left:
//      if ((event.type == sf::Event::KeyPressed))
//		_cursor->setRun(LEFT, true);
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
		lastInput = frame;
		_cursor->_x--;
	  }
      break;

	  // PutItem
    case sf::Keyboard::Return:
      if (event.type == sf::Event::KeyReleased) {
		if (_mode == MODE_BUILD) {
		  worldMap->putItem(_cursor->_x, _cursor->_y, _buildItemType);
		}
	  }
      break;

    case sf::Keyboard::BackSpace:
    case sf::Keyboard::Escape:
      if ((event.type == sf::Event::KeyReleased)) {
		switch(_mode) {
		case MODE_BUILD: _entries = entries_build; _code = CODE_BUILD; _mode = MODE_MAIN; break;
		default: _entries = entries_main; break;
		}
	  }
      break;

    default:
      break;
    }

  return true;
}
