package org.smallbox.faraway.ui;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.QuestManager;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.AndroidModel;
import org.smallbox.faraway.game.model.character.DroidModel;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.ui.engine.ViewFactory;
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

    private CommandEntry[] COMMANDS = new CommandEntry[] {
            new CommandEntry("Add crew (human)", view -> Game.getCharacterManager().addRandom(HumanModel.class)),
            new CommandEntry("Add crew (android)", view -> Game.getCharacterManager().addRandom(AndroidModel.class)),
            new CommandEntry("Add crew (droid)", view -> Game.getCharacterManager().addRandom(DroidModel.class)),
            new CommandEntry("Launch quest", view -> ((QuestManager)Game.getInstance().getManager(QuestManager.class)).launchRandomQuest()),
            new CommandEntry("Refresh rooms", view -> ((RoomManager)Game.getInstance().getManager(RoomManager.class)).refreshRooms()),
            new CommandEntry("Add item...", view -> {
                findById("frame_dev_commands").setVisible(false);
                findById("frame_dev_items").setVisible(true);
            }),
            new CommandEntry("Add consumable...", view -> {
                findById("frame_dev_commands").setVisible(false);
                findById("frame_dev_consumables").setVisible(true);
            }),
    };

    public PanelDev() {
        super(null, null, 0, 0, 0, 0, "data/ui/panels/dev.yml");
        setAlwaysVisible(true);
        setBackgroundColor(new Color(0x885522));
    }


    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        FrameLayout frameCommands = (FrameLayout)findById("frame_dev_commands");

        int index = 0;
        for (CommandEntry entry: COMMANDS) {
            TextView lbCommand = ViewFactory.getInstance().createTextView(100, 30);
            lbCommand.setCharacterSize(14);
            lbCommand.setAlign(Align.CENTER_VERTICAL);
            lbCommand.setPosition(10, index++ * 30);
            lbCommand.setOnClickListener(entry.listener);
            lbCommand.setString(entry.label);
            lbCommand.setSize(200, 30);
            frameCommands.addView(lbCommand);
        }

        // Create frame consumable
        index = 0;
        FrameLayout frameConsumable = (FrameLayout) findById("frame_dev_consumables");
        frameConsumable.setVisible(false);
        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isConsumable) {
                TextView lbConsumable = ViewFactory.getInstance().createTextView(100, 30);
                lbConsumable.setCharacterSize(14);
                lbConsumable.setAlign(Align.CENTER_VERTICAL);
                lbConsumable.setPosition(10, index++ * 30);
                lbConsumable.setOnClickListener(view -> {
                    UserInterface.getInstance().putDebug(itemInfo);
                    frameCommands.setVisible(true);
                    frameConsumable.setVisible(false);
                });
                lbConsumable.setString(itemInfo.label);
                lbConsumable.setSize(200, 30);
                frameConsumable.addView(lbConsumable);
            }
        }

        // Create frame items
        index = 0;
        FrameLayout frameItems = (FrameLayout) findById("frame_dev_items");
        frameItems.setVisible(false);
        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isUserItem) {
                TextView lbItem = ViewFactory.getInstance().createTextView(100, 30);
                lbItem.setCharacterSize(14);
                lbItem.setAlign(Align.CENTER_VERTICAL);
                lbItem.setPosition(10, index++ * 30);
                lbItem.setOnClickListener(view -> {
                    UserInterface.getInstance().putDebug(itemInfo);
                    frameCommands.setVisible(true);
                    frameItems.setVisible(false);
                });
                lbItem.setString(itemInfo.label);
                lbItem.setSize(200, 30);
                frameItems.addView(lbItem);
            }
        }
    }

}
