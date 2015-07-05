package org.smallbox.faraway.util;

import org.smallbox.faraway.engine.Color;

public class Constant {

	public static final boolean DEBUG = true;
	public static final double 	BODY_TEMPERATURE = 37.6;
	public static final int 	PLANET_WIDTH = 100;
	public static final int 	PLANET_HEIGHT = 90;
	public static String 		NAME	= "FarPoint";
	public static String 		VERSION	= "0.1";

	public static final int 	BASE_WIDTH			= 1280;
	public static final int 	BASE_HEIGHT			= 800;

	public static final int 	WINDOW_WIDTH			= 1920;
	public static final int 	WINDOW_HEIGHT			= 1200;

	public static final int 	KEY_REPEAT_INTERVAL		= 5;
	public static final int 	WORLD_MAX_SIZE			= 1000;
	public static final int 	LIMIT_ITEMS				= 10000;
	public static final int 	LIMIT_CHARACTER			= 500;
	public static final int 	MENU_TILE_WIDTH			= 240;
	public static final int 	MENU_TILE_HEIGHT		= 120;
	public static final int 	UI_WIDTH				= 300;
	public static final int 	UI_HEIGHT				= 100;
	public static final int 	UI_PADDING				= 10;
	public static final int 	UI_PADDING_H			= 16;
	public static final int 	UI_PADDING_V			= 10;
	public static final int 	UI_FONT_SIZE			= 32;
	public static final int 	TILE_WIDTH				= 32;
	public static final int 	TILE_HEIGHT				= 32;
	public static final int 	CHAR_HEIGHT				= 32;
	public static final int 	CHAR_WIDTH				= 32;
	public static final int 	RESSOURCE_MATTER_START 	= 50000;
	public static final int 	CHARACTER_INIT_FOOD 	= 80;
	public static final int 	CHARACTER_INIT_OXYGEN 	= 100;
	public static final int 	CHARACTER_INIT_HAPPINESS =80;
	public static final int 	CHARACTER_INIT_HEALTH 	= 80;
	public static final int 	CHARACTER_INIT_ENERGY 	= 0;
	public static final int 	FONT_SIZE 				= 20;
	public static final int 	PANEL_WIDTH 			= 420;
	public static final int 	PANEL_HEIGHT 			= WINDOW_HEIGHT;
	public static final int 	DURATION_MULTIPLIER 	= 10;
	public static final int 	CHANCE_TO_GET_MEETING_AREA_WHEN_JOBLESS = 0;
	public static final int 	CHARACTER_INVENTORY_SPACE = 20;
	public static final double 	CHARACTER_GROW_PER_UPDATE = 0.001;
	public static final int 	CHARACTER_DELAY_BEFORE_FIRST_CHILD = 2;
	public static final int		CHARACTER_DELAY_BETWEEN_CHILDS = 3;
	public static final int 	CHARACTER_MAX_CHILD = 4;
	public static final int 	CHARACTER_CHILD_MIN_OLD = 2;
	public static final int 	CHARACTER_CHILD_MAX_OLD = 42;
	public static final int 	SLOW_UPDATE_INTERVAL 	= 20;
	public static final int 	CHARACTER_DATE_MIN_OLD 	= 14;
	public static final double 	CHARACTER_MAX_OLD 		= 80;
	public static final int 	CHARACTER_STAY_IN_METTING_ROOM = 10;
	public static final double 	CHARACTER_LEAVE_HOME_OLD = 18;
	public static final int 	RESSOURCE_LOW_FOOD 		= 10;
	public static final int 	NB_COLUMNS = 47;
	public static final int 	ITEM_MAX_WIDTH = 5;
	public static final int 	ITEM_MAX_HEIGHT = 4;
	public static final int 	COUNT_BEFORE_REUSE_BLOCKED_ITEM = 10;
	public static final int 	DELAY_TO_RESTART_BLOCKED_JOB = COUNT_BEFORE_REUSE_BLOCKED_ITEM;
	public static final int     NB_COLUMNS_TITLE = 29;

	public static final Color COLOR_JOB_BUILD = new Color(170, 128, 64);
	public static final Color COLOR_JOB_MOVE = Color.CYAN;
	public static final Color COLOR_JOB_GATHER = Color.GREEN;
	public static final Color COLOR_JOB_MINING = Color.GREEN;
	public static final Color COLOR_JOB_WORK = Color.GREEN;
	public static final Color COLOR_JOB_REFILL = Color.GREEN;
	public static final Color COLOR_JOB_NONE = Color.BLACK;
	public static final Color COLOR_JOB_USE_INVENTORY = Color.BLUE;
	public static final Color COLOR_JOB_USE = Color.BLUE;
	public static final Color COLOR_JOB_DESTROY = new Color(200, 20, 20);
	public static final Color COLOR_JOB_STORE = new Color(180, 100, 255);
	public static final Color COLOR_JOB_TAKE = new Color(180, 100, 255);

}
