#define _GLIBCXX_USE_NANOSLEEP
#include <iostream>
#include <string>
#include <thread>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include <ctime>

#include <SFML/Graphics.hpp>

#include "Game.h"
#include "ResourceManager.h"
#include "MapSearchNode.h"
#include "PathManager.h"
#include "JobManager.h"
#include "Settings.h"

Settings* Settings::_self = new Settings();

#define REFRESH_INTERVAL		(1000/60)
#define UPDATE_INTERVAL			10

Game::Game(sf::RenderWindow* app) {
  Debug() << "Game";

  _seed = 42;
  srand(_seed);
  _renderTime = 0;

  _app = app;
  _lastInput = 0;
  _frame = 0;
  _viewport = new Viewport(app);
  _ui = new UserInterface(app, _viewport);

  _spriteManager = SpriteManager::getInstance();
  _worldRenderer = new WorldRenderer(app, _spriteManager, _ui);

  // PathManager::getInstance()->init();

  _update = 0;
  _characterManager = CharacterManager::getInstance();

  // Background
  Debug() << "Game background";
  _backgroundTexture = new sf::Texture();
  _backgroundTexture->loadFromFile("../res/background.png");
  _background = new sf::Sprite();
  _background->setTexture(*_backgroundTexture);
  _background->setTextureRect(sf::IntRect(0, 0, 1920, 1080));

  app->setKeyRepeatEnabled(true);

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
  // Update item
  int w = WorldMap::getInstance()->getWidth();
  int h = WorldMap::getInstance()->getHeight();

  for (int i = 0; i < w; i++) {
	for (int j = 0; j < h; j++) {


	  // Update oxygen
	  if (_frame % 6 == 0) {
		WorldArea* area = WorldMap::getInstance()->getArea(i, j);
		if (area != NULL && area->isType(BaseItem::STRUCTURE_FLOOR)) {
		  int oxygen = area->getOxygen();
		  int count = 1;

		  WorldArea* a1 = WorldMap::getInstance()->getArea(i+1, j);
		  if (a1 == NULL) {
			count++;
		  }
		  else if (a1->isType(BaseItem::STRUCTURE_FLOOR)) {
			oxygen += a1->getOxygen();
			count++;
		  }

		  WorldArea* a2 = WorldMap::getInstance()->getArea(i-1, j);
		  if (a2 == NULL) {
			count++;
		  }
		  else if (a2->isType(BaseItem::STRUCTURE_FLOOR)) {
			oxygen += a2->getOxygen();
			count++;
		  }

		  WorldArea* a3 = WorldMap::getInstance()->getArea(i, j+1);
		  if (a3 == NULL) {
			count++;
		  }
		  else if (a3->isType(BaseItem::STRUCTURE_FLOOR)) {
			oxygen += a3->getOxygen();
			count++;
		  }

		  WorldArea* a4 = WorldMap::getInstance()->getArea(i, j-1);
		  if (a4 == NULL) {
			count++;
		  }
		  else if (a4->isType(BaseItem::STRUCTURE_FLOOR)) {
			oxygen += a4->getOxygen();
			count++;
		  }

		  int value = ceil((double)oxygen / count);

		  // if (a1 != NULL && a1->isType(BaseItem::STRUCTURE_FLOOR)) {
		  // 	a1->setOxygen(value);
		  // }

		  // if (a2 != NULL && a2->isType(BaseItem::STRUCTURE_FLOOR)) {
		  // 	a2->setOxygen(value);
		  // }

		  // if (a3 != NULL && a3->isType(BaseItem::STRUCTURE_FLOOR)) {
		  // 	a3->setOxygen(value);
		  // }

		  // if (a4 != NULL && a4->isType(BaseItem::STRUCTURE_FLOOR)) {
		  // 	a4->setOxygen(value);
		  // }

		  area->setOxygen(value);
		}
	  }



	  BaseItem* item = WorldMap::getInstance()->getItem(i, j);
	  if (item != NULL) {
		  
		// Check zone match
		if (!item->isZoneMatch() && item->getZoneId() == 0) {
		  Room* room = WorldMap::getInstance()->getRoom(item->getRoomId());
		  if (room != NULL) {
			room->setZoneId(item->getZoneIdRequired());
		  }
		}
	  }
	}
  }


  // assign works
  if (ResourceManager::getInstance().getMatter() > 0) {
	// if (_update % 10 == 0) {
	//   WorldMap::getInstance()->reloadAborted();
	// }

	// Character* character = NULL;
	// BaseItem* item = NULL;
	int jobsCount = JobManager::getInstance()->getCount();
	if (jobsCount > 0) {
	  Job* job = JobManager::getInstance()->getJob();
	  if (job != NULL && _characterManager->assignJob(job) == NULL) {
		JobManager::getInstance()->abort(job);
	  }
	}

	// int length = WorldMap::getInstance()->getBuildListSize();
	// if (length > 0
	// 	&& (character = _characterManager->getUnemployed(Character::PROFESSION_ENGINEER)) != NULL
	// 	&& (item = WorldMap::getInstance()->getItemToBuild()) != NULL) {

	//   Debug() << "Game: search path from char (x: " << character->getX() << ", y: " << character->getY() << ")";
	//   Debug() << "Game: search path to item (x: " << item->getX() << ", y: " << item->getY() << ")";

	//   character->setBuild(item);
	// }
  }

  // Character
  _characterManager->update(_update);

  _update++;

  Log::flush();
}

void	Game::refresh(double animProgress) {
  // Flush
  _app->clear(sf::Color(0, 0, 50));

  // Draw scene
  draw_surface();

  sf::Transform transform;
  transform = _viewport->getViewTransform(transform);
  _characterManager->refresh(_app, transform, animProgress);

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

	_run = true;
	_last_refresh = display_timer.getElapsedTime();
	_last_update = display_timer.getElapsedTime();

	while (_run && _app->isOpen()) {
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

			// Check key code
			if (this->event.type == sf::Event::KeyReleased) {

			  // If not consumes by UI
			  if (_ui->checkKeyboard(event, _frame, _lastInput) == false) {
				  Info() << "Game: suspend";

				if (this->event.key.code == sf::Keyboard::Escape) {
				  _run = false;
				  Info() << "Game: suspend";
				}
			  }
			}

			checkQuit();
		}

		sf::Time elapsed = display_timer.getElapsedTime();

		long nextUpdate = _last_update.asMilliseconds() + UPDATE_INTERVAL - elapsed.asMilliseconds();
		long nextRefresh = _last_refresh.asMilliseconds() + REFRESH_INTERVAL - elapsed.asMilliseconds();

		// Refresh
		if (nextRefresh <= 0) {
		  _renderTime = elapsed.asMilliseconds() - _last_refresh.asMilliseconds();
		  _last_refresh = elapsed;
		  double animProgress = 1 - (double)nextUpdate / UPDATE_INTERVAL;
		  refresh(animProgress);
		  _app->display();
		} else {
		  sf::sleep(sf::milliseconds(nextRefresh));
		}

		// Update
		if (nextUpdate <= 0) {
		  _last_update = elapsed;
		  update();
		}

	}
}

void	Game::create() {
  Info() << "Game: create";

  ResourceManager::getInstance().setMatter(RESSOURCE_MATTER_START);

  WorldMap::getInstance()->create();
  CharacterManager::getInstance()->create();
}

void	Game::load(const char* filePath) {
  Info() << "Game: load";

  ifstream ifs(filePath);
  string line;
  std::vector<std::string> vector;
  int value;
  bool	inBlock = false;

  if (ifs.is_open()) {
    while (getline(ifs, line)) {

	  // Start block
	  if (line.compare("BEGIN GAME") == 0) {
		inBlock = true;
	  }

	  // End block
	  else if (line.compare("END GAME") == 0) {
		inBlock = false;
	  }

	  // Items
	  else if (inBlock) {
		std::cout << "line: " << line << std::endl;
		vector.clear();
		FileManager::split(line, '\t', vector);
		if (vector[0].compare("matter") == 0) {
		  std::istringstream issValue(vector[1]);
		  issValue >> value;
		  ResourceManager::getInstance().setMatter(value);
		}
	  }
	}
    ifs.close();
  } else {
	Error() << "Unable to open save file: " << filePath;
  }

  WorldMap::getInstance()->load(filePath);
  CharacterManager::getInstance()->load(filePath);
}

void	Game::save(const char* filePath) {
  Info() << "Game save";

  ofstream ofs(filePath);

  if (ofs.is_open()) {
	ofs << "BEGIN GAME\n";
	ofs << "matter\t" << ResourceManager::getInstance().getMatter() << "\n";
	ofs << "END GAME\n";
	ofs.close();
  } else {
	Error() << "Unable to open save file: " << filePath;
  }

  // ofstream ofs(filePath);
  // ofs.close();

  WorldMap::getInstance()->save(filePath);
  CharacterManager::getInstance()->save(filePath);
}

void	Game::checkQuit() {
  if (this->event.type == sf::Event::Closed) {
	_app->setKeyRepeatEnabled(false);
	_app->close();
	Info() << "Bye";
  }

  // if (this->event.type == sf::Event::KeyReleased &&
  // 	  this->event.key.code == sf::Keyboard::Escape) {
	
  // }

  if (this->event.type == sf::Event::KeyReleased && this->event.key.code == sf::Keyboard::K) {
	_run = false;
	// _app->setKeyRepeatEnabled(false);
	Info() << "Bye";
  }


}
