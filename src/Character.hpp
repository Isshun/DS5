#ifndef _C_CHARACTER_
#define _C_CHARACTER_

#include <ctime>
#include <map>
#include <list>
#include "defines.h"
#include "WorldMap.h"
#include "MapSearchNode.h"

using namespace std;

typedef map<int, sf::Sprite>	map_direction;

//fixme: intile quand update_position clean
class Scene;

class	Character
{
 public:
	Character(int x, int y);
	~Character();

  void	update_position(Scene *scene);
  void draw(sf::RenderWindow* app);

  // Gets
  sf::Vector2<int>	&get_position();
  sf::Sprite	&get_sprite();
  int		getRun();
  int		getDirection();

  void		go(int toX, int toY);

  void		move();

  // Sets
  void	set_direction(int direction);
  void	setRun(int direction, bool run);
  void	set_position(int x, int y);

  void	draw(int x, int y);

  map<int,int>	_run;

  int	_last_direction;
  int	current_frame;
  map<int,map_direction>	tile;
  sf::Vector2<int>		position;

  int	_posX;
  int	_posY;

private:
  const char	*path;
  AStarSearch<MapSearchNode>* _astarsearch;
  int	_steps;
};

#endif
