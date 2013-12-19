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
#include "Settings.h"

Settings* Settings::_self = new Settings();

extern int old_time1;
extern int old_time2;

sf::Time _time_elapsed;

Game::Game(sf::RenderWindow* app) {
  Debug() << "Game";

  _seed = 42;
  srand(_seed);

  _run = true;
  _app = app;
  _lastInput = 0;
  _frame = 0;
  _viewport = new Viewport(app);
  WorldMap* worldMap = WorldMap::getInstance();
  worldMap->init();

  // PathManager::getInstance()->init();

  _update = 0;

  _characterManager = new CharacterManager();
  // for (int i = 0; i < 50; i++) {
  // 	_characterManager->add(rand() % 20, rand() % 20);
  // }
  // _characterManager->add(15, 16);
  // _characterManager->add(9, 8);
  // _characterManager->add(17, 8);
  // _characterManager->add(15, 20);
  // _characterManager->add(16, 20);

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
	int length = WorldMap::getInstance()->getBuildListSize();
	if (length > 0
		&& (character = _characterManager->getUnemployed(Character::PROFESSION_ENGINEER)) != NULL
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
  srand(42);

  // Flush
  _app->clear(sf::Color(0, 0, 50));

  // Draw scene
  draw_surface();

  sf::Transform transform;
  transform = _viewport->getViewTransform(transform);
  _characterManager->draw(_app, transform);

  // User interface
  _ui->refresh(_update);

  srand(_seed + _frame++);
}

void	Game::draw_surface() {
  sf::Font font;
  if (!font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());

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

  // int offset = (TILE_SIZE / 2 * 3) - TILE_SIZE;
  int offsetY = -16;
  int offsetX = 2;
  int offsetWall = (TILE_SIZE / 2 * 3) - TILE_SIZE;

  // Draw floor
  for (int i = w-1; i >= 0; i--) {
  	for (int j = h-1; j >= 0; j--) {
	  BaseItem* item = WorldMap::getInstance()->getItem(i, j);
	  if (item != NULL) {
		sf::Sprite sprite;

		if (item->isType(BaseItem::STRUCTURE_DOOR)) {
		  _spriteManager->getSprite(item, &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		} else {
		  _spriteManager->getFloor(item, item->getZoneId(), item->getRoomId(), &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		}
	  
		_app->draw(sprite, render);
	  }
	}
  }

  // Draw structure
  int lastSpecialX = -1;
  int lastSpecialY = -1;

  for (int j = h-1; j >= 0; j--) {
	for (int i = w-1; i >= 0; i--) {
	  int r = rand();
	  BaseItem* item = WorldMap::getInstance()->getItem(i, j);
	  if (item != NULL && !item->isType(BaseItem::STRUCTURE_FLOOR) && item->isStructure()) {
		BaseItem* bellow = WorldMap::getInstance()->getItem(i, j+1);
		BaseItem* right = WorldMap::getInstance()->getItem(i+1, j);
		BaseItem* left = WorldMap::getInstance()->getItem(i-1, j);
		BaseItem* above = WorldMap::getInstance()->getItem(i, j-1);
		sf::Sprite sprite;
	  
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
			  BaseItem* aboveRight = WorldMap::getInstance()->getItem(i+1, j-1);
			  BaseItem* bellowRight = WorldMap::getInstance()->getItem(i+1, j+1);
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
	  
		_app->draw(sprite, render);
	  }
	}
  }

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
		if (item->isComplete() && item&& !item->isSupply()) {
		  sf::Sprite sprite;

		  _spriteManager->getSprite(SpriteManager::IC_BATTERY, &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);

		  _app->draw(sprite, render);
		}

		// Draw debug
		if (Settings::getInstance()->isDebug()) {
		  sf::RectangleShape shape;
		  shape.setSize(sf::Vector2f(TILE_SIZE, TILE_SIZE));
		  shape.setFillColor(sf::Color(250, 200, 200, 100));
		  shape.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		  _app->draw(shape, render);

		  sf::Text text;
		  text.setFont(font);
		  text.setCharacterSize(10);
		  text.setStyle(sf::Text::Regular);
		  std::ostringstream oss;
		  oss << item->getRoomId();
		  text.setString(oss.str().c_str());
		  text.setPosition(i * TILE_SIZE, j * TILE_SIZE);
		  _app->draw(text, render);
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

  Game	game(&app);
  app.setKeyRepeatEnabled(true);
  game.loop();

  return EXIT_SUCCESS;
}
