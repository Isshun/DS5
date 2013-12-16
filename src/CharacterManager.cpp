#include <iostream>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "CharacterManager.h"

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

  Character* c;
  while ((c = _characters->front()) != NULL) {
	delete c;
  }

  delete _characters;
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

Character*		CharacterManager::add(int x, int y) {
  if (_count + 1 > LIMIT_CHARACTER) {
	Error() << "LIMIT_CHARACTER reached";
	return NULL;
  }

  Character* c = new Character(_count++, x, y);

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

void	CharacterManager::draw(sf::RenderWindow* app, sf::Transform transform) {
  sf::Sprite sprite;

  sf::RenderStates render(transform);

  // Selection
  sf::Texture texture;
  texture.loadFromFile("../sprites/cursor.png");
  sf::Sprite selection;
  selection.setTexture(texture);
  selection.setTextureRect(sf::IntRect(0, 32, 32, CHAR_HEIGHT));
  selection.setTextureRect(sf::IntRect(0, 32, 32, CHAR_HEIGHT));

  std::list<Character*>::iterator it;
  for (it = _characters->begin(); it != _characters->end(); ++it) {
	int posX = (*it)->_posX * TILE_SIZE - (CHAR_WIDTH - TILE_SIZE) + 2;
	int posY = (*it)->_posY * TILE_SIZE - (CHAR_HEIGHT - TILE_SIZE) + 0;

	// Sprite
	sprite.setPosition(posX, posY);
	if ((*it)->isSleep()) {
	  sprite.setTextureRect(sf::IntRect(0, CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT));
	} else {
	  sprite.setTextureRect(sf::IntRect(0, 0, CHAR_WIDTH, CHAR_HEIGHT));
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

sf::Sprite*	CharacterManager::getSprite(sf::Sprite* sprite, int functionId) {

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
	int posX = (*it)->_posX * TILE_SIZE - (CHAR_WIDTH - TILE_SIZE);
	int posY = (*it)->_posY * TILE_SIZE - (CHAR_HEIGHT - TILE_SIZE + TILE_SIZE / 2);

	// Sprite
	sprite->setPosition(posX, posY);
	if ((*it)->isSleep()) {
	  sprite->setTextureRect(sf::IntRect(0, CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT));
	} else {
	  sprite->setTextureRect(sf::IntRect(0, 0, CHAR_WIDTH, CHAR_HEIGHT));
	}

	return sprite;
  }
}
