#ifndef _C_CHARACTERMANAGER_
#define _C_CHARACTERMANAGER_

#include <ctime>
#include <map>
#include <list>
#include "defines.h"
#include "WorldMap.h"
#include "Character.h"

class	CharacterManager
{
 public:
	CharacterManager();
	~CharacterManager();

	Character*	add(int x, int y);
	Character*	getUnemployed(int professionId);
	void		draw(sf::RenderWindow* app, sf::Transform transform);
    void        update(int count);
    Character*  getCharacterAtPos(int x, int y);
	std::list<Character*>*		getList() { return _characters; };
	sf::Sprite*	getSprite(sf::Sprite* sprite, int functionId);

 private:
	std::list<Character*>*		_characters;
	sf::Texture*				_textures[10];
	int							_count;
};

#endif
