package org.smallbox.faraway.core.lua;

import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.CategoryInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.game.planet.PlanetInfo;

import java.util.List;

public abstract class LuaDataModel implements LuaExtendInterface {
    public final List<NetworkInfo>     networks;
    public final List<ItemInfo>        items;
    public final List<CategoryInfo>    categories;
    public final List<PlanetInfo>      planets;

    public LuaDataModel(DataManager dataManager) {
        this.networks = dataManager.networks;
        this.items = dataManager.items;
        this.categories = dataManager.categories;
        this.planets = dataManager.planets;
    }
}
