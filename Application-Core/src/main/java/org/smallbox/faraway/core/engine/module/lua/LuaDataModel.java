package org.smallbox.faraway.core.engine.module.lua;

import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.modelInfo.CategoryInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;

import java.util.List;

public abstract class LuaDataModel implements LuaExtendInterface {
    public final List<NetworkInfo>     networks;
    public final List<ItemInfo>        items;
    public final List<CategoryInfo>    categories;
    public final List<PlanetInfo>      planets;

    public LuaDataModel(Data data) {
        this.networks = data.networks;
        this.items = data.items;
        this.categories = data.categories;
        this.planets = data.planets;
    }
}
