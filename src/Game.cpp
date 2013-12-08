#include <iostream>
#include <stdio.h>
#include <string.h>

#include <SFML/Graphics.hpp>

#include "Game.hpp"

WorldMap* gl_worldmap;

extern int old_time1;
extern int old_time2;
extern sf::RenderWindow	*app;

sf::Time _time_elapsed;

Game::Game(sf::RenderWindow* app): run(true), up_to_date(false), pause(false) {
  _app = app;
  _lastInput = 0;
  _frame = 0;
  _ui = new UserInterface();
  _worldMap = new WorldMap();
  gl_worldmap = _worldMap;

  _spriteManager = new SpriteManager();

  character = new Character(2, 2);
  character->go(8, 8);

  // Dump worldmap
  #if DEBUG
  for (int y = 0; y < _worldMap->getHeight(); y++) {
	for (int x = 0; x < _worldMap->getWidth(); x++) {
	  std::cout << (_worldMap->getItem(x, y) == 0 ? 0 : 9);
	}
	std::cout << std::endl;
  }
  #endif
  

  std::cout << "Game:\tdone" << std::endl;
}

Game::~Game() {
  //   delete player;
  //   delete scene;
  //   delete music;
}


//fixme: 
// au changement de scene, conserver la meme instance de player pour la nouvelle scene OU
// set le nouveau player avec les parametres de l'ancien
//resolv:
// l'obj player a ete redescendu dans l'obj Game, a voir
void	Game::update()
{
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
  if (character != 0) {
	if (_frame % CHARACTER_MOVE_INTERVAL == 0) {
	  character->move();
	}

	character->draw(_app);
  }

  // User interface
  _ui->draw(_app);
}

void	Game::draw_surface() {
  int w = _worldMap->getWidth();
  int h = _worldMap->getHeight();

  for (int i = 0; i < w; i++) {
	for (int j = 0; j < h; j++) {
	  BaseItem* item = _worldMap->getItem(i, j);
	  sf::Sprite* sprite = _spriteManager->getSprite(item != NULL ? item->type : BaseItem::NONE);
	  sprite->setPosition(UI_WIDTH + i * 32, UI_HEIGHT + j * 32);
	  _app->draw(*sprite);
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
		  }

		  if (event.type == sf::Event::MouseButtonReleased) {
			int posX = ((event.mouseButton.x - UI_WIDTH) / TILE_SIZE);
			int posY = ((event.mouseButton.y - UI_HEIGHT) / TILE_SIZE);

			std::cout << "event: " << posX << " x " << posY << std::endl;
			
			if (_ui->getCode() == UserInterface::CODE_BUILD_ITEM) {
			  Cursor* cursor = _ui->getCursor();
			  _worldMap->putItem(cursor->_x, cursor->_y, _ui->getBuildItemType());
			}

		  }

		  // GOTO
#if DEBUG
		  if (event.type == sf::Event::KeyReleased && event.key.code == sf::Keyboard::G) {
			Cursor* cursor = _ui->getCursor();
			character->go(cursor->_x, cursor->_y);
		  }
#endif

		  // if (!event.key.code)
		  // 	return false;

		  _force_refresh = _ui->checkKeyboard(event, _frame, _lastInput, _worldMap);
		  gere_quit();
		}

      // Update & refresh
	  _time_elapsed = display_timer.getElapsedTime();
      if (_force_refresh || _time_elapsed.asMilliseconds() > 25)
		{
    	  _force_refresh = false;
		  update();
		  refresh();
		  _app->display();
		  display_timer.restart();
		}

//      // Move pnj
//	  _time_elapsed = pnj_timer.getElapsedTime();
//      if (_time_elapsed.asMilliseconds() > 20)
//		{
//		  scene->pnj_update();
//		  pnj_timer.restart();
//		}
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
