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
  _renderTime = 0;

  _run = true;
  _app = app;
  _lastInput = 0;
  _frame = 0;
  _viewport = new Viewport(app);
  WorldMap* worldMap = WorldMap::getInstance();
  worldMap->init();

  _spriteManager = new SpriteManager();
  _worldRenderer = new WorldRenderer(app, _spriteManager);

  // PathManager::getInstance()->init();

  _update = 0;

  _characterManager = new CharacterManager();
  // for (int i = 0; i < 50; i++) {
  // 	_characterManager->add(rand() % 20, rand() % 20);
  // }
  _characterManager->add(15, 16, Character::PROFESSION_ENGINEER);
  // _characterManager->add(9, 8);
  // _characterManager->add(17, 8);
  // _characterManager->add(15, 20);
  // _characterManager->add(16, 20);

  _ui = new UserInterface(app, worldMap, _viewport, _characterManager);

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
  _ui->refresh(_update, _renderTime);

  srand(_seed + _frame++);
}

void	Game::draw_surface() {
  // Background
  sf::Transform transform2;
  sf::RenderStates render2(_viewport->getViewTransformBackground(transform2));
  _app->draw(*_background, render2);

  // Render transformation for viewport
  sf::Transform transform;
  sf::RenderStates render(_viewport->getViewTransform(transform));
  _worldRenderer->draw(render);
}

void	Game::loop() {
	// fixme: actuellement update et refresh se partage les meme timers
	sf::Clock display_timer;
	sf::Clock action_timer;
	sf::Clock pnj_timer;
	sf::Clock timer;

	while (_app->isOpen()) {
		timer.restart();

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

		// Update & refresh: 50fps
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

		if (_renderTime == 0) {
			_renderTime = timer.getElapsedTime().asMilliseconds();
		} else {
			_renderTime = (_renderTime * 10 + timer.getElapsedTime().asMilliseconds()) / 11;
		}

		// Info() << "Render: " << _renderTime << "ms";
		if (_renderTime > 0) {
			Info() << "FPS: " << (int)(1000 / _renderTime);
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
