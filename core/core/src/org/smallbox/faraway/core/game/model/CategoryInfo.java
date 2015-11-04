package org.smallbox.faraway.core.game.model;

import org.smallbox.faraway.core.data.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class CategoryInfo extends ObjectInfo {
    public List<ItemInfo>     items;
    public String            label;
    public int                 shortcutPos;
    public String             shortcut;
    public String             labelWithoutShortcut;

    public CategoryInfo(String name, String label) {
        this.items = new ArrayList<ItemInfo>();
        this.name = name;
        this.label = label;
    }
}
