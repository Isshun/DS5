package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.core.renderer.GDXRenderer;
import org.smallbox.faraway.game.model.planet.LandingSiteModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 02/06/2015.
 */
public class LandingSitePage extends MainMenuPage {
    public LandingSitePage(MainMenu mainMenu, GDXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/land_site.yml");

        _mainMenu.select(new LandingSiteModel());
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
        findById("bt_land").setOnClickListener(view -> _mainMenu.select(MainMenu.Scene.TEAM));
    }

    @Override
    protected void onOpen() {
        ((UILabel)findById("lb_planet")).setString(_mainMenu.getPlanet().name);
    }
}
