#include <sstream>
#include <fstream>
#include <iostream>
#include <glib.h>
#include <map>

using namespace std;

#include <SFML/Graphics.hpp>

extern sf::RenderWindow	*app;

#include "Layer.hpp"
#include "Log.hpp"

/** Gestions des layers d'une scene
 * contient:
 * (map<map>)data	-- disposition des elements sur le layer
 * tile[]		-- sprite decoupe en surfaces
 */

#warning taille des sprite en dur

Layer::Layer(std::string pmap, int height, int width, int level):
	 height(height), width(width), level(level), path_map(pmap)
{
	 this->load();
}

Layer::~Layer()
{
	 this->unload();
}

void	Layer::reload()
{
	 this->unload();
	 this->load();
}

void	Layer::load()
{
	 try
	 {
			this->loadMap();
	 }
	 catch (char const *str)
	 {
			std::cout << "Catched [Layer]: " << str << std::endl;
	 }
 
	 try
	 {
			this->loadSprite();
	 }
	 catch (char const *str)
	 {
			std::cout << "Catched [Layer]: " << str << std::endl;
	 }

	 //  this->show_map();

	 for (uint j = 0; j < this->height; j++)
	 {
			for (uint i = 0; i < this->width; i++)
			{
				 if (j < this->data.size() &&
						 i < this->data[j].size() &&
						 this->data[j][i])
				 {
						this->tile[this->data[j][i]].setPosition(TILE_SIZE * i, TILE_SIZE * j);
						app->draw(this->tile[this->data[j][i]]);
				 }
			}
	 }

	 std::cout << SHELL_BLUE << "Layer loaded ("
						 << this->path_sprite << ", " << this->path_map << ", "
						 << this->height << "x" << this->width << ", bg:" << this->level
						 << ")" << SHELL_PLAIN << std::endl;
}

void	Layer::draw(int x, int y, int player_x, int player_y)
{
	 for (int j = 0; j < this->height; j++)
	 {
			for (int i = 0; i < this->width; i++)
			{
				 if (j > player_y - SMOG_DISTANCE_Y &&
						 j < player_y + SMOG_DISTANCE_Y &&
						 i > player_x - SMOG_DISTANCE_X &&
						 i < player_x + SMOG_DISTANCE_X &&
						 j < this->data.size() &&
						 i < this->data[j].size() && (this->data[j][i]))
				 {
						int cur_tile = this->data[j][i];
						this->tile[cur_tile].setPosition(TILE_SIZE * i + x, TILE_SIZE * j + y);
						app->draw(this->tile[cur_tile]);
				 }
			}
	 }
}

void	Layer::show_map()
{
	 for (uint j = 0; j < this->height; j++)
	 {
      for (uint i = 0; i < this->width; i++)
			{
				 printf("%02d ", this->data[j][i]);
			}
      printf("\n");
	 }
	 printf("\n############\n");
}

void	Layer::unload()
{
	 delete _image;

	 std::cout << SHELL_RED << "Layer deleted ("
						 << "sprite: " << this->path_sprite << ", "
						 << ", map: " << this->path_map
						 << ", bg:" << this->level
						 << ")" << SHELL_PLAIN << std::endl;
}

/** Layer::load_map()
 *  Chargement de la structure de la map en memoire
 */

void	Layer::loadMap() {
  ifstream	file;
  string	s_line;
  char		buf[6];

  file.open(this->path_map.c_str());
  if (file.is_open()) {
	// Dump
#warning chaque layer contient la structure de tout les layer du bg
#warning aucune verif de l integrite du fichier
	for (uint cur_layer = 0; cur_layer <= level; cur_layer++) {
	  getline(file, this->path_sprite);
	  for (uint cur_line = 0; getline(file, s_line) && s_line.length(); cur_line++) {
		if (cur_layer == this->level) {
		  for (uint c = 0, i = 0; c < s_line.length() && i < this->width; i++, c++) {
			for (uint j = 0; c <= s_line.length() && s_line[c] >= '0' && s_line[c] <= '9'; c++, j++) {
			  buf[j] = s_line[c];
			  buf[j+1] = 0;
			}
			if (buf[0] < '0' || buf[0] > '9') {
			  throw (std::string("map file: invalid (").append(this->path_map).append(")").c_str());
			}
			this->data[cur_line][i] = atoi(buf);
		  }
		}
	  }
	}
	file.close();
  }
  else {
	throw (std::string("unable to open map file -> ").append(this->path_map).c_str());
  }
}

void	Layer::loadSprite() {
  sf::Texture *texture = new sf::Texture;
  if (!texture->loadFromFile(this->path_sprite.c_str()))
	throw (string("failed to open: ").append(this->path_sprite).c_str());

  texture->setSmooth(false);
  for (int i = 0; i < NB_TILE_BY_SPRITE; i++)
	{
      this->tile[i+1].setTexture(*texture);
      this->tile[i+1].setTextureRect(sf::IntRect(TILE_SIZE * (i % SPRITE_WIDTH),
												 TILE_SIZE * (i / SPRITE_WIDTH),
												 TILE_SIZE,
												 TILE_SIZE));
	}
}

#warning la gestion de la colision du personnage avec les bords de la map ne devrait pas se faire a ce niveau
int	Layer::get_area(int x, int y)
{
	 if (x < 0 || y < 0)
			throw ("try to get overbound area");

	 if (x >= (int)this->width || y+1 >= (int)this->height)
			throw ("try to get overbound area");

	 if (y+1 >= (int)this->data.size() || x >= (int)this->data[y+1].size())
			throw ("try to get overbound area");

	 return (this->data[y+1][x]);
}
