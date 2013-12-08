#ifndef _C_SCENE_
#define _C_SCENE_

#include "Character.hpp"
#include "Layer.hpp"
#include "tosng.h"
#include "WorldMap.h"

class	Scene
{
 public:
  Scene();
  ~Scene();

  void	loop();
  void	update();
  void	refresh();

  bool	get_pause(){return this->pause;}
  const char	*get_name(){return this->name.c_str();}

//   // Events
  void	gere_key();
//   voidgere_mouse();

  bool	get_physique(int x, int y);

  const char	*get_music_path(){return this->path_music.c_str();}
  
  void	load_info();

  void	reload();


  // BG layer actions
  void	toogle_bg(int n_bg);
  void	reload_bg(int n_bg);
  void	unload_bg(int n_bg);
  void	load_bg(int n_bg);

  void	drawInfo();

  void	pnj_update();

public:
  Layer		*bg[5];
  Layer		*clip;

  //debug
  int		timer_start;
  int		timer_end;

private:
  // Info
  std::string	name;
  std::string	description;
  std::string	path_scene;
  std::string	path_sprite;
  std::string	path_map;
  std::string	path_music;
  int	height;
  int	width;
  int	nb_layer;


//   SDL_Surface	*scene_surface;

  sf::Font MyFont;

//   intplayer;
//   char*team_name[NB_MAX_TEAM];
//   SDL_Surface*team_img[4][18];
//   SDL_Surface	*window;
// //   intnb_player;
//   SDL_Event	event;
  bool		up_to_date;
  bool		pause;
  bool	run;


//   intfps;
};

#endif
