package org.smallbox.faraway;

import org.smallbox.faraway.model.item.StructureItem;
import org.smallbox.faraway.model.item.UserItem;

/**
 * Created by Alex on 06/06/2015.
 */
public interface GameListener {
    void onStructureBuild(StructureItem structure);
    void onItemBuild(UserItem item);
}
