#include <iostream>
#include <stdio.h>
#include <string.h>

#include <SFML/Graphics.hpp>

#include "Game.hpp"

#define CHARACTER_MOVE_INTERVAL 6
#define KEY_REPEAT_INTERVAL 5

WorldMap* gl_worldmap;

extern int old_time1;
extern int old_time2;
// extern Music	*music;

extern sf::RenderWindow	*app;

sf::Time _time_elapsed;

extern const char	*arg1;

Game::Game(): run(true), up_to_date(false), pause(false) {
  _lastInput = 0;
  _frame = 0;
  _worldMap = new WorldMap();
  gl_worldmap = _worldMap;

  _spriteManager = new SpriteManager();

  character = new Character(2, 2);
  character->go(8, 8);

  for (int y = 0; y < _worldMap->getHeight(); y++) {
	for (int x = 0; x < _worldMap->getWidth(); x++) {
	  std::cout << (_worldMap->getItem(x, y) == 0 ? 0 : 9);
	}
	std::cout << std::endl;
  }
  

  _cursor = new Cursor();

  // load de la scene
  try
	{
	  scene = new Scene();
	}
  catch (const char *str)
	{
	  std::cout << "Fatal exception catched [Game]: " << str << std::endl;
	  app->close();
	  return;
	}

  //   // load de la musique
  //   music = new Music();
  //   music->load(scene->get_music_path());

  std::cout << "Game:\tdone" << std::endl;
}

Game::~Game()
{
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
  scene->update();
  this->up_to_date = false;
  _force_refresh = false;
}

void	Game::refresh()
{
  _frame++;

  // Flush
  app->clear(sf::Color(0, 0, 50));

  // Draw scene
  draw_surface();

  // Character
  if (character != 0) {
	if (_frame % CHARACTER_MOVE_INTERVAL == 0) {
	  character->move();
	}

	sf::Texture texture;
	texture.loadFromFile("sprites/cless.png");
	texture.setSmooth(true);

	sf::Sprite* sprite = new sf::Sprite();
	sprite->setTexture(texture);
	sprite->setTextureRect(sf::IntRect(0, 0, 30, 30));
	sprite->setPosition(character->_posX * 32, character->_posY * 32);
	app->draw(*sprite);
	delete sprite;
  }

  // Cursor
  sf::Texture texture;
  texture.loadFromFile("sprites/cursor.png");
  sf::Sprite* sprite = new sf::Sprite();
  sprite->setTexture(texture);
  sprite->setTextureRect(sf::IntRect(0, 0, 32, 32));
  sprite->setPosition(_cursor->_x * TILE_SIZE, _cursor->_y * TILE_SIZE);
  app->draw(*sprite);
}

void	Game::draw_surface() {
  int w = _worldMap->getWidth();
  int h = _worldMap->getHeight();

  for (int i = 0; i < w; i++) {
	for (int j = 0; j < h; j++) {
	  BaseItem* item = _worldMap->getItem(i, j);
	  sf::Sprite* sprite = _spriteManager->getSprite(item != NULL ? item->type : BaseItem::NONE);
	  sprite->setPosition(i * 32, j * 32);
	  app->draw(*sprite);
	}
  }
}

void	Game::gere_key()
{
  switch (this->event.key.code)
    {
    case sf::Keyboard::Up:
//      if ((this->event.type == sf::Event::KeyPressed))
//		this->_cursor->_y--;
      if (_frame > _lastInput + KEY_REPEAT_INTERVAL && (this->event.type == sf::Event::KeyPressed)) {
		_lastInput = _frame;
  		this->_cursor->_y--;
	  }
      break;
    case sf::Keyboard::Down:
//      if ((this->event.type == sf::Event::KeyPressed))
//		this->_cursor->setRun(DOWN, true);
      if (_frame > _lastInput + KEY_REPEAT_INTERVAL && (this->event.type == sf::Event::KeyPressed)) {
		_lastInput = _frame;
		this->_cursor->_y++;
	  }
      break;
    case sf::Keyboard::Right:
//      if ((this->event.type == sf::Event::KeyPressed))
//		this->_cursor->setRun(RIGHT, true);
      if (_frame > _lastInput + KEY_REPEAT_INTERVAL && (this->event.type == sf::Event::KeyPressed)) {
		_lastInput = _frame;
  		this->_cursor->_x++;
	  }
      break;
    case sf::Keyboard::Left:
//      if ((this->event.type == sf::Event::KeyPressed))
//		this->_cursor->setRun(LEFT, true);
      if (_frame > _lastInput + KEY_REPEAT_INTERVAL && (this->event.type == sf::Event::KeyPressed)) {
		_lastInput = _frame;
		this->_cursor->_x--;
	  }
      break;

	  // GOTO
    case sf::Keyboard::G:
      if ((this->event.type == sf::Event::KeyReleased))
		character->go(_cursor->_x, _cursor->_y);
      break;

	  // PutItem
    case sf::Keyboard::Return:
      if ((this->event.type == sf::Event::KeyReleased))
	  {
		if (_worldMap->getItem(_cursor->_x, _cursor->_y) == NULL) {
		  sf::Texture texture;
		  texture.loadFromFile("sprites/house_in_1.png");
		  texture.setSmooth(true);

		  if ((this->event.type == sf::Event::KeyReleased))
			sf::Texture texture;
		  texture.loadFromFile("sprites/house_in_1.png");
		  texture.setSmooth(true);

		  BaseItem *item = new BaseItem();
		  item->isSolid = 1;

		  _worldMap->putItem(_cursor->_x, _cursor->_y, BaseItem::HULL);
		} else {
		  _worldMap->putItem(_cursor->_x, _cursor->_y, BaseItem::NONE);
		}
	  }
      break;

    case sf::Keyboard::Escape:
      app->close();
      break;

    default:
      break;
    }

  _force_refresh = true;
}

void	Game::loop()
{
  // fixme: actuellement update et refresh se partage les meme timers
  sf::Clock display_timer;
  sf::Clock action_timer;
  sf::Clock pnj_timer;

  while (app->isOpen())
    {
      // Events
      while (app->pollEvent(event))
		{
		  gere_key();
		  gere_quit();
		}

      // Update & refresh
	  _time_elapsed = display_timer.getElapsedTime();
      if (_force_refresh || _time_elapsed.asMilliseconds() > 25)
		{
    	  _force_refresh = false;
		  update();
		  refresh();
		  app->display();
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
      app->setKeyRepeatEnabled(true);
      app->close();
    }

  if (this->event.type == sf::Event::KeyPressed &&
	  this->event.key.code == sf::Keyboard::Escape)
    {
      app->setKeyRepeatEnabled(true);
      app->close();
    }

  //std::cout << "Closing" << std::endl;
}
