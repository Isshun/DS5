#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include <ctime>

#include <SFML/Graphics.hpp>

#include "Game.hpp"
#include "ResourceManager.h"
#include "MapSearchNode.h"
#include "PathManager.h"

extern int old_time1;
extern int old_time2;

sf::Time _time_elapsed;

Game::Game(sf::RenderWindow* app) {
  Debug() << "Game";

  _run = true;
  _app = app;
  _lastInput = 0;
  _frame = 0;
  _viewport = new Viewport(app);
  WorldMap* worldMap = WorldMap::getInstance();
  _update = 0;

  _characterManager = new CharacterManager();
  _characterManager->add(2, 2);
  _characterManager->add(8, 8);
  _characterManager->add(20, 8);
  _characterManager->add(50, 8);

  _ui = new UserInterface(app, worldMap, _viewport, _characterManager);

  _spriteManager = new SpriteManager();

  // Background
  Debug() << "Game background";
  _backgroundTexture = new sf::Texture();
  _backgroundTexture->loadFromFile("../res/background.png");
  _background = new sf::Sprite();
  _background->setTexture(*_backgroundTexture);
  _background->setTextureRect(sf::IntRect(0, 0, 1920, 1080));

  Info() << "Game:\tdone";
}

Game::~Game() {
  // delete _viewport;
  // delete _worldMap;
  // delete _ui;
  // delete _spriteManager;
  // delete _characterManager;
  // delete _background;
  // delete _backgroundTexture;
}

void	Game::update() {

  // assign works
  if (ResourceManager::getInstance().getMatter() > 0) {
	if (_update % 10 == 0) {
	  WorldMap::getInstance()->reloadAborted();
	}

	Character* character = NULL;
	BaseItem* item = NULL;
	// int length = _worldMap->getBuildListSize();
	if ((character = _characterManager->getUnemployed()) != NULL
		&& (item = WorldMap::getInstance()->getItemToBuild()) != NULL) {

	  Debug() << "Game: search path from char (x: " << character->getX() << ", y: " << character->getY() << ")";
	  Debug() << "Game: search path to item (x: " << item->getX() << ", y: " << item->getY() << ")";

	  AStarSearch<MapSearchNode>* path = PathManager::getInstance()->getPath(character, item);

	  if (path != NULL) {
		Debug() << "Game: add build job to character";
		character->build(path, item);
	  } else {
		WorldMap::getInstance()->buildAbort(item);
	  }
	}
  }

  // Character
  _characterManager->update(_update);

  _force_refresh = false;
  _update++;
}

void	Game::refresh() {
  // Flush
  _app->clear(sf::Color(0, 0, 50));

  // Draw scene
  draw_surface();

  sf::Transform transform;
  transform = _viewport->getViewTransform(transform);
  _characterManager->draw(_app, transform);

  // User interface
  _ui->refresh(_update);

  _frame++;
}

void	Game::draw_surface() {
  int w = WorldMap::getInstance()->getWidth();
  int h = WorldMap::getInstance()->getHeight();

  // Background
  sf::Transform transform2;
  sf::RenderStates render2(_viewport->getViewTransformBackground(transform2));
  _app->draw(*_background, render2);

  // Render transformation for viewport
  sf::Transform transform;
  sf::RenderStates render(_viewport->getViewTransform(transform));

  // // Draw viewport background
  // sf::RectangleShape shape;
  // shape.setSize(sf::Vector2f(w * TILE_SIZE, h * TILE_SIZE));
  // shape.setFillColor(sf::Color(0, 50, 100));
  // _app->draw(shape, render);

  int offset = (TILE_SIZE / 2 * 3) - TILE_SIZE;

  // Draw floor
  for (int i = w-1; i >= 0; i--) {
  	for (int j = h-1; j >= 0; j--) {
	  BaseItem* item = WorldMap::getInstance()->getItem(i, j);
	  if (item != NULL) {
		sf::Sprite sprite;

		if (item->type == BaseItem::STRUCTURE_DOOR) {
		  _spriteManager->getSprite(item, &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		} else {
		  _spriteManager->getFloor(item->zone, item->room, &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		}
	  
		_app->draw(sprite, render);
	  }
	}
  }

  // Draw structure
  int lastSpecialX = -1;
  int lastSpecialY = -1;
  srand(42);
  for (int j = h-1; j >= 0; j--) {
	for (int i = w-1; i >= 0; i--) {
	  BaseItem* item = WorldMap::getInstance()->getItem(i, j);
	  if (item != NULL && item->type != BaseItem::STRUCTURE_FLOOR && item->isStructure()) {
		BaseItem* bellow = WorldMap::getInstance()->getItem(i, j+1);
		BaseItem* right = WorldMap::getInstance()->getItem(i+1, j);
		BaseItem* left = WorldMap::getInstance()->getItem(i-1, j);
		BaseItem* above = WorldMap::getInstance()->getItem(i, j-1);
		sf::Sprite sprite;
	  
		if (item->type == BaseItem::STRUCTURE_WALL) {

		  // bellow is a wall
		  if (bellow != NULL && bellow->type == BaseItem::STRUCTURE_WALL) {
			_spriteManager->getWall(1, &sprite, 0);
			sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE - offset);
		  }

		  // No wall above or bellow
		  else if ((above == NULL || above->type != BaseItem::STRUCTURE_WALL) &&
			  (bellow == NULL || bellow->type != BaseItem::STRUCTURE_WALL)) {

			// Check double wall
			bool doubleWall = false;
			if (right != NULL && right->type == BaseItem::STRUCTURE_WALL &&
				(lastSpecialY != j || lastSpecialX != i+1)) {
			  BaseItem* aboveRight = WorldMap::getInstance()->getItem(i+1, j-1);
			  BaseItem* bellowRight = WorldMap::getInstance()->getItem(i+1, j+1);
			  if ((aboveRight == NULL || aboveRight->type != BaseItem::STRUCTURE_WALL) &&
				  (bellowRight == NULL || bellowRight->type != BaseItem::STRUCTURE_WALL)) {
				doubleWall = true;
			  }
			}

			// Special double wall
			if (doubleWall) {
				_spriteManager->getWall(2, &sprite, rand());
				lastSpecialX = i;
				lastSpecialY = j;
			}

			// Special single wall
			else {
			  _spriteManager->getWall(3, &sprite, rand());
			}
		  	sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE - offset);
		  }

		  // // left is a wall
		  // else if (left != NULL && left->type == BaseItem::STRUCTURE_WALL) {
		  // 	_spriteManager->getWall(2, &sprite);
		  // 	sprite.setPosition(i * TILE_SIZE - TILE_SIZE, j * TILE_SIZE - offset);
		  // }

		  // single wall
		  else {
			_spriteManager->getWall(0, &sprite, 0);
			sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE - offset);
		  }

		}
	  
		_app->draw(sprite, render);
	  }
	}
  }
  srand(time(0));

  // Run through items
  for (int i = w-1; i >= 0; i--) {
  	for (int j = h-1; j >= 0; j--) {
	  BaseItem* item = WorldMap::getInstance()->getItem(i, j);

	  // // Draw floor
	  // if (item != NULL) {
	  // 	for (int x = 0; x < item->getWidth(); x++) {
	  // 	  for (int y = 0; y < item->getHeight(); y++) {
	  // 		if (item != NULL && item->type != BaseItem::NONE) {
	  // 		  sf::Sprite* sprite = _spriteManager->getSprite(item);
	  // 		  sprite->setPosition(UI_WIDTH + i * TILE_SIZE + x * TILE_SIZE, UI_HEIGHT + j * TILE_SIZE + y * TILE_SIZE);
	  // 		  _app->draw(*sprite);
	  // 		}
	  // 	  }
	  // 	}
	  // }

	  if (item != NULL) {

		// Draw item
		if (item->type != BaseItem::STRUCTURE_FLOOR && item->isStructure() == false) {
		  sf::Sprite sprite;

		  _spriteManager->getSprite(item, &sprite);

		  if (item->isStructure()) {
			sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		  } else {
			sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE - offset);
		  }

		  _app->draw(sprite, render);
		}

		// Draw battery
		if (item->isComplete() && item&& !item->isSupply()) {
		  sf::Sprite sprite;

		  _spriteManager->getSprite(SpriteManager::IC_BATTERY, &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);

		  _app->draw(sprite, render);
		}

	  }
  
	  // // Draw progress
	  // if (item != NULL) {
	  // 	for (int x = 0; x < item->getWidth(); x++) {
	  // 	  for (int y = 0; y < item->getHeight(); y++) {
	  // 		if (item != NULL && item->type != BaseItem::NONE) {
	  // 		  sf::Text text;
	  // 		  std::ostringstream oss;
	  // 		  oss << item->progress;
	  // 		  text.setString(oss.str());
	  // 		  text.setFont(font);
	  // 		  text.setCharacterSize(10);
	  // 		  text.setPosition(UI_WIDTH + i * TILE_SIZE + x * TILE_SIZE + 2,
	  // 						   UI_HEIGHT + j * TILE_SIZE + y * TILE_SIZE + 18);
	  // 		  _app->draw(text);
	  // 		}
	  // 	  }
	  // 	}
	  // }

	}
  }
}

void	Game::loop() {
  // fixme: actuellement update et refresh se partage les meme timers
  sf::Clock display_timer;
  sf::Clock action_timer;
  sf::Clock pnj_timer;

  while (_app->isOpen()) {

	// Events
	while (_app->pollEvent(event)) {
	  if (event.type == sf::Event::MouseMoved) {
		_ui->mouseMoved(event.mouseMove.x, event.mouseMove.y);
	  }

	  if (event.type == sf::Event::MouseButtonPressed) {
		_ui->mousePress(event.mouseButton.button, event.mouseButton.x, event.mouseButton.y);
	  }

	  if (event.type == sf::Event::MouseButtonReleased) {
		_ui->mouseRelease(event.mouseButton.button, event.mouseButton.x, event.mouseButton.y);
	  }

	  if (event.type == sf::Event::MouseWheelMoved) {
		_ui->mouseWheel(event.mouseButton.button, event.mouseButton.x, event.mouseButton.y);
	  }

	  _force_refresh = _ui->checkKeyboard(event, _frame, _lastInput);
	  gere_quit();
	}

	// Update & refresh
    _time_elapsed = display_timer.getElapsedTime();
	if (_time_elapsed.asMilliseconds() > 20) {
      display_timer.restart();
	  _force_refresh = false;

	  if (_frame % 5 == 0) {
		update();
	  }
	  refresh();
	  _app->display();
	}
  }
}

void	Game::gere_quit() {
  if (this->event.type == sf::Event::Closed) {
	_app->setKeyRepeatEnabled(true);
	_app->close();
	Info() << "Bye";
  }

  if (this->event.type == sf::Event::KeyPressed &&
	  this->event.key.code == sf::Keyboard::K) {
	_app->setKeyRepeatEnabled(true);
	_app->close();
	Info() << "Bye";
  }
}

int main(int argc, char *argv[]) {
  sf::RenderWindow app(sf::VideoMode(WINDOW_WIDTH, WINDOW_HEIGHT, 32), NAME);
  srand(time(0));

  Game	game(&app);
  app.setKeyRepeatEnabled(true);
  game.loop();

  return EXIT_SUCCESS;
}
