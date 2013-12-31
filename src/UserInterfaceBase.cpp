#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterfaceBase.h"
#include "BaseItem.h"
#include "ResourceManager.h"
#include "SpriteManager.h"

#define UIRES_POSX		UI_WIDTH
#define UIRES_POSY		0

#define	FONT_SIZE		16
#define LINE_HEIGHT		24
#define TITLE_SIZE		FONT_SIZE + 8

#define MENU_TILE_OPEN_WIDTH	300
#define MENU_TILE_OPEN_HEIGHT	160

#define MENU_COLOR		sf::Color(249, 195, 63)

UserInterfaceBase::UserInterfaceBase(sf::RenderWindow* app, int tileIndex) {
  _app = app;

  _posX = 200;
  _posY = 200;

  _posTileX = (MENU_TILE_WIDTH + UI_PADDING + UI_PADDING) * tileIndex + UI_PADDING;
  _posTileY = UI_PADDING;
  _tileIndex = tileIndex;
  _isTileActive = false;
  _isOpen = false;

  _textureTile.loadFromFile("../res/bg_tile_base.png");
  _bgTile.setTexture(_textureTile);
  _bgTile.setTextureRect(sf::IntRect(0, 0, 240, 120));

  _texturePanel.loadFromFile("../res/bg_panel_base.png");
  _bgPanel.setTexture(_texturePanel);
  _bgPanel.setTextureRect(sf::IntRect(0, 0, 800, 600));
}

UserInterfaceBase::~UserInterfaceBase() {
}


bool	UserInterfaceBase::checkKey(sf::Keyboard::Key key) {
  if (_isOpen) {
	switch (key) {
	case sf::Keyboard::Escape:
	  _isOpen = false;
	  return true;
	}
  }

  return false;
}

bool	UserInterfaceBase::onMouseMove(int x, int y) {
  _isTileActive = false;

  if (_isOpen && x > _posX && x < _posX + 800 && y > _posY && y < _posY + 600) {
	return true;
  }

  else if (x > _posTileX && x < _posTileX + 240 && y > _posTileY && y < _posTileY + 120) {
	_isTileActive = true;
	return true;
  }

  return false;
}

bool	UserInterfaceBase::mousePress(sf::Mouse::Button button, int x, int y) {
  if (_isOpen) {
	return true;
  }
  return false;
}

bool	UserInterfaceBase::mouseRelease(sf::Mouse::Button button, int x, int y) {

  // On tile
  if (x > _posTileX && x < _posTileX + 240 && y > _posTileY && y < _posTileY + 120) {
	_isOpen = !_isOpen;
	_isTileActive = true;
	return true;
  }

  // Panel open
  else if (_isOpen) {
	return true;
  }

  return false;
}

void	UserInterfaceBase::drawPanel() {
  _bgPanel.setPosition(_posX, _posY);
  _app->draw(_bgPanel);
}

void	UserInterfaceBase::drawTile(sf::Color color) {
  _bgTile.setPosition(_posTileX, _posTileY);
  _bgTile.setColor(isTileActive() || isOpen() ? color : sf::Color(255, 255, 255));
  _app->draw(_bgTile);
}
