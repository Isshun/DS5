package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.OxygenManager;
import org.smallbox.faraway.game.manager.TemperatureManager;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.UILabel;
import org.smallbox.faraway.ui.engine.ViewFactory;

import java.util.Collection;

/**
 * Created by Alex on 12/07/2015.
 */
public class PanelWhiteRoom extends BasePanel {
    private TemperatureManager          _temperatureManager;
    private OxygenManager               _oxygenManager;

    public PanelWhiteRoom() {
        super(null, null, 0, 0, 0, 0, "data/ui/panels/white_room.yml");
        setAlwaysVisible(true);
        setBackgroundColor(new Color(0xeeeeee));
    }

    private static class CommandEntry {
        public final OnClickListener    listener;
        public final String             label;

        public CommandEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private CommandEntry[] COMMANDS = new CommandEntry[] {
            new CommandEntry("Increase temperature", view -> _temperatureManager.increaseTemperature()),
            new CommandEntry("Decrease temperature", view -> _temperatureManager.decreaseTemperature()),
            new CommandEntry("Normalize temperature", view -> _temperatureManager.normalize()),
            new CommandEntry("Increase oxygen", view -> ((OxygenManager)Game.getInstance().getManager(OxygenManager.class)).setOxygen(Game.getWorldManager().getParcel(0, 0).getOxygen() + 1)),
            new CommandEntry("Decrease oxygen", view -> ((OxygenManager)Game.getInstance().getManager(OxygenManager.class)).setOxygen(Game.getWorldManager().getParcel(0, 0).getOxygen() - 1)),
            new CommandEntry("Normalize oxygen", view -> Game.getWorldManager().getParcelList().forEach(parcel -> parcel.setOxygen(((OxygenManager) Game.getInstance().getManager(OxygenManager.class)).getOxygen()))),
    };

    private void openSubFrame(Collection<CommandEntry> commands) {
        final FrameLayout frameCommands = (FrameLayout) findById("frame_dev_commands");
        frameCommands.setVisible(false);

        final FrameLayout frameConsumable = (FrameLayout) findById("frame_dev_sub");
        frameConsumable.setVisible(true);
        frameConsumable.removeAllViews();

        int index = 0;
        for (CommandEntry command: commands) {
            UILabel lbConsumable = ViewFactory.getInstance().createTextView(100, 26);
            lbConsumable.setCharacterSize(14);
            lbConsumable.setAlign(Align.CENTER_VERTICAL);
            lbConsumable.setPosition(10, index++ * 26);
            lbConsumable.setOnClickListener(view -> {
                command.listener.onClick(view);
            });
            lbConsumable.setString(command.label);
            lbConsumable.setSize(200, 26);
            frameConsumable.addView(lbConsumable);
        }
    }

    @Override
    public void onCreate(ViewFactory factory) {
        _temperatureManager = ((TemperatureManager)Game.getInstance().getManager(TemperatureManager.class));
        _oxygenManager = ((OxygenManager)Game.getInstance().getManager(OxygenManager.class));
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        FrameLayout frameCommands = (FrameLayout)findById("frame_dev_commands");

        int index = 0;
        for (CommandEntry entry: COMMANDS) {
            UILabel lbCommand = ViewFactory.getInstance().createTextView(100, 26);
            lbCommand.setCharacterSize(14);
            lbCommand.setColor(Color.BLACK);
            lbCommand.setAlign(Align.CENTER_VERTICAL);
            lbCommand.setPosition(10, index++ * 26);
            lbCommand.setOnClickListener(entry.listener);
            lbCommand.setString(entry.label);
            lbCommand.setSize(200, 26);
            frameCommands.addView(lbCommand);
        }

//        findById("bt_back").setOnClickListener(view -> {
//            findById("frame_dev_commands").setVisible(true);
//            findById("frame_dev_sub").setVisible(false);
//        });
    }

}
