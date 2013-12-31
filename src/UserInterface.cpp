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
#include "Settings.h"

UserInterface::UserInterface(sf::RenderWindow* app, Viewport* viewport) {
  _app = app;
  _viewport = viewport;
  _cursor = new Cursor();
  _characteres = CharacterManager::getInstance();
  _keyLeftPressed = false;
  _keyRightPressed = false;
  _zoom = 1.0f;

  _menu = new UserInterfaceMenu(app, _cursor);
  _menu->init();

  _menuCharacter = new UserInterfaceMenuCharacter(app);
  _menuCharacter->init();

  _menuInfo = new UserInterfaceMenuInfo(app);
  _menuInfo->init();

  _uiEngeneering = new UserInterfaceEngineering(app, 3);
  _uiScience = new UserInterfaceScience(app, 2);
  _uiSecurity = new UserInterfaceSecurity(app, 4);
  _crewViewOpen = false;
  _uiCharacter = new UserInterfaceCrew(app, 0);
  _uiDebug = new UserInterfaceDebug(app, _cursor);
  _uiBase = new UserInterfaceMenuOperation(app, 1);
  _cursorTexture.loadFromFile("../sprites/cursor.png");
}

UserInterface::~UserInterface() {
  delete _menu;
  delete _menuCharacter;
  delete _uiSecurity;
  delete _uiScience;
  delete _uiEngeneering;
}

void	UserInterface::mouseMoved(int x, int y) {
  // if (x <= UI_WIDTH || y <= UI_HEIGHT)
  // 	return;

  if (_uiEngeneering->onMouseMove(x, y)) {
	return;
  }

  if (_uiCharacter->onMouseMove(x, y)) {
	return;
  }

  if (_uiSecurity->onMouseMove(x, y)) {
	return;
  }

  if (_uiScience->onMouseMove(x, y)) {
	return;
  }

  if (_uiBase->onMouseMove(x, y)) {
	return;
  }

  _keyMovePosX = getRelativePosX(x);
  _keyMovePosY = getRelativePosY(y);
  _cursor->setPos(_keyMovePosX, _keyMovePosY);
	// _cursor->setMousePos(x * _viewport->getScale() - UI_WIDTH - _viewport->getPosX() - 1,
    //                      y * _viewport->getScale() - UI_HEIGHT - _viewport->getPosY() - 1);

  // // left button pressed
  // if (_keyLeftPressed) {
  // }

  // right button pressed
  if (_keyRightPressed) {
    _viewport->update(_mouseRightPress.x - x, _mouseRightPress.y - y);
	_mouseRightPress.x = x;
	_mouseRightPress.y = y;
  }

  // no buttons pressed
  else {

	// _cursor->setMousePos(x * _viewport->getScale() - UI_WIDTH - _viewport->getPosX() - 1,
    //                      y * _viewport->getScale() - UI_HEIGHT - _viewport->getPosY() - 1);
  }
}

void	UserInterface::mousePress(sf::Mouse::Button button, int x, int y) {
  // if (x < UI_WIDTH) {
  //   _menu->mousePressed(button, x, y);
  // } else if (y < UI_HEIGHT) {
  // } else {

  if (_uiEngeneering->mousePress(button, x, y)) {
	return;
  }

  if (_uiCharacter->mousePress(button, x, y)) {
	return;
  }

  if (_uiScience->mousePress(button, x, y)) {
	return;
  }

  if (_uiSecurity->mousePress(button, x, y)) {
	return;
  }

  if (_uiBase->mousePress(button, x, y)) {
	return;
  }

  switch (button) {

  case sf::Mouse::Left:
	_keyLeftPressed = true;
	_keyMovePosX = _keyPressPosX = getRelativePosX(x);
	_keyMovePosY = _keyPressPosY = getRelativePosY(y);
	break;

  case sf::Mouse::Right:
	_keyRightPressed = true;
	_mouseRightPress.x = x;
	_mouseRightPress.y = y;
	break;
  }
  // }
}

void	UserInterface::mouseRelease(sf::Mouse::Button button, int x, int y) {

  if (_uiEngeneering->mouseRelease(button, x, y)) {
	_uiSecurity->close();
	_uiScience->close();
	_uiCharacter->close();
	_uiBase->close();
	return;
  }

  if (_uiCharacter->mouseRelease(button, x, y)) {
	_uiSecurity->close();
	_uiScience->close();
	_uiEngeneering->close();
	_uiBase->close();
	return;
  }

  if (_uiBase->mouseRelease(button, x, y)) {
	_uiSecurity->close();
	_uiScience->close();
	_uiCharacter->close();
	_uiEngeneering->close();
	return;
  }

  if (_uiScience->mouseRelease(button, x, y)) {
	_uiSecurity->close();
	_uiCharacter->close();
	_uiEngeneering->close();
	_uiBase->close();
	return;
  }

  if (_uiSecurity->mouseRelease(button, x, y)) {
	_uiScience->close();
	_uiCharacter->close();
	_uiEngeneering->close();
	_uiBase->close();
	return;
  }

  switch (button) {

  case sf::Mouse::Left:
    if (_keyLeftPressed) {
      int startX = std::min(_keyPressPosX, _keyMovePosX);
      int startY = std::min(_keyPressPosY, _keyMovePosY);
      int toX = std::max(_keyPressPosX, _keyMovePosX);
      int toY = std::max(_keyPressPosY, _keyMovePosY);

      _menuCharacter->setCharacter(NULL);

      // Select character
      if (_uiEngeneering->getBuildItemType() == -1 && _menu->getCode() == UserInterfaceMenu::CODE_MAIN) {
        Info() << "select character";
        Character* c = _characteres->getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
		if (c != NULL) {
		  _menuCharacter->setCharacter(c);
		} else {
		  WorldArea* a = WorldMap::getInstance()->getArea(getRelativePosX(x), getRelativePosY(y));
		  if (a != NULL) {
			if (_menuInfo->getArea() == a && _menuInfo->getItem() == NULL && a->getItem() != NULL) {
			  _menuInfo->setItem(a->getItem());
			} else {
			  _menuInfo->setArea(a);
			  _menuInfo->setItem(NULL);
			}
		  }
		}
      }

      // Build item
      else if (_uiEngeneering->getBuildItemType() != -1 || _menu->getCode() == UserInterfaceMenu::CODE_BUILD_ITEM) {
        for (int x = toX; x >= startX; x--) {
          for (int y = toY; y >= startY; y--) {

            // Structure
			BaseItem* item = NULL;
			int type = _uiEngeneering->getBuildItemType();
            if (type == BaseItem::STRUCTURE_ROOM) {
              if (x == startX || x == toX || y == startY || y == toY) {
				Warning() << "1";
				JobManager::getInstance()->build(BaseItem::STRUCTURE_WALL, x, y);
				// item = WorldMap::getInstance()->putItem(x, y, BaseItem::STRUCTURE_WALL);
              } else {
				Warning() << "2";
				JobManager::getInstance()->build(BaseItem::STRUCTURE_FLOOR, x, y);
				// item = WorldMap::getInstance()->putItem(x, y, BaseItem::STRUCTURE_FLOOR);
              }
            } else {
              // item = WorldMap::getInstance()->putItem(x, y, _menu->getBuildItemType());
			  if (type != -1) {
				Warning() << "3 " << type << " " << BaseItem::getItemName(type);
				JobManager::getInstance()->build(type, x, y);
				// item = WorldMap::getInstance()->putItem(x, y, type);
			  }
            }

			// if (item != NULL) {
			// }
          }
        }
      }

	  // TODO: job manager
      // Erase item
      else if (_menu->getCode() == UserInterfaceMenu::CODE_ERASE) {
        for (int x = startX; x <= toX; x++) {
          for (int y = startY; y <= toY; y++) {
			WorldMap::getInstance()->removeItem(x, y);
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

  _keyMovePosX = getRelativePosX(x);
  _keyMovePosY = getRelativePosY(y);
}

void	UserInterface::drawCursor(int startX, int startY, int toX, int toY) {
  sf::Sprite sprite;
  sprite.setTexture(_cursorTexture);
  sprite.setTextureRect(sf::IntRect(0, 0, 32, 32));

  startX = max(startX, 0);
  startY = max(startY, 0);
  toX = min(toX, WorldMap::getInstance()->getWidth());
  toY = min(toY, WorldMap::getInstance()->getHeight());
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
  if (_uiEngeneering->getBuildItemType() != -1 ||
	  _menu->getCode() == UserInterfaceMenu::CODE_BUILD_ITEM ||
	  _menu->getCode() == UserInterfaceMenu::CODE_ERASE) {

	// Structure: multiple 1x1 tile
	if (_keyLeftPressed && (_menu->getParentCode() == UserInterfaceMenu::CODE_BUILD_STRUCTURE || _menu->getCode() == UserInterfaceMenu::CODE_ERASE)) {
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

void UserInterface::refresh(int frame, long interval) {

  // Display character frame
  if (_menuCharacter->getCharacter() != NULL) {
    _menuCharacter->refresh(frame);
  }

  // Display info frame
  else if (_menuInfo->getArea() != NULL || _menuInfo->getItem() != NULL) {
    _menuInfo->refresh(frame);
  }

  // Display debug view
  if (Settings::getInstance()->isDebug()) {
  	_uiDebug->refresh(frame);
  	drawCursor(_keyMovePosX, _keyMovePosY, _keyMovePosX, _keyMovePosY);
  }

  refreshCursor();
  // _uiResource->refreshResources(frame, interval);

  _uiCharacter->draw(frame);
  _uiScience->draw(frame);
  _uiSecurity->draw(frame);
  _uiBase->draw(frame);
  _uiEngeneering->draw(frame);
}

bool UserInterface::checkKeyboard(sf::Event	event, int frame, int lastInput) {

  if (_menu->checkKeyboard(event.key.code, _keyMovePosX, _keyMovePosY)) {
	return true;
  }

  if (_uiEngeneering->checkKey(event.key.code)) {
	return true;
  }

  if (_uiCharacter->checkKey(event.key.code)) {
	return true;
  }

  if (_uiBase->checkKey(event.key.code)) {
	return true;
  }

  if (_uiSecurity->checkKey(event.key.code)) {
	return true;
  }

  if (_uiScience->checkKey(event.key.code)) {
	return true;
  }

  if (_uiEngeneering->getBuildItemType() != -1) {
	if (event.type == sf::Event::KeyReleased && event.key.code == sf::Keyboard::Escape) {
	  _uiEngeneering->setBuildItemType(-1);
	  return true;
	}
  }

  switch (event.key.code) {

  case sf::Keyboard::Tab:
	if ((event.type == sf::Event::KeyReleased)) {
	  if (_menuCharacter->getCharacter() != NULL) {
		_menuCharacter->setCharacter(_characteres->getNext(_menuCharacter->getCharacter()));
	  }
	}
	break;

  case sf::Keyboard::D:
	Settings::getInstance()->setDebug(!Settings::getInstance()->isDebug());
	// 	WorldMap::getInstance()->dump();
	break;

  case sf::Keyboard::C:
	_crewViewOpen = !_crewViewOpen;
	break;

  case sf::Keyboard::E:
	_uiEngeneering->open();
	break;

  case sf::Keyboard::O:
	_uiBase->toogleTile();
	break;

  case sf::Keyboard::J:
	_uiBase->toogleJobs();
	break;

  case sf::Keyboard::G: {
	Character* c = _menuCharacter->getCharacter();
	if (c != NULL) {
	  c->go(_cursor->getX(), _cursor->getY());
	}
	break;
  }

  case sf::Keyboard::I:
	WorldMap::getInstance()->dumpItems();
	break;

	// case sf::Keyboard::T:
	// 	WorldMap::getInstance()->setZone(_keyMovePosX, _keyMovePosY, 0);
	// 	break;

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
		BaseItem* item = WorldMap::getInstance()->putItem(_menu->getBuildItemType(), _keyMovePosX, _keyMovePosY);
		if (item != NULL) {
		  JobManager::getInstance()->build(item);
		}
	  }
	}
	break;

  // case sf::Keyboard::Escape:
  // 	if ((event.type == sf::Event::KeyReleased)) {
  // 	  _menu->openRoot();
  // 	  _menuCharacter->setCharacter(NULL);
  // 	  _menuInfo->setArea(NULL);
  // 	}
  // 	break;

  case sf::Keyboard::BackSpace:
	if ((event.type == sf::Event::KeyReleased)) {
	  _menu->openBack();
	}
	break;

  default:
	break;
  }

  return false;
}
