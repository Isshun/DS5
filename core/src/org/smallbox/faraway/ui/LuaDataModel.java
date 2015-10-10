package org.smallbox.faraway.ui;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.game.model.CategoryInfo;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.planet.PlanetInfo;

import java.util.List;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaDataModel {
    public interface OnExtendListener {
        void onExtend(LuaValue values);
    }

    private final OnExtendListener      _extendListener;

    public final List<ItemInfo>        items;
    public final List<CategoryInfo>    categories;
    public final List<PlanetInfo>      planets;

    public LuaDataModel(OnExtendListener extendListener, GameData data) {
        _extendListener = extendListener;
        this.items = data.items;
        this.categories = data.categories;
        this.planets = data.planets;
    }

    @SuppressWarnings("unused")
    public void extend(LuaValue values) {
        _extendListener.onExtend(values);
    }

}
