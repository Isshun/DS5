package org.smallbox.faraway.ui;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.game.model.character.AndroidModel;
import org.smallbox.faraway.game.model.character.DroidModel;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.ui.panel.BasePanel;

/**
 * Created by Alex on 17/06/2015.
 */
public class PanelDev extends BasePanel {
    private static class CommandEntry {
        public final OnClickListener    listener;
        public final String             label;

        public CommandEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private static CommandEntry[] COMMANDS = new CommandEntry[] {
            new CommandEntry("Add crew (human)", view -> Game.getCharacterManager().addRandom(HumanModel.class)),
            new CommandEntry("Add crew (android)", view -> Game.getCharacterManager().addRandom(AndroidModel.class)),
            new CommandEntry("Add crew (droid)", view -> Game.getCharacterManager().addRandom(DroidModel.class))
    };

    public PanelDev() {
        super(null, null, 0, 300, 200, 600, "data/ui/panels/dev.yml");
        setAlwaysVisible(true);
        setBackgroundColor(new Color(0x885522));
    }


    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        FrameLayout frame = (FrameLayout)findById("frame_dev");

        int index = 0;
        for (CommandEntry entry: COMMANDS) {
            TextView lbCommand = ViewFactory.getInstance().createTextView(100, 30);
            lbCommand.setCharacterSize(14);
            lbCommand.setAlign(Align.CENTER_VERTICAL);
            lbCommand.setPosition(10, index++ * 30);
            lbCommand.setOnClickListener(entry.listener);
            lbCommand.setString(entry.label);
            lbCommand.setSize(200, 30);
            frame.addView(lbCommand);
        }
    }

}
