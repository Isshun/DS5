package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.ui.LayoutModel;

/**
 * Created by Alex on 02/06/2015.
 */
public class TeamScene extends MainMenuScene {
    public TeamScene(MainMenu mainMenu, GFXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/team.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        findById("bt_land_site").setOnClickListener(view -> Application.getInstance().newGame("5.sav"));
    }

    @Override
    protected void onOpen() {
        ((TextView)findById("lb_planet")).setString(_mainMenu.getPlanet().name);
        ((TextView)findById("lb_landing_site")).setString(_mainMenu.getLandingSite().name);
    }
}
