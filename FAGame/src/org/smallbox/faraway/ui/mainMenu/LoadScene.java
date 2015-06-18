package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.LayoutModel;

import java.io.File;

/**
 * Created by Alex on 17/06/2015.
 */
public class LoadScene extends MainMenuScene {
    public LoadScene(MainMenu mainMenu, GFXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/load.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        FrameLayout frameSaves = (FrameLayout)findById("frame_saves");

        int index = 0;
        for (File saveFile: new File("data/saves/").listFiles()) {
            if (saveFile.getName().endsWith(".sav")) {
                TextView lbSave = ViewFactory.getInstance().createTextView(200, 30);
                lbSave.setCharacterSize(16);
                lbSave.setString(saveFile.getName());
                lbSave.setPosition(0, 30 * index++);
                lbSave.setSize(200, 30);
                lbSave.setOnClickListener(view -> Application.getInstance().loadGame(saveFile.getName()));
                frameSaves.addView(lbSave);
            }
        }
    }
}
