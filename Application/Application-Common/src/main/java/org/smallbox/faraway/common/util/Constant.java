package org.smallbox.faraway.common.util;

public class Constant {
    public static final boolean     DEBUG = true;
    public static final int         PLANET_WIDTH = 100;
    public static final int         PLANET_HEIGHT = 90;
    public static final long        RELOAD_DATA_INTERVAL = 2000;
    public static final int         MAX_WORLD_WIDTH = 256;
    public static final int         MAX_WORLD_HEIGHT = 256;
    public static final int         MAX_WORLD_FLOORS = 64;
    public static String            NAME    = "FarPoint";
    public static String            VERSION    = "0.1";

    public static final int         MODULE_JOB_PRIORITY = 100;
    public static final int         MODULE_CHARACTER_PRIORITY = 101;
    public static final int         MODULE_WORLD_PRIORITY = 102;

    public static final int         BASE_WIDTH            = 1280;
    public static final int         BASE_HEIGHT            = 800;

    public static final int         WINDOW_WIDTH            = 1920;
    public static final int         WINDOW_HEIGHT            = 1200;

    public static final int         TILE_WIDTH                = 32;
    public static final int         TILE_HEIGHT                = 32;
    public static final int         CHAR_HEIGHT                = 32;
    public static final int         CHAR_WIDTH                = 32;
    public static final double      CHARACTER_INIT_FOOD     = 1;
    public static final double      CHARACTER_INIT_DRINK     = 1;
    public static final double      CHARACTER_INIT_OXYGEN     = 1;
    public static final double      CHARACTER_INIT_HAPPINESS = 1;
    public static final double      CHARACTER_INIT_HEALTH     = 1;
    public static final double      CHARACTER_INIT_ENERGY     = 1;
    public static final double      CHARACTER_INIT_ENTERTAINMENT = 1;
    public static final double      CHARACTER_INIT_RELATION = 1;
    public static final int         PANEL_WIDTH             = 420;
    public static final int         PANEL_HEIGHT             = WINDOW_HEIGHT;
    public static final int         DURATION_MULTIPLIER     = 10;
    public static final int         CHANCE_TO_GET_MEETING_AREA_WHEN_JOBLESS = 0;
    public static final int         CHARACTER_INVENTORY_SPACE = 20;
    public static final double      CHARACTER_GROW_PER_UPDATE = 0.001;
    public static final int         CHARACTER_DELAY_BEFORE_FIRST_CHILD = 2;
    public static final int         CHARACTER_DELAY_BETWEEN_CHILDS = 3;
    public static final int         CHARACTER_MAX_CHILD = 4;
    public static final int         CHARACTER_CHILD_MIN_OLD = 2;
    public static final int         CHARACTER_CHILD_MAX_OLD = 42;
    public static final int         SLOW_UPDATE_INTERVAL     = 20;
    public static final int         CHARACTER_DATE_MIN_OLD     = 14;
    public static final double      CHARACTER_MAX_OLD         = 80;
    public static final int         CHARACTER_STAY_IN_METTING_ROOM = 10;
    public static final double      CHARACTER_LEAVE_HOME_OLD = 18;
    public static final int         RESSOURCE_LOW_FOOD         = 10;
    public static final int         NB_COLUMNS = 47;
    public static final int         ITEM_MAX_WIDTH = 5;
    public static final int         ITEM_MAX_HEIGHT = 4;
    public static final int         COUNT_BEFORE_REUSE_BLOCKED_ITEM = 10;
    public static final int         DELAY_TO_RESTART_BLOCKED_JOB = COUNT_BEFORE_REUSE_BLOCKED_ITEM;
    public static final int         NB_COLUMNS_TITLE = 29;
    public static final int[][]     RESOLUTIONS_4_3 = new int[][] {
            {800, 600},
            {1024, 768},
            {1152, 864},
            {1280, 960},
            {1400, 1050},
            {1600, 1200},
    };
    public static final int[][]     RESOLUTIONS_16_9 = new int[][] {
            {1280, 720},
            {1360, 768},
            {1366, 768},
            {1600, 900},
            {1600, 1200},
            {1920, 1080},
            {2048, 1152},
            {2560, 1440},
    };
    public static final int[][]     RESOLUTIONS_16_10 = new int[][] {
            {1280, 800},
            {1440, 900},
            {1680, 1050},
            {1920, 1200},
            {2560, 1600},
    };
}