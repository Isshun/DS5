#include <iostream>

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>

#include "defines.h"
#include "Game.hpp"

const char				*arg1;

#define Uint32 int

// Uint32	refresh_game(Uint32 time, void *data)
// {
// 	 Game	*game = (Game*)data;

// 	 if (game->is_run())
// 	 {
// 			game->update();
// 	 }

// 	 return (time);
// }

// Uint32	refresh_frame(Uint32 time, void *data)
// {
// 	 Game *game = (Game*)data;
	 
// 	 if (game->is_run())
// 	 {
// 			game->refresh(sf::RenderWindow* app);
// 	 }
	 
// 	 return (time);
// }

// Uint32	refresh_fps(Uint32 time, void*)
// {
// 	return 0;
// }

void	init_timer(Game *game)
{
}

int main(int argc, char *argv[])
{
  sf::RenderWindow* app = new sf::RenderWindow(sf::VideoMode(1600 / 2, 900 / 2, 32), NAME);
  app->setKeyRepeatEnabled(true);

  sf::View view = app->getDefaultView();

  view.setViewport(sf::FloatRect(0.f, 0.f, 1.0f, 1.0f));

  // view.setCenter(400, 200);

  // view.zoom(zoom);

  app->setView(view);

  if (argc > 1)
    arg1 = argv[1];

  // load game
  Game	*game = new Game(app);

  game->loop();

  return EXIT_SUCCESS;
}

