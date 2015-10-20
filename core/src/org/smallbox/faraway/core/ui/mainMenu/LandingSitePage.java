package org.smallbox.faraway.core.ui.mainMenu;

import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.planet.LandingSiteModel;
import org.smallbox.faraway.core.ui.LayoutModel;
import org.smallbox.faraway.core.ui.engine.views.UIFrame;
import org.smallbox.faraway.core.ui.engine.views.UILabel;

/**
 * Created by Alex on 02/06/2015.
 */
public class LandingSitePage extends MainMenuPage {
    public LandingSitePage(MainMenu mainMenu, GDXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/land_site.yml");

        _mainMenu.select(new LandingSiteModel());
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, UIFrame panel) {
        findById("bt_land").setOnClickListener(view -> _mainMenu.select(MainMenu.Scene.TEAM));
    }

    @Override
    protected void onOpen() {
        ((UILabel)findById("lb_planet")).setText(_mainMenu.getPlanet().name);
    }
}
