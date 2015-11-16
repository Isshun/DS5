package org.smallbox.faraway.core.engine.module.lua.luaModel;

import org.smallbox.faraway.core.Application;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaEventsModel {
    public static final int on_character_selected = 1;
    public static final int on_parcel_selected = 2;
    public static final int on_area_selected = 3;
    public static final int on_structure_selected = 4;
    public static final int on_item_selected = 5;
    public static final int on_plant_selected = 6;
    public static final int on_consumable_selected = 7;
    public static final int on_deselect = 8;
    public static final int on_job_create = 9;
    public static final int on_custom_event = 10;
    public static final int on_key_press = 11;
    public static final int on_parcel_over = 12;
    public static final int on_receipt_select = 13;
    public static final int on_weather_change = 14;
    public static final int on_temperature_change = 15;
    public static final int on_light_change = 16;
    public static final int on_day_time_change = 17;
    public static final int on_hour_change = 18;
    public static final int on_day_change = 19;
    public static final int on_speed_change = 20;
    public static final int on_binding = 21;
    public static final int on_network_selected = 22;
    public static final int on_game_paused = 23;
    public static final int on_game_resume = 24;
    public static final int on_floor_change = 25;
    public static final int on_rock_selected = 26;

    public void send(String tag) {
        Application.getInstance().notify(observer -> observer.onCustomEvent(tag, null));
    }

    public void send(String tag, Object object) {
        Application.getInstance().notify(observer -> observer.onCustomEvent(tag, object));
    }
}
