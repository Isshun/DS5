package org.smallbox.faraway.core.game.modelInfo;

import java.util.ArrayList;
import java.util.List;

public class CategoryInfo extends ObjectInfo {
    public List<ItemInfo>     items;
    public String            label;
    public int                 shortcutPos;
    public String             shortcut;
    public String             labelWithoutShortcut;

    public CategoryInfo(String name, String label) {
        this.items = new ArrayList<>();
        this.name = name;
        this.label = label;
    }
}
