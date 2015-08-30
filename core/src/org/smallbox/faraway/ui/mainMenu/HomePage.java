package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.core.renderer.GDXRenderer;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 02/06/2015.
 */
public class HomePage extends MainMenuPage {
    private static class DebugEntry {
        private final String            label;
        private final OnClickListener   listener;
        public DebugEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private DebugEntry[]    DEBUG_ENTRIES = {
        new DebugEntry("Arrakis mountain", view -> Application.getInstance().newGame("8.sav", GameData.getData().getRegion("arrakis", "mountain"))),
        new DebugEntry("Arrakis valley", view -> Application.getInstance().newGame("8.sav", GameData.getData().getRegion("arrakis", "valley"))),
        new DebugEntry("Arrakis desert", view -> Application.getInstance().newGame("8.sav", GameData.getData().getRegion("arrakis", "desert"))),
    };

    public HomePage(MainMenu mainMenu, GDXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/home.yml");
        setVisible(true);
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
        findById("bt_new_colony").setOnClickListener(view -> _mainMenu.select(MainMenu.Scene.PLANETS));
        findById("bt_load_colony").setOnClickListener(view -> _mainMenu.select(MainMenu.Scene.LOAD));
        findById("bt_exit").setOnClickListener(view -> _renderer.close());

        int index = 0;
        for (DebugEntry entry: DEBUG_ENTRIES) {
            UILabel lbEntry = ViewFactory.getInstance().createTextView(200, 36);
            lbEntry.setString(entry.label);
            lbEntry.setCharacterSize(18);
            lbEntry.setPosition(500, 100 + index * 36);
            lbEntry.setOnClickListener(entry.listener);
            addView(lbEntry);
            index++;
        }
    }
}
