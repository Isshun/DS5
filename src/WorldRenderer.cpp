#include <sstream>
#include "Settings.h"
#include "WorldMap.h"
#include "WorldRenderer.h"

WorldRenderer::WorldRenderer(sf::RenderWindow* app, SpriteManager* spriteManager, UserInterface* ui) {
	_ui = ui;
	_app = app;
	_spriteManager = spriteManager;
	if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
		throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

void	WorldRenderer::draw(sf::RenderStates render) {
  int fromX = max(_ui->getRelativePosX(0)-1, 0);
  int fromY = max(_ui->getRelativePosY(0)-1, 0);
  // int toX = _ui->getRelativePosX(WorldMap::getInstance()->getWidth());
  // int toY = _ui->getRelativePosY(WorldMap::getInstance()->getHeight());
  int toX = min(_ui->getRelativePosX(WINDOW_WIDTH)+1, WorldMap::getInstance()->getWidth());
  int toY = min(_ui->getRelativePosY(WINDOW_HEIGHT)+1, WorldMap::getInstance()->getHeight());

  // Debug() << "Renderer: " << fromX << " to: " << toX;

  drawFloor(render, fromX, fromY, toX, toY);
  drawStructure(render, fromX, fromY, toX, toY);
  drawItems(render, fromX, fromY, toX, toY);

  // Draw debug
  if (Settings::getInstance()->isDebug()) {
	drawDebug(render, fromX, fromY, toX, toY);
  }
}

void	WorldRenderer::drawFloor(sf::RenderStates render, int fromX, int fromY, int toX, int toY) {
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(TILE_SIZE, TILE_SIZE));

  for (int i = toX-1; i >= fromX; i--) {
	for (int j = toY-1; j >= fromY; j--) {
	  srand(i * j);
	  WorldArea* item = WorldMap::getInstance()->getArea(i, j);
	  sf::Sprite sprite;
	  if (item != NULL) {

		if (item->isType(BaseItem::STRUCTURE_DOOR)) {
		  _spriteManager->getSprite(item, &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		  _app->draw(sprite, render);
		} else {
		  _spriteManager->getFloor(item, item->getZoneId(), item->getRoomId(), &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		  _app->draw(sprite, render);

		  // Oxygen
		  if (item->isType(BaseItem::STRUCTURE_FLOOR)) {
			if (item->getOxygen() < 25) {
			  _spriteManager->getNoOxygen(&sprite);
			  _app->draw(sprite, render);
			} else if (item->getOxygen() < 100) {
			  shape.setFillColor(sf::Color(255, 0, 0, item->getOxygen() * 125 / 100));
			  shape.setPosition(i * TILE_SIZE, j * TILE_SIZE);
			  _app->draw(shape, render);
			}
		  }
		}
  
	  }

	  else {
		_spriteManager->getExterior(&sprite);
		sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		_app->draw(sprite, render);
	  }

	}
  }
}

void	WorldRenderer::drawStructure(sf::RenderStates render, int fromX, int fromY, int toX, int toY) {
  int lastSpecialX = -1;
  int lastSpecialY = -1;
  int offsetWall = (TILE_SIZE / 2 * 3) - TILE_SIZE;

  for (int j = toY-1; j >= fromY; j--) {
	for (int i = toX-1; i >= fromX; i--) {
	  srand(i * j);
	  int r = rand();
	  WorldArea* item = WorldMap::getInstance()->getArea(i, j);
	  if (item != NULL) {
		sf::Sprite sprite;

		// Structure except floor
		if (item->isStructure() && !item->isType(BaseItem::STRUCTURE_FLOOR)) {

		  WorldArea* bellow = WorldMap::getInstance()->getArea(i, j+1);
		  WorldArea* right = WorldMap::getInstance()->getArea(i+1, j);
		  WorldArea* left = WorldMap::getInstance()->getArea(i-1, j);
		  WorldArea* above = WorldMap::getInstance()->getArea(i, j-1);
	  
		  // Door
		  if (item->isType(BaseItem::STRUCTURE_DOOR)) {
			// if (_characterManager->getCharacterAtPos(i, j) != NULL
			// 	  || _characterManager->getCharacterAtPos(i+1, j) != NULL
			// 	  || _characterManager->getCharacterAtPos(i-1, j) != NULL
			// 	  || _characterManager->getCharacterAtPos(i, j+1) != NULL
			// 	  || _characterManager->getCharacterAtPos(i, j-1) != NULL) {
			// 	_spriteManager->getWall(item, 2, &sprite, 0, 0);
			// } else {
			_spriteManager->getWall(item, 0, &sprite, 0, 0);
			// }
			sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE - offsetWall);
		  }

		  // Wall
		  else if (item->isType(BaseItem::STRUCTURE_WALL)) {

			// bellow is a wall
			if (bellow != NULL && bellow->isType(BaseItem::STRUCTURE_WALL)) {
			  _spriteManager->getWall(item, 1, &sprite, 0, 0);
			  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE - offsetWall);
			}

			// No wall above or bellow
			else if ((above == NULL || above->getType() != BaseItem::STRUCTURE_WALL) &&
					 (bellow == NULL || bellow->getType() != BaseItem::STRUCTURE_WALL)) {

			  // Check double wall
			  bool doubleWall = false;
			  if (right != NULL && right->isComplete() && right->isType(BaseItem::STRUCTURE_WALL) &&
				  (lastSpecialY != j || lastSpecialX != i+1)) {
				WorldArea* aboveRight = WorldMap::getInstance()->getArea(i+1, j-1);
				WorldArea* bellowRight = WorldMap::getInstance()->getArea(i+1, j+1);
				if ((aboveRight == NULL || aboveRight->getType() != BaseItem::STRUCTURE_WALL) &&
					(bellowRight == NULL || bellowRight->getType() != BaseItem::STRUCTURE_WALL)) {
				  doubleWall = true;
				}
			  }

			  // Normal
			  if (bellow == NULL) {
				// Double wall
				if (doubleWall) {
				  _spriteManager->getWall(item, 4, &sprite, r, 0);
				  lastSpecialX = i;
				  lastSpecialY = j;
				}
				// Single wall
				else {
				  _spriteManager->getWall(item, 0, &sprite, 0, 0);
				}
			  }
			  // Special
			  else {
				// Double wall
				if (doubleWall) {
				  _spriteManager->getWall(item, 2, &sprite, r, bellow->getZoneId());
				  lastSpecialX = i;
				  lastSpecialY = j;
				}
				// Single wall
				else {
				  _spriteManager->getWall(item, 3, &sprite, r, bellow->getZoneId());
				}
			  }
			  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE - offsetWall);
			}

			// // left is a wall
			// else if (left != NULL && left->type == BaseItem::STRUCTURE_WALL) {
			// 	_spriteManager->getWall(item, 2, &sprite);
			// 	sprite.setPosition(i * TILE_SIZE - TILE_SIZE, j * TILE_SIZE - offset);
			// }

			// single wall
			else {
			  _spriteManager->getWall(item, 0, &sprite, 0, 0);
			  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE - offsetWall);
			}

		  }	  
		}

		// // floor
		// else {
		// 	_spriteManager->getFloor(item, item->getZoneId(), item->getRoomId(), &sprite);
		// 	sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		// }

		_app->draw(sprite, render);
	  }
	}
  }
}

void	WorldRenderer::drawItems(sf::RenderStates render, int fromX, int fromY, int toX, int toY) {
	int offsetY = -16;
	int offsetX = 2;

	for (int i = toX-1; i >= fromX; i--) {
		for (int j = toY-1; j >= fromY; j--) {
			BaseItem* item = WorldMap::getInstance()->getItem(i, j);

			if (item != NULL) {

				// Draw item
				if (item->getType() != BaseItem::STRUCTURE_FLOOR && item->isStructure() == false) {
					sf::Sprite sprite;

					_spriteManager->getSprite(item, &sprite);

					if (item->isStructure()) {
						sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
					} else {
						sprite.setPosition(i * TILE_SIZE + offsetX, j * TILE_SIZE + offsetY);
					}

					_app->draw(sprite, render);
				}

				// Draw battery
				if (item->isComplete() && !item->isSupply()) {
					sf::Sprite sprite;

					_spriteManager->getSprite(SpriteManager::IC_BATTERY, &sprite);
					sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);

					_app->draw(sprite, render);
				}
			}
		}
	}
}

void	WorldRenderer::drawDebug(sf::RenderStates render, int fromX, int fromY, int toX, int toY) {
  int offsetY = -16;
  int offsetX = 2;

  for (int i = toX-1; i >= fromX; i--) {
	for (int j = toY-1; j >= fromY; j--) {
	  // BaseItem* item = WorldMap::getInstance()->getItem(i, j);
	  WorldArea* item = WorldMap::getInstance()->getArea(i, j);

	  if (item == NULL) {
		item = WorldMap::getInstance()->getArea(i, j);
	  }

	  if (item != NULL) {
		sf::RectangleShape shape;
		shape.setSize(sf::Vector2f(TILE_SIZE, TILE_SIZE));
		shape.setFillColor(sf::Color(250, 200, 200, 100));
		shape.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		_app->draw(shape, render);

		sf::Text text;
		text.setFont(_font);
		text.setCharacterSize(10);
		text.setColor(sf::Color(0, 0, 0));
		text.setStyle(sf::Text::Regular);
		std::ostringstream oss;
		//oss << item->getOxygen();
		oss << item->getRoomId();
		text.setString(oss.str().c_str());
		text.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		_app->draw(text, render);
	  }
	}
  }
}
