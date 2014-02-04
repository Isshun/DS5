#include "UserInteraction.h"
#include "WorldMap.h"
#include "MainRenderer.h"
#include "JobManager.h"

UserInteraction::UserInteraction(Viewport* viewport) {
  _viewport = viewport;
  _cursor = new Cursor();
  _cursorTexture.loadFromFile("../sprites/cursor.png");
  _startPressX = 0;
  _startPressY = 0;
  _mouseMoveX = 0;
  _mouseMoveY = 0;
  _button = -1;
  _mode = MODE_NONE;
}

void	UserInteraction::drawCursor(int startX, int startY, int toX, int toY) {
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
	  MainRenderer::getInstance()->draw(sprite, render);
	}
  }
}

void	UserInteraction::refreshCursor() {
  if (_mode == MODE_BUILD || _mode == MODE_EREASE) {
	ItemInfo itemInfo = BaseItem::getItemInfo(_itemType);

	// Structure: multiple 1x1 tile
	if (_button == sf::Mouse::Left) {
	  if (itemInfo.type > BaseItem::STRUCTURE_START && itemInfo.type < BaseItem::STRUCTURE_STOP) {
		drawCursor(std::min(_startPressX, _mouseMoveX),
				   std::min(_startPressY, _mouseMoveY),
				   std::max(_startPressX, _mouseMoveX),
				   std::max(_startPressY, _mouseMoveY));
	  }

	  // Single nxn tile: holding mouse button
	  else {
		drawCursor(std::min(_startPressX, _mouseMoveX),
				   std::min(_startPressY, _mouseMoveY),
				   std::min(_startPressX, _mouseMoveX) + itemInfo.width - 1,
				   std::min(_startPressY, _mouseMoveY) + itemInfo.height - 1);
	  }
	}

	// Single 1x1 tile: mouse hover
	else {
	  drawCursor(_mouseMoveX, _mouseMoveY, _mouseMoveX, _mouseMoveY);
	}
  }
}

void	UserInteraction::mouseMove(int x, int y) {
  _mouseMoveX = x;
  _mouseMoveY = y;
}

void	UserInteraction::mousePress(int button, int x, int y) {
  Error() << "Press: " << y;

  if (button == sf::Mouse::Left) {
	_button = button;
	_startPressX = x;
	_startPressY = y;
  }
}

bool	UserInteraction::mouseRelease(int button, int x, int y) {
  if (_mode != MODE_NONE && button == sf::Mouse::Left) {
	Error() << "Release: " << y;

	int startX = std::min(_startPressX, _mouseMoveX);
	int startY = std::min(_startPressY, _mouseMoveY);
	int toX = std::max(_startPressX, _mouseMoveX);
	int toY = std::max(_startPressY, _mouseMoveY);

	switch (_mode) {
	case MODE_BUILD:
	  build(startX, startY, toX, toY);
	  break;
	case MODE_EREASE:
	  erease(startX, startY, toX, toY);
	  break;
	}

	_button = -1;
	_startPressX = -1;
	_startPressY = -1;

	return true;
  }
  return false;
}

void	UserInteraction::build(int startX, int startY, int toX, int toY) {
  for (int x = toX; x >= startX; x--) {
	for (int y = toY; y >= startY; y--) {

	  // Structure
	  BaseItem* item = NULL;
	  if (_itemType == BaseItem::STRUCTURE_ROOM) {
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
		if (_itemType != -1) {
		  Warning() << "3 " << _itemType << " " << BaseItem::getItemName(_itemType);
		  JobManager::getInstance()->build(_itemType, x, y);
		  // item = WorldMap::getInstance()->putItem(x, y, type);
		}
	  }

	  // if (item != NULL) {
	  // }
	}
  }
}

void	UserInteraction::erease(int startX, int startY, int toX, int toY) {
  for (int x = startX; x <= toX; x++) {
	for (int y = startY; y <= toY; y++) {
	  WorldMap::getInstance()->removeItem(x, y);
	}
  }
}
