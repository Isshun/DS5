#include <sstream>
#include <fstream>
#include <iostream>
#include "Scene.hpp"

#include <SFML/Graphics.hpp>

extern sf::RenderWindow	*app;

extern sf::Time _time_elapsed;

// extern SDL_Surface *fps;
// extern Music	*music;
// extern int	old_time1;
// extern int	old_time2;

#define	MARGIN_PLAYER	(TILE_SIZE/2)

extern sf::RenderWindow	*app;

//fixme: le nombre de layer va etre reduis, surrement a 3 + clip (bg1>bg3 + bg0):
// fond + mur arriere plan
// elem decors arriere plan
// => player <=
// elem decors premier plan + mur premier plan
//edit: enfaite il sagit simplement de merge [sol] et [mur arriere plan]
Scene::Scene():
  run(true)
{
//  // Load du fichier scene
//  this->load_info();
//
//  // Load des layer
//  this->bg[0] = new Layer(this->path_map, this->height, this->width, 0);
//  this->bg[1] = new Layer(this->path_map, this->height, this->width, 1);
//  this->bg[2] = new Layer(this->path_map, this->height, this->width, 2);
//  this->bg[3] = new Layer(this->path_map, this->height, this->width, 3);
//  this->bg[4] = new Layer(this->path_map, this->height, this->width, 4);
//  this->clip = this->bg[0];
//
//  // Load des PNJ
//  this->pnj = new Player("sprites/cless.png");
//
////   this->scene_surface = SDL_CreateRGBSurface(SDL_HWSURFACE, VIDEO_WINDOW_W, VIDEO_WINDOW_H, 16, 0, 0, 0, 0);
//
//  if (!MyFont.loadFromFile("snap/complete.ttf"))
//    throw(std::string("failed to load: ").append("snap/complete.ttf").c_str());
//
//
//  std::cout << "Scene:\tdone (" << path_scene << ")" << std::endl;
//  this->pause = false;
}

Scene::~Scene()
{
//   delete this->bg[0];
//   delete this->bg[1];
//   delete this->bg[2];
//   delete this->bg[3];
//   delete this->bg[4];
}



/** Load des meta data de la scene
 * ex:
 * +----------------------------------------------------+
 * | totus_ext_1		-- nom de la scene	|
 * | Totus Village		-- description		|
 * | 33				-- hauteur		|
 * | 30				-- largeur		|
 * | 5				-- nombre de layers	|
 * | maps/totus.map		-- fichier map (layers)	|
 * | musics/totus.ogg		-- fichier son		|
 * +----------------------------------------------------+
 */
void	Scene::load_info()
{
  std::ifstream	file;
  std::string	s_line;

  file.open(this->path_scene.c_str());
  if (file.is_open())
    {
      getline(file, this->name);
      getline(file, this->description);
      getline(file, s_line); this->height   = atoi(s_line.c_str());
      getline(file, s_line); this->width    = atoi(s_line.c_str());
      getline(file, s_line); this->nb_layer = atoi(s_line.c_str());
      getline(file, this->path_map);
      getline(file, this->path_music);
      file.close();
    }
  else
    throw (this->path_scene.c_str());

  std::cout << std::endl << std::endl
	    << "######## Scene: " << this->name << " ########"
	    << std::endl
	    << "  size: " << this->height << "x" << this->width
	    << ", nb_layer: " << this->nb_layer
	    << std::endl
	    << "  path (map: " << this->path_map
	    << ", music: " << this->path_music << ")"
	    << std::endl;
}

void	Scene::update()
{
  this->up_to_date = false;
}

/** Retourne un SDL_Surface contenant la scene:
 *
 *  Bg 4   -- murs premier plan
 *  NPC
 *  Player
 *  Bg 3   -- elements de decor
 *  Bg 2   -- murs arriere plan
 *  Bg 1   -- sol
 *  Bg 0   -- clipping
 */
void	Scene::pnj_update()
{
  static int pnj_pos = 0;

  //std::cout << "PNJ update" << std::endl;

}

// void	Scene::draw_surface()
// {

// 	sf::Texture texture;
// 	texture.loadFromFile("sprites/house_in_1.png");
// 	texture.setSmooth(true);

// 	int w = worldMap->getWidth();
// 	int h = worldMap->getHeight();


// 	for (int i = 0; i < w; i++) {
// 		for (int j = 0; j < h; j++) {
// 			BaseItem* item = worldMap->getItem(i, j);
// 			if (item != 0) {
// 			  sf::Sprite sprite;
// 			  sprite.setTexture(texture);
// 			  switch (item->type) {
// 			  case BaseItem::HULL:
// 				sprite.setTextureRect(sf::IntRect(32, 32, 30, 30));
// 				break;
// 			  default:
// 				sprite.setTextureRect(sf::IntRect(64, 64, 30, 30));
// 				break;
// 			  }
// 			  sprite.setPosition(i * 32, j * 32);
// 			  app->draw(sprite);
// 			} else {
// 			  sf::Sprite sprite;
// 			  sprite.setTexture(texture);
// 			  sprite.setTextureRect(sf::IntRect(0, 0, 30, 30));
// 			  sprite.setPosition(i * 32, j * 32);
// 			  app->draw(sprite);
// 			}
// 		}
// 	}
// }

// void	Scene::drawInfo()
// {
//   std::stringstream ss;

//   sf::Text Text;
//   Text.setString(ss.str());
//   Text.setFont(MyFont);
//   Text.setCharacterSize(18);
//   Text.setStyle(sf::Text::Regular);
  
//   //sf::String Text(ss.str(), MyFont, 18);
//   Text.setPosition(20, 12);
//   app->draw(Text);


//   std::stringstream ss_fps;
//   ss_fps << "fps: " << 1 / _time_elapsed.asMilliseconds();

//   sf::Text Fps;
//   Fps.setString(ss_fps.str());
//   Fps.setFont(MyFont);
//   Fps.setCharacterSize(18);
//   Fps.setStyle(sf::Text::Regular);
//   //  sf::String Fps(ss_fps.str(), MyFont, 18);
//   Fps.setPosition(150, 12);
//   app->draw(Fps);

//   // layers info
//   std::stringstream ss_layer;
//   ss_layer << "layers: ";
//   for (int i = 0; i < nb_layer; i++)
//     ss_layer << (bg[i] ? "1" : "0");
//   //sf::String Layer(ss_layer.str(), MyFont, 18);
//   sf::Text Layer;
//   Layer.setString(ss_layer.str());
//   Layer.setFont(MyFont);
//   Layer.setCharacterSize(18);
//   Layer.setStyle(sf::Text::Regular);
//   Layer.setPosition(20, 24);
//   app->draw(Layer);

// //  //player info
// //  std::stringstream ss_player;
// //  ss_player << "player: " << (player ? "1" : "0");
// //  //sf::String Player(ss_player.str(), MyFont, 18);
// //  sf::Text Player;
// //  Player.setString(ss_player.str());
// //  Player.setFont(MyFont);
// //  Player.setCharacterSize(18);
// //  Player.setStyle(sf::Text::Regular);
// //  Player.setPosition(150, 24);
// //  app->draw(Player);


//   //timer - debug
//   if (this->timer_start)
//     {
//       std::stringstream ss_timer;
//       if (this->timer_end)
// 	ss_timer << "timer: " << (this->timer_end - this->timer_start) << " [stop]";
//       else
// 	ss_timer << "timer: " << (time(0) - this->timer_start) << " [play]";

// 	  sf::Text Timer;
// 	  Timer.setString(ss_timer.str());
// 	  Timer.setFont(MyFont);
// 	  Timer.setCharacterSize(18);
// 	  Timer.setStyle(sf::Text::Regular);
// 	  Timer.setPosition(150, 24);
//       //sf::String Timer(ss_timer.str(), MyFont, 18);
//       Timer.setPosition(20, 36);
//       app->draw(Timer);
//     }
// }

// void	Scene::reload()
// {
//   this->load_info();
//   this->reload_bg(0);
//   this->reload_bg(1);
//   this->reload_bg(2);
//   this->reload_bg(3);
//   this->reload_bg(4);
// }


// // fixme: inutile en l'etat
// // recup les event de la scene dans le gere_key de Game et les placer ici
// void	Scene::gere_key()
// {
// //   if (event.type == SDL_KEYUP)
// //     player->set_run(false);

// //   if (event.type != SDL_KEYDOWN)
// //     return;
// }




// // fixme: redondant avec physique_layer
// // fixme: a clarifier
// bool	Scene::get_physique(int x, int y)
// {
//   if (x < 0 || y < 0)
//     return false;

//   if (x > this->width || y > this->height)
//     return false;

//   try {
//     if (bg[0])
//       return !this->bg[0]->get_area(x, y);
//   } catch (const std::string str) {
//     std::cout << "Catched [Scene]: " << str << "x=" << x << " y=" << y << std::endl;
//   } catch (const char *str) {
//     std::cout << "Catched [Scene]: " << str << "x=" << x << " y=" << y << std::endl;
//   }

//   return false;
// }






// /**
//  **   Gestion des layers
//  **/

// void	Scene::toogle_bg(int n_bg)
// {
//   if (this->bg[n_bg] == NULL)
//     this->load_bg(n_bg);
//   else
//     this->unload_bg(n_bg);
// }

// void	Scene::reload_bg(int n_bg)
// {
//   try {
//     this->unload_bg(n_bg);
//   } catch (const char *str) {
//     std::cout << "[reload_bg] an exception occurred: " << str << "(#" << n_bg << ")" << std::endl;
//   }

//   try {
//     this->load_bg(n_bg);
//   } catch (const char *str) {
//     std::cout << "[reload_bg] an exception occurred: " << str << "(#" << n_bg << ")" << std::endl;
//   }
  
// }

// void	Scene::unload_bg(int n_bg)
// {
//   if (this->bg[n_bg] == NULL)
//     throw("[unload_bg] try to unload NULL intance of Layer, abort");

//   delete this->bg[n_bg];
//   this->bg[n_bg] = NULL;
// }

// void	Scene::load_bg(int n_bg)
// {
//   if (this->bg[n_bg] != NULL)
//     throw("[load_bg] try to load a new intance over pre-existing layer, abort");

//   this->bg[n_bg] = new  Layer(path_map, this->height, this->width, n_bg);
// }
