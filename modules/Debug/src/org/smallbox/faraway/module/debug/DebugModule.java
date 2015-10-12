package org.smallbox.faraway.module.debug;

import org.smallbox.faraway.data.factory.world.WorldFactory;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.AndroidModel;
import org.smallbox.faraway.game.model.character.DroidModel;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.BuildableMapObject;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.module.*;
import org.smallbox.faraway.game.module.base.RoomModule;
import org.smallbox.faraway.module.quest.QuestModule;
import org.smallbox.faraway.ui.engine.views.UIList;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UILabel;
import org.smallbox.faraway.ui.engine.views.View;
import org.smallbox.faraway.util.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by Alex on 30/08/2015.
 */
public class DebugModule extends GameUIModule {
    private CharacterModel _character;

    private UIList          _listCommands;
    private ItemModel       _item;

    private static class CommandEntry {
        public final OnClickListener    listener;
        public final String             label;

        public CommandEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private Collection<CommandEntry> COMMANDS = Arrays.asList(
            new CommandEntry("Add item...",         view ->
                    openFrame(GameData.getData().items.stream()
                            .filter(item -> item.isUserItem)
                            .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
                            .collect(Collectors.toList()))
            ),
            new CommandEntry("Add consumable...",   view ->
                    openFrame(GameData.getData().items.stream()
                            .filter(item -> item.isConsumable)
                            .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
                            .collect(Collectors.toList()))
            ),
            new CommandEntry("Add resource...",     view ->
                    openFrame(GameData.getData().items.stream()
                            .filter(item -> item.isResource)
                            .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
                            .collect(Collectors.toList()))
            ),
            new CommandEntry("List receipts...",     view ->
                    openFrame(GameData.getData().receipts.stream()
                            .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().getSelector().select(info)))
                            .collect(Collectors.toList()))
            ),
            new CommandEntry("Re-gen",              view -> {
                new WorldFactory().create(ModuleHelper.getWorldModule(), Game.getInstance().getRegion().getInfo());
                MainRenderer.getInstance().getWorldRenderer().refreshAll();
            }),
            new CommandEntry("Add crew (human)",    view -> ModuleHelper.getCharacterModule().addRandom(HumanModel.class)),
            new CommandEntry("Add crew (android)",  view -> ModuleHelper.getCharacterModule().addRandom(AndroidModel.class)),
            new CommandEntry("Add crew (droid)",    view -> ModuleHelper.getCharacterModule().addRandom(DroidModel.class)),
            new CommandEntry("Kill selected",       view -> _character.setIsDead()),
            new CommandEntry("Kill all",            view -> ModuleHelper.getCharacterModule().getCharacters().forEach(CharacterModel::setIsDead)),
            new CommandEntry("add components to item",   view -> {
                _item.getComponents().add(new BuildableMapObject.ComponentModel(GameData.getData().getItemInfo("base.calcite_brick"), 10, 3));
                _item.setComplete(false);
            }),
            new CommandEntry("remove characters",   view -> ModuleHelper.getCharacterModule().getCharacters().clear()),
            new CommandEntry("Launch quest",        view -> ((QuestModule)ModuleManager.getInstance().getModule(QuestModule.class)).launchRandomQuest()),
            new CommandEntry("Refresh rooms",       view -> ((RoomModule)ModuleManager.getInstance().getModule(RoomModule.class)).refreshRooms()),
            new CommandEntry("Remove rubbles",      view -> {
                for (ConsumableModel consumable : ModuleHelper.getWorldModule().getConsumables().stream().filter(res -> "base.calcite_rubble".equals(res.getInfo().name)).collect(Collectors.toList())) {
                    ModuleHelper.getWorldModule().removeConsumable(consumable);
                }
            }
            ),
            new CommandEntry("Set need...",         view ->
                    openFrame(Arrays.asList(
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
                            new CommandEntry("Joy (c)", v -> _character.getNeeds().joy = _character.getType().needs.joy.critical)))
            ),
            new CommandEntry("Dump managers",       view -> {
                Log.notice("\n----------- dump -----------");
                ModuleManager.getInstance().getModules().forEach(manager -> manager.dump());
            }),
            new CommandEntry("Dump renders",       view -> {
                Log.notice("\n----------- dump -----------");
                MainRenderer.getInstance().getRenders().forEach(renderer -> renderer.dump());
            })
//            new CommandEntry("Dump panels",       view -> {
//                Log.notice("\n----------- dump -----------");
//                Arrays.asList(UserInterface.getInstance().getPanels()).forEach(panel -> panel.dump());
//            }),
//            new CommandEntry("Temperature debug",       view -> ModuleManager.getInstance().toggleModule(TemperatureDebugModule.class)),
////            new CommandEntry("Oxygen debug",            view -> UserInterface.getInstance().getPanel(OxygenManagerPanel.class).setVisible(true)),
//            new CommandEntry("Job detail",              view -> UserInterface.getInstance().getPanel(JobDebugPanel.class).setVisible(true)),
//            new CommandEntry("Parcel detail",           view -> UserInterface.getInstance().getPanel(ParcelDebugPanel.class).setVisible(true)),
    );

    private void openFrame(Collection<CommandEntry> commands) {
        _listCommands.removeAllViews();
        for (CommandEntry entry : commands) {
            UILabel lbCommand = ViewFactory.getInstance().createTextView(100, 26);
            lbCommand.setTextSize(14);
            lbCommand.setTextAlign(View.Align.CENTER_VERTICAL);
            lbCommand.setOnClickListener(entry.listener);
            lbCommand.setText(entry.label);
            lbCommand.setSize(200, 22);
            lbCommand.setPadding(5);
            _listCommands.addView(lbCommand);
        }
    }

    @Override
    public void onReloadUI() {
        UIFrame view = new UIFrame(200, 600);
        view.setBackgroundColor(0x121c1e);
        view.setPosition(0, 0);

        UILabel lbTitle = new UILabel();
        lbTitle.setText("Debug");
        lbTitle.setTextSize(16);
        lbTitle.setSize(200, 22);
        lbTitle.setBackgroundColor(0x55000000);
        lbTitle.setPadding(5);
        view.addView(lbTitle);

        UILabel btBack = new UILabel();
        btBack.setText("[Back]");
        btBack.setTextSize(16);
        btBack.setSize(100, 32);
        btBack.setPosition(140, 0);
        btBack.setPadding(5);
        btBack.setOnClickListener(v -> {
            openFrame(COMMANDS);
        });
        view.addView(btBack);

        _listCommands = new UIList(-1, -1);
        _listCommands.setPosition(0, 20);
        openFrame(COMMANDS);
        view.addView(_listCommands);
//        view.setModule();

        UserInterface.getInstance()._views.add(view);
    }

    @Override
    protected void onLoaded() {

        System.out.println("debug");

//        addWindow(WindowBuilder.create().setTitle("Debug").setContentLayout("panels/dev.yml").build(new WindowListener() {
//            @Override
//            public void onCreate(UIWindow window, UIFrame view) {
//                mView = view;
//                UIFrame frameCommands = (UIFrame) view.findById("frame_dev_commands");
//
//            }
//
//            @Override
//            public void onRefresh(int update) {
//
//            }
//
//            @Override
//            public void onClose() {
//
//            }
//        }));
    }

    @Override
    protected void onUpdate(int tick) {
//        if (mView != null) {
//            ((UILabel) mView.findById("lb_mouse")).setText(
//                    UserInterface.getInstance().getMouseX() + "x" + UserInterface.getInstance().getMouseY());
//        }
    }

    @Override
    public void onSelectCharacter(CharacterModel character) {
        _character = character;
    }

    @Override
    public void onSelectItem(ItemModel item) {
        _item = item;
    }

//    @Override
//    protected void onWindowRefresh(int update) {
//    }

    public void onKeyPress(GameEventListener.Key key) {
        if (key == GameEventListener.Key.F10) {
            setActivate(!isActivate());
        }
        if (key == GameEventListener.Key.BACKSPACE) {
            openFrame(COMMANDS);
        }
    }

}
