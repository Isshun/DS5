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
  _characteres = CharacterManager::getInstance();
  _keyLeftPressed = false;
  _keyRightPressed = false;
  _zoom = 1.0f;

  // _menu = new UserInterfaceMenu(app);
  // _menu->init();

  _menuCharacter = new UserInterfaceMenuCharacter(app);
  _menuCharacter->init();

  _menuInfo = new UserInterfaceMenuInfo(app);
  _menuInfo->init();

  _interaction = new UserInteraction(_viewport);

  _uiEngeneering = new UserInterfaceEngineering(app, 3, _interaction);
  _uiScience = new UserInterfaceScience(app, 2);
  _uiSecurity = new UserInterfaceSecurity(app, 4);
  _crewViewOpen = false;
  _uiCharacter = new UserInterfaceCrew(app, 0);
  _uiDebug = new UserInterfaceDebug(app);
  _uiBase = new UserInterfaceMenuOperation(app, 1);
}

UserInterface::~UserInterface() {
  delete _interaction;
  delete _menuCharacter;
  delete _menuInfo;
  delete _uiEngeneering;
  delete _uiScience;
  delete _uiSecurity;
  delete _uiCharacter;
  delete _uiDebug;
  delete _uiBase;
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
  _interaction->mouseMove(_keyMovePosX, _keyMovePosY);
  // _cursor->setPos(_keyMovePosX, _keyMovePosY);

  // right button pressed
  if (_keyRightPressed) {
    _viewport->update(x, y);
  }

  // no buttons pressed
  else {
	// _cursor->setMousePos(x * _viewport->getScale() - UI_WIDTH - _viewport->getPosX() - 1,
    //                      y * _viewport->getScale() - UI_HEIGHT - _viewport->getPosY() - 1);
  }
}

void	UserInterface::mousePress(sf::Mouse::Button button, int x, int y) {
  if (_uiEngeneering->mousePress(button, x, y)) {
	_keyLeftPressed = false;
	return;
  }

  if (_uiCharacter->mousePress(button, x, y)) {
	_keyLeftPressed = false;
	return;
  }

  if (_uiScience->mousePress(button, x, y)) {
	_keyLeftPressed = false;
	return;
  }

  if (_uiSecurity->mousePress(button, x, y)) {
	_keyLeftPressed = false;
	return;
  }

  if (_uiBase->mousePress(button, x, y)) {
	_keyLeftPressed = false;
	return;
  }

  _interaction->mousePress(button, getRelativePosX(x), getRelativePosY(y));
  _interaction->mouseMove(getRelativePosX(x), getRelativePosY(y));

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
    _viewport->startMove(x, y);
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

  if (_interaction->mouseRelease(button, x, y)) {
	return;
  }

  _interaction->cancel();

  switch (button) {

  case sf::Mouse::Left:
    if (true) {

      _menuCharacter->setCharacter(NULL);

      // Select character
      if (_interaction->getMode() == UserInteraction::MODE_NONE) {// && _menu->getCode() == UserInterfaceMenu::CODE_MAIN) {
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

      _keyLeftPressed = false;
    }

  case sf::Mouse::Right:
	if (_keyRightPressed) {
	  _keyRightPressed = false;

	  if (abs(_mouseRightPress.x - x) > 5 || abs(_mouseRightPress.y - y) > 5) {
		_viewport->update(x, y);
		// _viewport->update(_mouseRightPress.x - x, _mouseRightPress.y - y);
	  } else {
		_interaction->cancel();
	  }

	}
    break;

  }
}

void	UserInterface::mouseWheel(int delta, int x, int y) {
  _viewport->setScale(delta);

  _keyMovePosX = getRelativePosX(x);
  _keyMovePosY = getRelativePosY(y);
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
  	_uiDebug->refresh(frame, _interaction->getCursor()->getX(), _interaction->getCursor()->getY());
  	//drawCursor(_keyMovePosX, _keyMovePosY, _keyMovePosX, _keyMovePosY);
  }

  _interaction->refreshCursor();
  // _uiResource->refreshResources(frame, interval);

  _uiCharacter->draw(frame);
  _uiScience->draw(frame);
  _uiSecurity->draw(frame);
  _uiBase->draw(frame);
  _uiEngeneering->draw(frame);
}

bool UserInterface::checkKeyboard(sf::Event	event, int frame, int lastInput) {

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

  if (_interaction->getMode() != UserInteraction::MODE_NONE) {
	if (event.type == sf::Event::KeyReleased && event.key.code == sf::Keyboard::Escape) {
	  _interaction->cancel();
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

  // case sf::Keyboard::G: {
  // 	Character* c = _menuCharacter->getCharacter();
  // 	if (c != NULL) {
  // 	  c->go(_interaction->getCursor()->getX(), _interaction->getCursor()->getY());
  // 	}
  // 	break;
  // }

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

  // 	// PutItem
  // case sf::Keyboard::Return:
  // 	if (event.type == sf::Event::KeyReleased) {
  // 	  if (_menu->getCode() == UserInterfaceMenu::CODE_BUILD_ITEM) {
  // 		BaseItem* item = WorldMap::getInstance()->putItem(_menu->getBuildItemType(), _keyMovePosX, _keyMovePosY);
  // 		if (item != NULL) {
  // 		  JobManager::getInstance()->build(item);
  // 		}
  // 	  }
  // 	}
  // 	break;

  // case sf::Keyboard::BackSpace:
  // 	if ((event.type == sf::Event::KeyReleased)) {
  // 	  _menu->openBack();
  // 	}
  // 	break;

  default:
	break;
  }

  return false;
}
