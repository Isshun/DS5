package org.smallbox.faraway.game.model;

import org.smallbox.faraway.ui.AreaModel;
import org.smallbox.faraway.ui.AreaType;

/**
 * Created by Alex on 28/06/2015.
 */
public class HomeAreaModel extends AreaModel {
    public HomeAreaModel(AreaType type) {
        super(type);
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
