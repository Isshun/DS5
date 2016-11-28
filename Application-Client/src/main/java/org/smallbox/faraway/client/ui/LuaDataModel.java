package org.smallbox.faraway.client.ui;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.modelInfo.CategoryInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;

import java.util.List;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaDataModel {
    public interface OnExtendListener {
        void onExtend(LuaValue values);
    }

    private final OnExtendListener      _extendListener;

    public final List<NetworkInfo>     networks;
    public final List<ItemInfo>        items;
    public final List<CategoryInfo>    categories;
    public final List<PlanetInfo>      planets;

    public LuaDataModel(OnExtendListener extendListener) {
        _extendListener = extendListener;
        this.networks = Application.data.networks;
        this.items = Application.data.items;
        this.categories = Application.data.categories;
        this.planets = Application.data.planets;
    }

    @SuppressWarnings("unused")
    public void extend(LuaValue values) {
        _extendListener.onExtend(values);
    }

}
