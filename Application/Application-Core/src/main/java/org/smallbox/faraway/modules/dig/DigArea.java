package org.smallbox.faraway.modules.dig;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.modules.area.AreaModel;

/**
 * Created by Alex on 08/03/2017.
 */
@AreaTypeInfo(label = "Dig")
public class DigArea extends AreaModel {

    @Override
    public boolean accept(ItemInfo itemInfo) {
        return false;
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {

    }
}
