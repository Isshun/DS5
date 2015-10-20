package org.smallbox.faraway.core.game.model.area;

/**
 * Created by Alex on 28/06/2015.
 */
public class HomeAreaModel extends AreaModel {
    public HomeAreaModel() {
        super(AreaType.HOME);
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
