package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.QuestManager;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.AndroidModel;
import org.smallbox.faraway.game.model.character.DroidModel;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.*;
import org.smallbox.faraway.util.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 17/06/2015.
 */
public class PanelDebug extends BasePanel {
    private CharacterModel _character;

    private static class CommandEntry {
        public final OnClickListener    listener;
        public final String             label;

        public CommandEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private CommandEntry[] COMMANDS = new CommandEntry[] {
            new CommandEntry("Add crew (human)",    view -> Game.getCharacterManager().addRandom(HumanModel.class)),
            new CommandEntry("Add crew (android)",  view -> Game.getCharacterManager().addRandom(AndroidModel.class)),
            new CommandEntry("Add crew (droid)",    view -> Game.getCharacterManager().addRandom(DroidModel.class)),
            new CommandEntry("Kill selected",       view -> _character.setIsDead()),
            new CommandEntry("Kill all",            view -> Game.getCharacterManager().getCharacters().forEach(CharacterModel::setIsDead)),
            new CommandEntry("remove characters",   view -> Game.getCharacterManager().getCharacters().clear()),
            new CommandEntry("Launch quest",        view -> ((QuestManager)Game.getInstance().getManager(QuestManager.class)).launchRandomQuest()),
            new CommandEntry("Refresh rooms",       view -> ((RoomManager)Game.getInstance().getManager(RoomManager.class)).refreshRooms()),
            new CommandEntry("Remove rubbles",      view -> {
                for (ConsumableModel consumable : Game.getWorldManager().getConsumables().stream().filter(res -> "base.rubble".equals(res.getInfo().name)).collect(Collectors.toList())) {
                    Game.getWorldManager().removeConsumable(consumable);
                }
            }
            ),
            new CommandEntry("Add item...",         view ->
                    openSubFrame(
                            GameData.getData().items.stream()
                                    .filter(item -> item.isUserItem)
                                    .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
                                    .collect(Collectors.toList()))
            ),
            new CommandEntry("Add consumable...",   view ->
                    openSubFrame(
                            GameData.getData().items.stream()
                                    .filter(item -> item.isConsumable)
                                    .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
                                    .collect(Collectors.toList()))
            ),
            new CommandEntry("Add resource...",     view ->
                    openSubFrame(
                            GameData.getData().items.stream()
                                    .filter(item -> item.isResource)
                                    .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
                                    .collect(Collectors.toList()))
            ),
            new CommandEntry("Set need...",         view ->
                    openSubFrame(
                            Arrays.asList(new CommandEntry[]{
                                    new CommandEntry("Energy (f)", v -> _character.getNeeds().energy = 100),
                                    new CommandEntry("Energy (w)", v -> _character.getNeeds().energy = _character.getType().needs.energy.warning),
                                    new CommandEntry("Energy (c)", v -> _character.getNeeds().energy = _character.getType().needs.energy.critical),
                                    new CommandEntry("Food (f)", v -> _character.getNeeds().food = 100),
                                    new CommandEntry("Food (w)", v -> _character.getNeeds().food = _character.getType().needs.food.warning),
                                    new CommandEntry("Food (c)", v -> _character.getNeeds().food = _character.getType().needs.food.critical),
                                    new CommandEntry("Relation (f)", v -> _character.getNeeds().relation = 100),
                                    new CommandEntry("Relation (w)", v -> _character.getNeeds().relation = _character.getType().needs.relation.warning),
                                    new CommandEntry("Relation (c)", v -> _character.getNeeds().relation = _character.getType().needs.relation.critical),
                                    new CommandEntry("Joy (f)", v -> _character.getNeeds().joy = 100),
                                    new CommandEntry("Joy (w)", v -> _character.getNeeds().joy = _character.getType().needs.joy.warning),
                                    new CommandEntry("Joy (c)", v -> _character.getNeeds().joy = _character.getType().needs.joy.critical),
                            }))
            ),
            new CommandEntry("Dump managers",       view -> {
                Log.notice("----------- dump -----------");
                Game.getInstance().getManagers().forEach(manager -> manager.dump());
            }),
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

    public PanelDebug() {
        super(null, null, 0, 0, 0, 0, "data/ui/panels/dev.yml");
        setAlwaysVisible(true);
        setBackgroundColor(new Color(0x885522));
    }


    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        FrameLayout frameCommands = (FrameLayout)findById("frame_dev_commands");

        int index = 0;
        for (CommandEntry entry: COMMANDS) {
            UILabel lbCommand = ViewFactory.getInstance().createTextView(100, 26);
            lbCommand.setCharacterSize(14);
            lbCommand.setAlign(Align.CENTER_VERTICAL);
            lbCommand.setPosition(10, index++ * 26);
            lbCommand.setOnClickListener(entry.listener);
            lbCommand.setString(entry.label);
            lbCommand.setSize(200, 26);
            frameCommands.addView(lbCommand);
        }

        findById("bt_back").setOnClickListener(view -> {
            findById("frame_dev_commands").setVisible(true);
            findById("frame_dev_sub").setVisible(false);
        });
    }

    @Override
    public void onSelectCharacter(CharacterModel character) {
        _character = character;
    }

    @Override
    protected void onRefresh(int update) {
        ((UILabel)findById("lb_mouse")).setString(
                UserInterface.getInstance().getMouseX() + "x" + UserInterface.getInstance().getMouseY());
    }

}
