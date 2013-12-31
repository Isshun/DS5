#include <iostream>
#include <stdio.h>
#include <string.h>
#include <sstream>
#include "UserInterfaceMenuOperation.h"
#include "BaseItem.h"
#include "ResourceManager.h"
#include "WorldMap.h"
#include "JobManager.h"
#include "Job.h"
#include "Character.h"

#define UIRES_POSX		UI_WIDTH
#define UIRES_POSY		0

#define	FONT_SIZE		16
#define LINE_HEIGHT		24
#define TITLE_SIZE		FONT_SIZE + 8

#define MENU_COLOR		sf::Color(80, 255, 200)
#define MENU_COLOR_OPEN	sf::Color(100, 255, 220)

UserInterfaceMenuOperation::UserInterfaceMenuOperation(sf::RenderWindow* app) {
  _app = app;

  _isTileOpen = false;
  _isJobsOpen = false;

  _textureTile.loadFromFile("../res/bg_tile.png");
  _bgTile.setTexture(_textureTile);
  _bgTile.setTextureRect(sf::IntRect(0, 0, 240, 120));

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceMenuOperation::~UserInterfaceMenuOperation() {
}

void	UserInterfaceMenuOperation::draw(int index) {
  drawTile(2);

  if (_isJobsOpen) {
	drawJobs();
  }
}

void	UserInterfaceMenuOperation::drawTile(int index) {
  int posX = (MENU_TILE_WIDTH + UI_PADDING + UI_PADDING) * index + UI_PADDING;
  int posY = UI_PADDING;

  std::ostringstream oss;

  _bgTile.setPosition(posX, UI_PADDING);
  _bgTile.setColor(_isTileOpen ? MENU_COLOR_OPEN : MENU_COLOR);
  _app->draw(_bgTile);

  // // Background
  // sf::RectangleShape shape;
  // shape.setSize(sf::Vector2f(MENU_TILE_WIDTH, MENU_TILE_HEIGHT));
  // shape.setFillColor(sf::Color(0, 0, 100));
  // shape.setPosition(posX, posY);
  // _app->draw(shape);

  sf::Text text;
  text.setFont(_font);
  text.setCharacterSize(FONT_SIZE);

  {
    std::ostringstream oss;
    oss << "Power: " << ResourceManager::getInstance().getPower();

    text.setString(oss.str());
    text.setPosition(posX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING + LINE_HEIGHT * 0);
    _app->draw(text);
  }

  {
    std::ostringstream oss;
    oss << "O2: " << ResourceManager::getInstance().getO2();

    text.setString(oss.str());
    text.setPosition(posX + UI_PADDING, TITLE_SIZE + UI_PADDING + UI_PADDING + LINE_HEIGHT * 1);
    _app->draw(text);
  }

  text.setString("Operation");
  text.setCharacterSize(TITLE_SIZE);
  text.setPosition(posX + UI_PADDING, UI_PADDING);
  _app->draw(text);
  text.setString("O");
  text.setStyle(sf::Text::Underlined);
  text.setColor(sf::Color(255, 255, 0));
  _app->draw(text);
}

void	UserInterfaceMenuOperation::drawJobs() {
  int posX = 200;
  int posY = 100;

  // _background.setPosition(posX, UI_PADDING);
  // _background.setColor(MENU_COLOR);
  // _app->draw(_background);

  // Background
  sf::RectangleShape shape;
  shape.setSize(sf::Vector2f(800, 600));
  shape.setFillColor(sf::Color(0, 0, 100, 100));
  shape.setPosition(posX, posY);
  _app->draw(shape);

  sf::Text text;
  text.setFont(_font);
  text.setCharacterSize(16);

  // Display jobs
  std::list<Job*>::iterator it;
  std::list<Job*>* jobs = JobManager::getInstance()->getJobs();

  int i = 0;
  for (it = jobs->begin(); it != jobs->end(); ++it) {
    std::ostringstream oss;
    oss << "Job # " << (*it)->getId()
		<< ": " << JobManager::getActionName((*it)->getAction())
		<< " " << BaseItem::getItemName((*it)->getItemType());
	if ((*it)->getCharacter() != NULL) {
	  text.setColor(sf::Color(255, 255, 255));
	  oss << " (" << (*it)->getCharacter()->getName() << ")";
	} else {
	  text.setColor(sf::Color(255, 255, 0));
	  oss << " (on queue)";
	}
    text.setString(oss.str());
    text.setPosition(posX + UI_PADDING, posY + UI_PADDING + (32 * i++));
    _app->draw(text);
  }

  // for (int i = 0; i < 20; i++) {
  //   std::ostringstream oss;
  //   oss << "Job # " << i << ": Build item";
  //   text.setString(oss.str());
  //   text.setPosition(posX + UI_PADDING, posY + UI_PADDING + (32 * i));
  //   _app->draw(text);
  // }
}
