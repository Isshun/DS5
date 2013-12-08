#ifndef _LAYER_HPP_
#define _LAYER_HPP_

#include <map>

#include <SFML/Graphics.hpp>

#include "tosng.h"

#define NB_TILE_BY_SPRITE	5000

using namespace std;

typedef map<int,int> line;

class Layer
{
public:
  Layer(string pmap, int height, int width, int level);
  ~Layer();
//   SDL_Surface	*get_layer(){return this->layer;}

  void	draw(int x, int y, int player_x, int player_y);
  const char	*get_name(){return this->name.c_str();}
  int		get_area(int x, int y);

  void		reload();

private:
  void		load();
  void		loadMap();
  void		loadSprite();
  void		unload();
  void		show_map();

private:
  unsigned int		height;
  unsigned int		width;
  unsigned int		level;
  string		name;
//   SDL_Surface		*layer;
  sf::Image	*_image;
  map<int,sf::Sprite>	tile;
  map<int,line>		data;
  string		path_sprite;
  string		path_map;
};

#endif
