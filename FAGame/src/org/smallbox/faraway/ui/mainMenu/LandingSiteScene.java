package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.model.LandingSiteModel;
import org.smallbox.faraway.ui.LayoutModel;

/**
 * Created by Alex on 02/06/2015.
 */
public class LandingSiteScene extends MainMenuScene {
    public LandingSiteScene(MainMenu mainMenu, GFXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/land_site.yml");

        _mainMenu.select(new LandingSiteModel());
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        findById("bt_land").setOnClickListener(view -> _mainMenu.select(MainMenu.Scene.TEAM));
    }

    @Override
    protected void onOpen() {
        ((TextView)findById("lb_planet")).setString(_mainMenu.getPlanet().name);
    }
}
