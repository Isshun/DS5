package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 02/06/2015.
 */
public class TeamPage extends MainMenuPage {
    public TeamPage(MainMenu mainMenu, GDXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/team.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
        findById("bt_land_site").setOnClickListener(view -> Application.getInstance().newGame("5.sav", null));
    }

    @Override
    protected void onOpen() {
        ((UILabel)findById("lb_planet")).setText(_mainMenu.getPlanet().name);
        ((UILabel)findById("lb_landing_site")).setText(_mainMenu.getLandingSite().name);
    }
}
