package org.smallbox.faraway.core.module.lua.luaModel;

import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaEventsModel {
    public static final int on_character_selected = 1;
    public static final int on_parcel_selected = 2;
    public static final int on_area_selected = 3;
    public static final int on_structure_selected = 4;
    public static final int on_item_selected = 5;
    public static final int on_resource_selected = 6;
    public static final int on_consumable_selected = 7;
    public static final int on_deselect = 8;
    public static final int on_job_create = 9;
    public static final int on_custom_event = 10;
    public static final int on_key_press = 11;
    public static final int on_parcel_over = 12;
    public static final int on_receipt_select = 13;
    public static final int on_weather_change = 14;

    public void send(String tag) {
        Game.getInstance().notify(observer -> observer.onCustomEvent(tag, null));
    }

    public void send(String tag, Object object) {
        Game.getInstance().notify(observer -> observer.onCustomEvent(tag, object));
    }
}
