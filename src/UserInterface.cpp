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

UserInterface::UserInterface(sf::RenderWindow* app, WorldMap* worldMap, Viewport* viewport) {
  _app = app;
  _viewport = viewport;
  _worldMap = worldMap;
  _cursor = new Cursor();
  _keyLeftPressed = false;
  _keyRightPressed = false;
  _zoom = 1.0f;
  _menu = new UserInterfaceMenu(app, _worldMap, _cursor);
  _uiResource = new UserInterfaceResource(app);
}

UserInterface::~UserInterface() {
  delete _menu;
  delete _uiResource;
}

void	UserInterface::mouseMoved(int x, int y) {
  if (x <= UI_WIDTH || y <= UI_HEIGHT)
	return;

  setRelativeMousePos(x, y);

  // left button pressed
  if (_keyLeftPressed) {
	setRelativeMousePos(x, y);
  }

  // right button pressed
  else if (_keyRightPressed) {
	setRelativeMousePos(x, y);
    _viewport->update(_mouseRightPress.x - x, _mouseRightPress.y - y);
	_mouseRightPress.x = x;
	_mouseRightPress.y = y;
  }

  // no buttons pressed
  else {
	setRelativeMousePos(x, y);
	_cursor->setMousePos(x - UI_WIDTH - _viewport->getPosX() - 1,
                         y - UI_HEIGHT - _viewport->getPosY() - 1);
  }
}

void	UserInterface::mousePress(sf::Mouse::Button button, int x, int y) {
  if (x < UI_WIDTH) {
    _menu->mousePressed(button, x, y);
  } else if (y < UI_HEIGHT) {
  } else {
    switch (button) {

    case sf::Mouse::Left:
      _keyLeftPressed = true;
      _keyMovePosX = _keyPressPosX = (x - UI_WIDTH - _viewport->getPosX()) / TILE_SIZE;
      _keyMovePosY = _keyPressPosY = (y - UI_HEIGHT - _viewport->getPosY()) / TILE_SIZE;
      break;

    case sf::Mouse::Right:
	  _keyRightPressed = true;
	  _mouseRightPress.x = x;
	  _mouseRightPress.y = y;
      break;
    }
  }
}

void	UserInterface::mouseRelease(sf::Mouse::Button button, int x, int y) {
  switch (button) {

  case sf::Mouse::Left:
    if (_keyLeftPressed) {
      int startX = std::min(_keyPressPosX, _keyMovePosX);
      int startY = std::min(_keyPressPosY, _keyMovePosY);
      int toX = startX;
      int toY = startY;
      if (_menu->getParentCode() == UserInterfaceMenu::CODE_BUILD_STRUCTURE) {
        toX = std::max(_keyPressPosX, _keyMovePosX);
        toY = std::max(_keyPressPosY, _keyMovePosY);
      }

      for (int x = startX; x <= toX; x++) {
        for (int y = startY; y <= toY; y++) {
          if (_menu->getCode() == UserInterfaceMenu::CODE_BUILD_ITEM) {
            if (_menu->getBuildItemType() == BaseItem::STRUCTURE_ROOM) {
              if (x == startX || x == toX || y == startY || y == toY) {
                _worldMap->putItem(x, y, BaseItem::STRUCTURE_WALL);
              } else {
                _worldMap->putItem(x, y, BaseItem::STRUCTURE_FLOOR);
              }
            } else {
              _worldMap->putItem(x, y, _menu->getBuildItemType());
            }
          }
        }
      }

      _keyLeftPressed = false;
    }

  case sf::Mouse::Right:
	if (_keyRightPressed) {
	  _keyRightPressed = false;
	  _viewport->update(_mouseRightPress.x - x, _mouseRightPress.y - y);
	}
    break;

  }
}

void	UserInterface::mouseWheel(int delta, int x, int y) {
  _viewport->setScale(delta);
  setRelativeMousePos(x, y);
}

void	UserInterface::drawCursor(int startX, int startY, int toX, int toY) {
  sf::Texture texture;
  texture.loadFromFile("../sprites/cursor.png");

  sf::Sprite sprite;
  sprite.setTexture(texture);
  sprite.setTextureRect(sf::IntRect(0, 0, 32, 32));

  sf::Transform transform;
  transform.scale(_zoom, _zoom);
  sf::RenderStates render(transform);

  startX = max(startX, 0);
  startY = max(startY, 0);
  toX = min(toX, _worldMap->getWidth());
  toY = min(toY, _worldMap->getHeight());
  for (int x = startX; x <= toX; x++) {
	for (int y = startY; y <= toY; y++) {
      sf::Transform transform;
      sf::RenderStates render(_viewport->getViewTransform(transform));
	  sprite.setPosition(x * TILE_SIZE, y * TILE_SIZE);
	  _app->draw(sprite, render);
	}
  }
}

void	UserInterface::refreshCursor() {
  if (_menu->getCode() == UserInterfaceMenu::CODE_BUILD_ITEM) {

	// Structure: multiple 1x1 tile
	if (_keyLeftPressed && _menu->getParentCode() == UserInterfaceMenu::CODE_BUILD_STRUCTURE) {
	  drawCursor(std::min(_keyPressPosX, _keyMovePosX),
				 std::min(_keyPressPosY, _keyMovePosY),
				 std::max(_keyPressPosX, _keyMovePosX),
				 std::max(_keyPressPosY, _keyMovePosY));
	}
	// Single nxn tile: holding mouse button
	else if (_keyLeftPressed) {
	  ItemInfo itemInfo = BaseItem::getItemInfo(_menu->getBuildItemType());
	  drawCursor(std::min(_keyPressPosX, _keyMovePosX),
				 std::min(_keyPressPosY, _keyMovePosY),
				 std::min(_keyPressPosX, _keyMovePosX) + itemInfo.width - 1,
				 std::min(_keyPressPosY, _keyMovePosY) + itemInfo.height - 1);
	}

	// Single nxn tile: mouse hover
	else {
	  ItemInfo itemInfo = BaseItem::getItemInfo(_menu->getBuildItemType());
	  drawCursor(_keyMovePosX, _keyMovePosY, _keyMovePosX, _keyMovePosY);
	}
  }
}

void UserInterface::refresh() {
  _menu->refreshMenu();
  refreshCursor();
  _uiResource->refreshResources();
}

bool UserInterface::checkKeyboard(sf::Event	event, int frame, int lastInput, WorldMap* worldMap) {
  if (event.type == sf::Event::KeyReleased) {
    if (_menu->checkKeyboard(event.key.code)) {
      return true;
    }
  }


  switch (event.key.code)
    {

    case sf::Keyboard::D:
	  worldMap->dump();
      break;

    case sf::Keyboard::T:
	  worldMap->setZone(_cursor->_x, _cursor->_y, 0);
      break;

    case sf::Keyboard::Up:
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
        _viewport->update(0, MOVE_VIEW_OFFSET);
		lastInput = frame;
  		// _cursor->_y--;
	  }
      break;

    case sf::Keyboard::Down:
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
        _viewport->update(0, -MOVE_VIEW_OFFSET);
		lastInput = frame;
		// _cursor->_y++;
	  }
      break;

    case sf::Keyboard::Right:
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
        _viewport->update(-MOVE_VIEW_OFFSET, 0);
		lastInput = frame;
  		// _cursor->_x++;
	  }
      break;

    case sf::Keyboard::Left:
      if (frame > lastInput + KEY_REPEAT_INTERVAL && (event.type == sf::Event::KeyPressed)) {
        _viewport->update(MOVE_VIEW_OFFSET, 0);
		lastInput = frame;
		// _cursor->_x--;
	  }
      break;

	  // PutItem
    case sf::Keyboard::Return:
      if (event.type == sf::Event::KeyReleased) {
		if (_menu->getCode() == UserInterfaceMenu::CODE_BUILD_ITEM) {
		  worldMap->putItem(_cursor->_x, _cursor->_y, _menu->getBuildItemType());
		}
	  }
      break;

    case sf::Keyboard::BackSpace:
    case sf::Keyboard::Escape:
      if ((event.type == sf::Event::KeyReleased)) {
        _menu->openBack();
      }
      break;

    default:
      break;
    }

  return false;
}
