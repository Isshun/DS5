package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UILabel;

import java.io.File;

/**
 * Created by Alex on 17/06/2015.
 */
public class LoadPage extends MainMenuPage {
    public LoadPage(MainMenu mainMenu, GDXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/load.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, UIFrame panel) {
        UIFrame frameSaves = (UIFrame)findById("frame_saves");

        int index = 0;
        for (File saveFile: new File("data/saves/").listFiles()) {
            if (saveFile.getName().endsWith(".sav")) {
                UILabel lbSave = ViewFactory.getInstance().createTextView(200, 30);
                lbSave.setTextSize(16);
                lbSave.setText(saveFile.getName());
                lbSave.setPosition(0, 30 * index++);
                lbSave.setSize(200, 30);
                lbSave.setOnClickListener(view -> Application.getInstance().loadGame(saveFile.getName()));
                frameSaves.addView(lbSave);
            }
        }
    }
}
