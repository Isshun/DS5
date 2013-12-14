#include <iostream>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "CharacterManager.h"

CharacterManager::CharacterManager() {
  Debug() << "CharacterManager";

  _characters = new std::list<Character*>();
  _count = 0;

  _textures[0] = new sf::Texture();
  _textures[0]->loadFromFile("../sprites/cless.png");
  _textures[0]->setSmooth(true);

  Debug() << "CharacterManager done";
}

CharacterManager::~CharacterManager() {
  delete _textures[0];

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
	(*it)->updateNeeds();
    (*it)->update();
	(*it)->move();
  }
}

Character*        CharacterManager::getCharacterAtPos(int x, int y) {
  std::cout << "getCharacterAtPos: " << x << "x" << y << std::endl;
  std::list<Character*>::iterator it;

  for (it = _characters->begin(); it != _characters->end(); ++it) {
	if ((*it)->getX() == x && (*it)->getY() == y) {
      std::cout << "found" << std::endl;
      return *it;
    }
  }

  return NULL;
}

Character*		CharacterManager::add(int x, int y) {
  Character* c = new Character(_count++, x, y);

  _characters->push_back(c);

  return c;
}

Character*		CharacterManager::getUnemployed() {
  std::list<Character*>::iterator it;

  for (it = _characters->begin(); it != _characters->end(); ++it) {
	if ((*it)->getJob() == NULL) {
	  Character* c = *it;
	  _characters->push_back(c);
	  _characters->erase(it);
	  return c;
	}
  }

  return NULL;
}

void	CharacterManager::draw(sf::RenderWindow* app, sf::Transform transform) {
  sf::Sprite sprite;
  sprite.setTexture(*_textures[0]);

  sf::RenderStates render(transform);

  std::list<Character*>::iterator it;
  for (it = _characters->begin(); it != _characters->end(); ++it) {
	sprite.setPosition((*it)->_posX * TILE_SIZE, (*it)->_posY * TILE_SIZE);
	if ((*it)->isSleep()) {
	  sprite.setTextureRect(sf::IntRect(32, 0, TILE_SIZE, TILE_SIZE));
	} else {
	  sprite.setTextureRect(sf::IntRect(0, 0, TILE_SIZE, TILE_SIZE));
	}
	app->draw(sprite, render);
  }
}
