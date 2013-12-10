#include <iostream>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "CharacterManager.h"

extern WorldMap* gl_worldmap;

CharacterManager::CharacterManager() {
  _characters = new std::list<Character*>();
}

CharacterManager::~CharacterManager() {
}

Character*		CharacterManager::add(int x, int y) {
  Character* c = new Character(x, y);

  _characters->push_back(c);

  return c;
}

Character*		CharacterManager::getUnemployed() {
  std::list<Character*>::iterator it;

  for (it = _characters->begin(); it != _characters->end(); ++it) {
	if ((*it)->getJob() == NULL) {
	  _characters->erase(it);
	  _characters->push_back(*it);
	  return *it;
	}
  }

  return NULL;
}

void	CharacterManager::move() {
  std::list<Character*>::iterator it;

  for (it = _characters->begin(); it != _characters->end(); ++it) {
	(*it)->move();
  }
}

void	CharacterManager::draw(sf::RenderWindow* app, sf::Transform transform) {
  sf::Texture texture;
  texture.loadFromFile("../sprites/cless.png");
  texture.setSmooth(true);

  sf::Sprite sprite;
  sprite.setTexture(texture);
  sprite.setTextureRect(sf::IntRect(0, 0, TILE_SIZE, TILE_SIZE));

  sf::RenderStates render(transform);

  std::list<Character*>::iterator it;
  for (it = _characters->begin(); it != _characters->end(); ++it) {
	sprite.setPosition((*it)->_posX * TILE_SIZE, (*it)->_posY * TILE_SIZE);
	app->draw(sprite, render);
  }
}
