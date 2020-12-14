package org.smallbox.faraway.modules.area;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

/**
 * Created by Alex on 28/06/2015.
 */
public class HomeArea extends AreaModel {

    @Override
    public boolean isAccepted(ItemInfo itemInfo) {
        return false;
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {

    }

    @Override
    public String getName() {
        return "Home Area #n";
    }

    @Override
    public boolean isHome() {
        return true;
    }

}
