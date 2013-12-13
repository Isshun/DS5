#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include <ctime>

#include <SFML/Graphics.hpp>

#include "Game.hpp"
#include "ResourceManager.h"

WorldMap* gl_worldmap;

extern int old_time1;
extern int old_time2;

sf::Time _time_elapsed;

Game::Game(sf::RenderWindow* app) {
  std::cout << Debug() << "Game" << std::endl;

  _run = true;
  _app = app;
  _lastInput = 0;
  _frame = 0;
  _viewport = new Viewport(app);
  _worldMap = WorldMap::getInstance();
  _update = 0;

  _characterManager = new CharacterManager();
  // _characterManager->add(2, 2);
  // _characterManager->add(8, 8);
  // _characterManager->add(20, 8);
  _characterManager->add(50, 8);

  _ui = new UserInterface(app, _worldMap, _viewport, _characterManager);

  gl_worldmap = _worldMap;

  _spriteManager = new SpriteManager();

  // Background
  std::cout << Debug() << "Game background" << std::endl;
  _backgroundTexture = new sf::Texture();
  _backgroundTexture->loadFromFile("../res/background.png");
  _background = new sf::Sprite();
  _background->setTexture(*_backgroundTexture);
  _background->setTextureRect(sf::IntRect(0, 0, 1920, 1080));

  std::cout << Info() << "Game:\tdone" << std::endl;
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
	  _worldMap->reloadAborted();
	}

	Character* character = NULL;
	BaseItem* item = NULL;
	// int length = _worldMap->getBuildListSize();
	if ((character = _characterManager->getUnemployed()) != NULL
		&& (item = _worldMap->getItemToBuild()) != NULL) {
	  std::cout << Debug() << "Game: add build job to character" << std::endl;
	  character->build(item);
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
  _ui->refresh();

  _frame++;
}

void	Game::draw_surface() {
  int w = _worldMap->getWidth();
  int h = _worldMap->getHeight();

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

  // Run through items
  for (int i = w-1; i >= 0; i--) {
	for (int j = h-1; j >= 0; j--) {
	  BaseItem* item = _worldMap->getItem(i, j);

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

		// Draw floor
		{
		  sf::Sprite sprite;

		  _spriteManager->getSprite(BaseItem::STRUCTURE_FLOOR, &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);

		  _app->draw(sprite, render);
        }

		// Draw item
		{
		  sf::Sprite sprite;

		  _spriteManager->getSprite(item, &sprite);
		  sprite.setPosition(i * TILE_SIZE, j * TILE_SIZE);

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

	  _force_refresh = _ui->checkKeyboard(event, _frame, _lastInput, _worldMap);
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
	std::cout << Info() << "Bye" << std::endl;
  }

  if (this->event.type == sf::Event::KeyPressed &&
	  this->event.key.code == sf::Keyboard::K) {
	_app->setKeyRepeatEnabled(true);
	_app->close();
	std::cout << Info() << "Bye" << std::endl;
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
