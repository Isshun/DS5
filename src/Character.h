#ifndef _C_CHARACTER_
#define _C_CHARACTER_

#include <ctime>
#include <map>
#include <list>
#include "defines.h"
#include "WorldMap.h"
#include "MapSearchNode.h"

using namespace std;

class	Character
{
 public:
	Character(int x, int y);
	~Character();

  void	draw(sf::RenderWindow* app, sf::Transform transform);
  void	build(BaseItem* item);
  
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
  void*	getJob() { return _job; }

  map<int,int>	_run;

  int	_last_direction;
  int	current_frame;
  sf::Vector2<int>		position;

  int	_posX;
  int	_posY;

private:
  const char	*path;
  AStarSearch<MapSearchNode>* _astarsearch;
  int	_steps;
  void*	_job;
};

#endif
