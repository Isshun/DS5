package org.smallbox.faraway.core.game.model;

import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.data.ItemInfo;

import java.util.List;

/**
 * Created by Alex on 04/11/2015.
 */
public class NetworkInfo extends ObjectInfo {
    public String               label;
    public GraphicInfo          graphics;
    public int                  health = 1;
    public int                  quantity = 10;
    public List<String>         itemNames;
    public List<ItemInfo>       items;
}
