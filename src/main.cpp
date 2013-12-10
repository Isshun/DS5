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
