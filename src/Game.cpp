#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>

#include <SFML/Graphics.hpp>

#include "Game.hpp"
#include "ResourceManager.h"

WorldMap* gl_worldmap;

extern int old_time1;
extern int old_time2;

sf::Time _time_elapsed;

Game::Game(sf::RenderWindow* app): run(true), up_to_date(false), pause(false) {
  _app = app;
  _lastInput = 0;
  _frame = 0;
  _worldMap = new WorldMap();
  _ui = new UserInterface(app, _worldMap);
  gl_worldmap = _worldMap;

  _spriteManager = new SpriteManager();

  _characterManager = new CharacterManager();
  _characterManager->add(2, 2);
  _characterManager->add(8, 8);
  _characterManager->add(20, 8);
  _characterManager->add(50, 8);
  // character->go(8, 8);

  // // Dump worldmap
  // #if DEBUG
  // for (int y = 0; y < _worldMap->getHeight(); y++) {
  //   for (int x = 0; x < _worldMap->getWidth(); x++) {
  //     std::cout << (_worldMap->getItem(x, y) == 0 ? 0 : 9);
  //   }
  //   std::cout << std::endl;
  // }
  // #endif
  

  std::cout << "Game:\tdone" << std::endl;
}

Game::~Game() {
}

void	Game::update() {

  // assign works
  if (ResourceManager::getInstance().getMatter() > 0) {
	Character* character = NULL;
	BaseItem* item = NULL;
	int length = _worldMap->getBuildListSize();
	std::cout << Debug() << length << std::endl;
	while (--length >= 0 &&
		   (character = _characterManager->getUnemployed()) != NULL
		   && (item = _worldMap->getItemToBuild()) != NULL) {
		std::cout << Debug() << "Game: add build job to character" << std::endl;
		character->build(item);
	}
  }

  this->up_to_date = false;
  _force_refresh = false;
}

void	Game::refresh() {
  _frame++;

  // Flush
  _app->clear(sf::Color(0, 0, 50));

  // Draw scene
  draw_surface();

  // Character
  if (_frame % CHARACTER_MOVE_INTERVAL == 0) {
	_characterManager->move();
  }
  sf::Transform transform;
  transform = _ui->getViewTransform(transform);
  _characterManager->draw(_app, transform);

  // User interface
  _ui->refresh();
}

void	Game::draw_surface() {
  int w = _worldMap->getWidth();
  int h = _worldMap->getHeight();

  int viewPosX = _ui->getViewPosX();
  int viewPosY = _ui->getViewPosY();

  // Render transformation for viewport
  sf::Transform transform;
  sf::RenderStates render(_ui->getViewTransform(transform));

  // Draw viewport background
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(w * TILE_SIZE, h * TILE_SIZE));
  shape.setFillColor(sf::Color(0, 50, 100));
  _app->draw(shape, render);

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

		// Draw item
		{
		  sf::Sprite* sprite = _spriteManager->getSprite(item);
		  sprite->setPosition(i * TILE_SIZE, j * TILE_SIZE);

		  _app->draw(*sprite, render);
		}

		// Draw battery
		if (item->isComplete() && item&& !item->isSupply()) {
		  sf::Sprite* sprite = _spriteManager->getSprite(SpriteManager::IC_BATTERY);
		  sprite->setPosition(UI_WIDTH + i * TILE_SIZE, UI_HEIGHT + j * TILE_SIZE);

		  _app->draw(*sprite, render);
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

void	Game::gere_key()
{
}

void	Game::loop()
{
  // fixme: actuellement update et refresh se partage les meme timers
  sf::Clock display_timer;
  sf::Clock action_timer;
  sf::Clock pnj_timer;

  while (_app->isOpen())
    {
      // Events
      while (_app->pollEvent(event))
		{
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

		  // GOTO
// #if DEBUG
// 		  if (event.type == sf::Event::KeyReleased && event.key.code == sf::Keyboard::G) {
// 			Cursor* cursor = _ui->getCursor();
// 			character->go(cursor->_x, cursor->_y);
// 		  }
// #endif

		  _force_refresh = _ui->checkKeyboard(event, _frame, _lastInput, _worldMap);
		  gere_quit();
		}

      // Update & refresh
	  _time_elapsed = display_timer.getElapsedTime();
      if (_force_refresh || _time_elapsed.asMilliseconds() > 50)
		{
    	  _force_refresh = false;
		  update();
		  refresh();
		  _app->display();
		  display_timer.restart();
		}
    }
}

void	Game::gere_quit()
{
  if (this->event.type == sf::Event::Closed)
    {
      _app->setKeyRepeatEnabled(true);
      _app->close();
    }

  if (this->event.type == sf::Event::KeyPressed &&
	  this->event.key.code == sf::Keyboard::K)
    {
      _app->setKeyRepeatEnabled(true);
      _app->close();
    }

  //std::cout << "Closing" << std::endl;
}

int main(int argc, char *argv[]) {
  sf::RenderWindow* app = new sf::RenderWindow(sf::VideoMode(WINDOW_WIDTH, WINDOW_HEIGHT, 32), NAME);
  app->setKeyRepeatEnabled(true);

  sf::View view = app->getDefaultView();

  view.setViewport(sf::FloatRect(0.f, 0.f, 1.0f, 1.0f));

  // view.setCenter(400, 200);
  // view.zoom(zoom);

  app->setView(view);

  // load game
  Game	game(app);
  game.loop();

  return EXIT_SUCCESS;
}

