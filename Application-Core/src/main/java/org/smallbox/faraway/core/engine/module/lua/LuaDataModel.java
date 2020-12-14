package org.smallbox.faraway.core.engine.module.lua;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.modelInfo.CategoryInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;

import java.util.List;

/**
 * Created by Alex on 26/09/2015.
 */
public abstract class LuaDataModel implements LuaExtendInterface {
    public final List<NetworkInfo>     networks;
    public final List<ItemInfo>        items;
    public final List<CategoryInfo>    categories;
    public final List<PlanetInfo>      planets;

    public LuaDataModel() {
        this.networks = Application.data.networks;
        this.items = Application.data.items;
        this.categories = Application.data.categories;
        this.planets = Application.data.planets;
    }
}
