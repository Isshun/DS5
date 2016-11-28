package org.smallbox.faraway.core.module.area.model;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

/**
 * Created by Alex on 28/06/2015.
 */
public class HomeAreaModel extends AreaModel {
    public HomeAreaModel() {
        super(AreaType.HOME);
    }

    @Override
    public boolean accept(ItemInfo itemInfo) {
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
