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

    public LuaDataModel(OnExtendListener extendListener) {
        _extendListener = extendListener;
        this.items = GameData.getData().items;
        this.categories = GameData.getData().categories;
        this.planets = GameData.getData().planets;
    }

    @SuppressWarnings("unused")
    public void extend(LuaValue values) {
        _extendListener.onExtend(values);
    }

}
