package org.smallbox.faraway.ui;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.model.CategoryInfo;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.NetworkInfo;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;

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
        this.networks = Data.getData().networks;
        this.items = Data.getData().items;
        this.categories = Data.getData().categories;
        this.planets = Data.getData().planets;
    }

    @SuppressWarnings("unused")
    public void extend(LuaValue values) {
        _extendListener.onExtend(values);
    }

}
