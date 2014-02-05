#include <iostream>
#include <cstdlib>
#include <stdio.h>
#include <sstream>
#include <string.h>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "CharacterManager.h"
#include "JobManager.h"

CharacterManager* CharacterManager::_self = new CharacterManager();

#define FUNCTIONS_COUNT 5

const Profession professions[] = {
  {Character::PROFESSION_ENGINEER, "Engineer", sf::Color(255, 255, 50), sf::Color(50, 50, 50)},
  {Character::PROFESSION_OPERATION, "Technician", sf::Color(128, 0, 0), sf::Color(255, 255, 255)},
  {Character::PROFESSION_DOCTOR, "Doctor", sf::Color(50, 200, 0), sf::Color(255, 255, 255)},
  {Character::PROFESSION_SCIENCE, "Science", sf::Color(50, 100, 255), sf::Color(255, 255, 255)},
  {Character::PROFESSION_SECURITY, "Security", sf::Color(42, 42, 42), sf::Color(255, 255, 255)},
  {Character::PROFESSION_NONE, NULL, sf::Color(0, 0, 0), sf::Color(0, 0, 0)}
};

CharacterManager::CharacterManager() {
  Debug() << "CharacterManager";

  _characters = new std::list<Character*>();
  _count = 0;

  _textures[0] = new sf::Texture();
  _textures[0]->loadFromFile("../res/Characters/scientifique.png");
  _textures[0]->setSmooth(true);

  _textures[1] = new sf::Texture();
  _textures[1]->loadFromFile("../res/Characters/soldat3.png");
  _textures[1]->setSmooth(true);

  _textures[2] = new sf::Texture();
  _textures[2]->loadFromFile("../res/Characters/Spacecharas.png");
  _textures[2]->setSmooth(true);

  Debug() << "CharacterManager done";
}

CharacterManager::~CharacterManager() {
  delete _textures[0];
  delete _textures[1];
  delete _textures[2];

  Character* c;
  while ((c = _characters->front()) != NULL) {
	delete c;
  }

  delete _characters;
}

Character*	CharacterManager::getInactive() {
  Debug() << "CharacterManager::getInactive";

  std::list<Character*>::iterator it;
  for (it = _characters->begin(); it != _characters->end(); ++it) {
	if ((*it)->getJob() == NULL) {
	  return *it;
	}
  }
  return NULL;
}

void	CharacterManager::assignJobs() {
  Debug() << "CharacterManager::assignJobs";

  std::list<Character*>::iterator it;
  for (it = _characters->begin(); it != _characters->end(); ++it) {
	if ((*it)->getJob() == NULL) {
		Job* job = JobManager::getInstance()->getJob(*it);
		if (job != NULL) {
		  job->setCharacter(*it);
		  (*it)->setJob(job);
		}
	}
  }
}

void	CharacterManager::create() {
  add(0, 0, Character::PROFESSION_ENGINEER);
  add(1, 0, Character::PROFESSION_OPERATION);
  add(2, 0, Character::PROFESSION_DOCTOR);
  add(3, 0, Character::PROFESSION_SCIENCE);
  add(4, 0, Character::PROFESSION_SECURITY);
}

void	CharacterManager::load(const char* filePath) {
  std::vector<std::string>		vector;
  ifstream						ifs(filePath);
  string						line;
  int							x, y, professionId;
  bool							inBlock = false;

  if (ifs.is_open()) {
    while (getline(ifs, line)) {

	  // Start block
	  if (line.compare("BEGIN CHARACTERS") == 0) {
		inBlock = true;
	  }

	  // End block
	  else if (line.compare("END CHARACTERS") == 0) {
		inBlock = false;
	  }

	  // Items
	  else if (inBlock) {
		vector.clear();
		FileManager::split(line, '\t', vector);
		if (vector.size() == 4) {
		  std::istringstream issX(vector[0]);
		  std::istringstream issY(vector[1]);
		  std::istringstream issProfessionId(vector[2]);
		  issX >> x;
		  issY >> y;
		  issProfessionId >> professionId;
		  Character* c = add(x, y, professionId);
		  c->setName(vector[3].c_str());
		}
	  }
	}
    ifs.close();
  } else {
	Error() << "Unable to open save file: " << filePath;
  }
}

void	CharacterManager::save(const char* filePath) {
  ofstream ofs(filePath, ios_base::app);
  std::list<Character*>::iterator it;

  if (ofs.is_open()) {
	ofs << "BEGIN CHARACTERS\n";

	for (it = _characters->begin(); it != _characters->end(); ++it) {
	  Character* c = *it;
	  ofs << c->getX() << "\t"
		  << c->getY() << "\t"
 		  << c->getProfessionId() << "\t"
 		  << c->getName() << "\n";
	}
	ofs << "END CHARACTERS\n";

	ofs.close();
  } else {
	Error() << "Unable to open save file: " << filePath;
  }
}

Character*	CharacterManager::getNext(Character* character) {
  std::list<Character*>::iterator it = _characters->begin();

  for (int count = _characters->size(); count >= 0; count--, ++it) {
	if ((*it) == character) {
	  return ++it == _characters->end() ? *(_characters->begin()) : *it;
	}
  }

  return NULL;
}

// TODO: heavy
int			CharacterManager::getCount(int professionId) {
  std::list<Character*>::iterator it;
  int count = 0;

  for (it = _characters->begin(); it != _characters->end(); ++it) {
	if ((*it)->getProfession().id == professionId) {
	  count++;
	}
  }

  return count;
}

const Profession*	CharacterManager::getProfessions() {
  return professions;
}

void    CharacterManager::update(int count) {
  std::list<Character*>::iterator it;

  for (it = _characters->begin(); it != _characters->end(); ++it) {
	(*it)->action();
    (*it)->update();
	(*it)->move();

	if (count % 10 == 0) {
	  (*it)->updateNeeds(count);
	}
  }
}

// TODO: heavy
Character*        CharacterManager::getCharacterAtPos(int x, int y) {
  Debug() << "getCharacterAtPos: " << x << "x" << y;
  std::list<Character*>::iterator it;

  for (it = _characters->begin(); it != _characters->end(); ++it) {
	if ((*it)->getX() == x && (*it)->getY() == y) {
      Debug() << "getCharacterAtPos: found";
      return *it;
    }
  }

  return NULL;
}

Character*		CharacterManager::assignJob(Job* job) {
  if (job == NULL) {
	Error() << "CharacterManager:: try to assign NULL job";
	return NULL;
  }

  std::list<Character*>::iterator it;
  Character* bestCharacter = NULL;

  int jobAction = job->getAction();

  for (it = _characters->begin(); it != _characters->end(); ++it) {
	if ((*it)->getJob() == NULL) {

	  if (bestCharacter == NULL) {
		bestCharacter = *it;
	  }

	  // build action -> only engineer
	  if (jobAction == JobManager::ACTION_BUILD && (*it)->getProfession().id == Character::PROFESSION_ENGINEER) {
		if (bestCharacter == NULL || (*it)->getProfessionScore(Character::PROFESSION_ENGINEER) > bestCharacter->getProfessionScore(Character::PROFESSION_ENGINEER)) {
		  bestCharacter = *it;
		}
	  }
	}
  }

  if (bestCharacter != NULL) {

	// TODO: remove if invalid

	// Action build
	if (job->getAction() == JobManager::ACTION_BUILD) {
	  BaseItem* jobItem = job->getItem();
	  BaseItem* item = WorldMap::getInstance()->getItem(job->getX(), job->getY());
	  WorldArea* area = WorldMap::getInstance()->getArea(job->getX(), job->getY());
	  if (item != NULL && item->isComplete() && area != NULL && area->isComplete()) {
		Error() << "CharacterManager: Job ACTION_BUILD on complete item";
		return NULL;
	  }
	  if (jobItem == NULL && item != NULL) {
		jobItem = item;
	  }
	  if (jobItem == NULL && area != NULL) {
		jobItem = area;
	  }
	  if (jobItem == NULL) {
		jobItem = WorldMap::getInstance()->putItem(job->getItemType(), job->getX(), job->getY());
	  }
	}

	// Action gather
	else if (job->getAction() == JobManager::ACTION_GATHER) {
	  BaseItem* jobItem = job->getItem();
	  if (jobItem == NULL) {
		Error() << "CharacterManager: Job ACTION_GATHER on missing item";
		return NULL;
	  }
	}

	Info() << "assign " << job->getId() << " to " << bestCharacter;
  	job->setCharacter(bestCharacter);
	bestCharacter->setJob(job);
  }

  return bestCharacter;
}

Character*		CharacterManager::add(int x, int y) {
  if (_count + 1 > LIMIT_CHARACTER) {
	Error() << "LIMIT_CHARACTER reached";
	return NULL;
  }

  Character* c = new Character(_count++, x, y);
  Profession profession = professions[_count % FUNCTIONS_COUNT];
  c->setProfession(profession);
  _characters->push_back(c);

  return c;
}

Character*		CharacterManager::add(int x, int y, int profession) {
  if (_count + 1 > LIMIT_CHARACTER) {
	Error() << "LIMIT_CHARACTER reached";
	return NULL;
  }

  Character* c = new Character(_count++, x, y);
  c->setProfession(profession);
  _characters->push_back(c);

  return c;
}

Character*		CharacterManager::getUnemployed(int professionId) {
  std::list<Character*>::iterator it;

  for (it = _characters->begin(); it != _characters->end(); ++it) {
	if ((*it)->getProfession().id == professionId && (*it)->getJob() == NULL) {
	  return *it;
	}
  }

  return NULL;
}

void	CharacterManager::refresh(sf::RenderWindow* app, sf::Transform transform, double animProgress) {
  sf::Sprite sprite;

  sf::RenderStates render(transform);

  // Selection
  sf::Texture texture;
  texture.loadFromFile("../sprites/cursor.png");
  sf::Sprite selection;
  selection.setTexture(texture);
  selection.setTextureRect(sf::IntRect(0, 32, 32, CHAR_HEIGHT));

  std::list<Character*>::iterator it;
  for (it = _characters->begin(); it != _characters->end(); ++it) {
	int posX = (*it)->getX() * TILE_SIZE - (CHAR_WIDTH - TILE_SIZE) + 2;
	int posY = (*it)->getY() * TILE_SIZE - (CHAR_HEIGHT - TILE_SIZE) + 0;
	int direction = (*it)->getDirection();

	// TODO: ugly
	int offset = 0;

	if (direction == Character::DIRECTION_TOP ||
		direction == Character::DIRECTION_BOTTOM ||
		direction == Character::DIRECTION_RIGHT ||
		direction == Character::DIRECTION_LEFT)
	  offset = (1-animProgress) * TILE_SIZE;

	if (direction == Character::DIRECTION_TOP_RIGHT ||
		direction == Character::DIRECTION_TOP_LEFT  ||
		direction == Character::DIRECTION_BOTTOM_RIGHT ||
		direction == Character::DIRECTION_BOTTOM_LEFT)
	  offset = (1-animProgress) * TILE_SIZE;

	switch (direction) {
	case Character::DIRECTION_BOTTOM: posY -= offset; break;
	case Character::DIRECTION_TOP: posY += offset; break;
	case Character::DIRECTION_RIGHT: posX -= offset; break;
	case Character::DIRECTION_LEFT: posX += offset; break;
	case Character::DIRECTION_BOTTOM_RIGHT: posY -= offset; posX -= offset; break;
	case Character::DIRECTION_BOTTOM_LEFT: posY -= offset; posX += offset; break;
	case Character::DIRECTION_TOP_RIGHT: posY += offset; posX -= offset; break;
	case Character::DIRECTION_TOP_LEFT: posY += offset; posX += offset; break;
	}
	
	if (direction == Character::DIRECTION_TOP_RIGHT)
	  direction = Character::DIRECTION_RIGHT;
	if (direction == Character::DIRECTION_TOP_LEFT)
	  direction = Character::DIRECTION_LEFT;
	if (direction == Character::DIRECTION_BOTTOM_RIGHT)
	  direction = Character::DIRECTION_RIGHT;
	if (direction == Character::DIRECTION_BOTTOM_LEFT)
	  direction = Character::DIRECTION_LEFT;

	// end ugly


	sprite.setPosition(posX, posY);
	int index = (*it)->getFrameIndex() / 20 % 4;
	if ((*it)->getNeeds()->isSleeping()) {
	  sprite.setTextureRect(sf::IntRect(0, CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT));
 	} else if (direction == Character::DIRECTION_NONE) {
	  sprite.setTextureRect(sf::IntRect(0, 0, CHAR_WIDTH, CHAR_HEIGHT));
	} else {
	  sprite.setTextureRect(sf::IntRect(CHAR_WIDTH * index, CHAR_HEIGHT * direction, CHAR_WIDTH, CHAR_HEIGHT));
	}
	sprite.setScale(0.8f, 0.8f);

	int functionId = (*it)->getProfession().id;
	switch (functionId) {
	case Character::PROFESSION_SECURITY:
	  sprite.setTexture(*_textures[1]);
	  break;
	case Character::PROFESSION_ENGINEER:
	  sprite.setTexture(*_textures[2]);
	  sprite.setTextureRect(sf::IntRect(0, 0, CHAR_WIDTH, 32));
	  sprite.setScale(1.0f, 1.0f);
	  break;
	default:
	  sprite.setTexture(*_textures[0]);
	  break;
	}

	app->draw(sprite, render);

	// Selection
	if ((*it)->getSelected()) {
	  selection.setPosition(posX, posY);
	  app->draw(selection, render);
	}
  }
}

sf::Sprite*	CharacterManager::getSprite(sf::Sprite* sprite, int functionId, int index) {

  switch (functionId) {
  case Character::PROFESSION_SECURITY:
	sprite->setTexture(*_textures[1]);
	break;
  default:
	sprite->setTexture(*_textures[0]);
	break;
  }

  std::list<Character*>::iterator it;
  for (it = _characters->begin(); it != _characters->end(); ++it) {
	int posX = (*it)->getX() * TILE_SIZE - (CHAR_WIDTH - TILE_SIZE);
	int posY = (*it)->getY() * TILE_SIZE - (CHAR_HEIGHT - TILE_SIZE + TILE_SIZE / 2);

	// Sprite
	sprite->setPosition(posX, posY);
	if ((*it)->getNeeds()->isSleeping()) {
	  sprite->setTextureRect(sf::IntRect(0, CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT));
	} else {
	  sprite->setTextureRect(sf::IntRect(CHAR_WIDTH * (index % 4), 0, CHAR_WIDTH, CHAR_HEIGHT));
	}

	return sprite;
  }
}
