package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.ui.LayoutModel;

/**
 * Created by Alex on 02/06/2015.
 */
public class HomeScene extends MainMenuScene {
    public HomeScene(MainMenu mainMenu, GFXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/home.yml");
        setVisible(true);
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        findById("bt_new_colony").setOnClickListener(view -> _mainMenu.select(MainMenu.Scene.PLANETS));
        findById("bt_load_colony").setOnClickListener(view -> _mainMenu.select(MainMenu.Scene.LOAD));
        findById("bt_exit").setOnClickListener(view -> _renderer.close());
    }
}
