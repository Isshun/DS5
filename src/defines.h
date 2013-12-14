#ifndef _TOSNG_H
#define _TOSNG_H_

#include "Log.h"

#define CHARACTER_MOVE_INTERVAL 1
#define KEY_REPEAT_INTERVAL 5

#define	VIDEO_ZOOM	1
#define VIDEO_WINDOW_W	320
#define VIDEO_WINDOW_H	240
#define VIDEO_FPS	99
#define PLAYER_STEP	15
#define GAME_SPEED	20
#define TILE_SIZE	32
#define SPRITE_WIDTH	16

#define DEC_X	(VIDEO_WINDOW_W / VIDEO_ZOOM - (TILE_SIZE/2))
#define DEC_Y	(VIDEO_WINDOW_H / VIDEO_ZOOM - TILE_SIZE)
// avant: 8, 16


#define SMOG_DISTANCE_X	100
#define SMOG_DISTANCE_Y	70

// Usless
#define	N_RES_X	320
#define	RES_X	800
#define	ZOOM_X	(RES_X / N_RES_X)

#define	N_RES_Y	240
#define	RES_Y	600
#define	ZOOM_Y	(RES_Y / N_RES_Y)
// Usless

#define NAME	"top_scene"

#define UP	1
#define RIGHT	2
#define	DOWN	3
#define LEFT	4

#define SHELL_PLAIN	"\33[0m"
#define SHELL_RED	"\33[1m\33[31m"
#define SHELL_GREEN	"\33[32m"
#define SHELL_ORANGE	"\33[33m"
#define SHELL_BLUE	"\33[1m\33[34m"

#define	UI_WIDTH 300
#define	UI_HEIGHT 100
#define	UI_PADDING 10
#define UI_FONT_SIZE 32

#define TILE_SIZE 32

#define WINDOW_WIDTH 1600
#define WINDOW_HEIGHT 900

#define RESSOURCE_MATTER_START 50000

#define CHARACTER_INIT_FOOD 80
#define CHARACTER_INIT_OXYGEN 100
#define CHARACTER_INIT_HAPINESS 80
#define CHARACTER_INIT_HEALTH 80;
#define CHARACTER_INIT_ENERGY 100;

#endif
