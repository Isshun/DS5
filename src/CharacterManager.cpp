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
    (*it)->update();
	(*it)->move();

	if (count % 10 == 0) {
	  (*it)->updateNeeds(count);
	}
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
  if (_count + 1 > LIMIT_CHARACTER) {
	Error() << "LIMIT_CHARACTER reached";
	return NULL;
  }

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
	sprite.setPosition((*it)->_posX * TILE_SIZE - (CHAR_WIDTH - TILE_SIZE),
					   (*it)->_posY * TILE_SIZE - (CHAR_HEIGHT - TILE_SIZE + TILE_SIZE / 4));
	if ((*it)->isSleep()) {
	  sprite.setTextureRect(sf::IntRect(0, CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT));
	} else {
	  sprite.setTextureRect(sf::IntRect(0, 0, CHAR_WIDTH, CHAR_HEIGHT));
	}
	app->draw(sprite, render);
  }
}
