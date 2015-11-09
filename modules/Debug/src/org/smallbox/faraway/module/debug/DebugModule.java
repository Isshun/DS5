package org.smallbox.faraway.module.debug;

import org.smallbox.faraway.core.data.factory.world.WorldFactory;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.character.model.AndroidModel;
import org.smallbox.faraway.core.game.module.character.model.DroidModel;
import org.smallbox.faraway.core.game.module.character.model.HumanModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.core.game.module.room.RoomModule;
import org.smallbox.faraway.core.game.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.quest.QuestModule;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by Alex on 30/08/2015.
 */
public class DebugModule extends GameModule {
    private CharacterModel _character;

    private UIList          _listCommands;
    private ItemModel       _item;
    private UILabel         _lbMemory;
    private UIFrame         _view;

    private static class CommandEntry {
        public final OnClickListener    listener;
        public final String             label;

        public CommandEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private Collection<CommandEntry> COMMANDS = Arrays.asList(
//            new CommandEntry("Add item...",         view ->
//                    openFrame(Data.getData().items.stream()
//                            .filter(item -> item.isUserItem)
//                            .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
//                            .collect(Collectors.toList()))
//            ),
//            new CommandEntry("Add consumable...",   view ->
//                    openFrame(Data.getData().items.stream()
//                            .filter(item -> item.isConsumable)
//                            .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
//                            .collect(Collectors.toList()))
//            ),
//            new CommandEntry("Add resource...",     view ->
//                    openFrame(Data.getData().items.stream()
//                            .filter(item -> item.isResource)
//                            .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
//                            .collect(Collectors.toList()))
//            ),
//            new CommandEntry("List receipts...",     view ->
//                    openFrame(Data.getData().receipts.stream()
//                            .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().getSelector().select(info)))
//                            .collect(Collectors.toList()))
//            ),
            new CommandEntry("Re-gen",              view -> {
//                new WorldFactory().create(ModuleHelper.getWorldModule(), Game.getInstance().getInfo().region);
//                MainRenderer.getInstance().getWorldRenderer().refreshAll();
            }),
            new CommandEntry("Add crew (human)",    view -> ModuleHelper.getCharacterModule().addRandom(HumanModel.class)),
            new CommandEntry("Add crew (android)",  view -> ModuleHelper.getCharacterModule().addRandom(AndroidModel.class)),
            new CommandEntry("Add crew (droid)",    view -> ModuleHelper.getCharacterModule().addRandom(DroidModel.class)),
            new CommandEntry("Kill selected",       view -> _character.setIsDead()),
            new CommandEntry("Kill all",            view -> ModuleHelper.getCharacterModule().getCharacters().forEach(CharacterModel::setIsDead)),
            new CommandEntry("add components to item",   view -> {
                _item.getComponents().add(new BuildableMapObject.ComponentModel(Data.getData().getItemInfo("base.calcite_brick"), 10, 3));
                _item.setComplete(false);
            }),
            new CommandEntry("remove characters",   view -> ModuleHelper.getCharacterModule().getCharacters().clear()),
            new CommandEntry("Launch quest",        view -> ((QuestModule) ModuleManager.getInstance().getModule(QuestModule.class)).launchRandomQuest()),
//            new CommandEntry("Refresh rooms",       view -> ((RoomModule)ModuleManager.getInstance().getModule(RoomModule.class)).refreshRooms()),
            new CommandEntry("Remove rubbles",      view -> {
                for (ConsumableModel consumable : ModuleHelper.getWorldModule().getConsumables().stream().filter(res -> "base.calcite_rubble".equals(res.getInfo().name)).collect(Collectors.toList())) {
                    ModuleHelper.getWorldModule().removeConsumable(consumable);
                }
            }
            ),
            new CommandEntry("Set need...",         view ->
                    openFrame(Arrays.asList(
                            new CommandEntry("Energy (f)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_ENERGY, 100)),
                            new CommandEntry("Energy (w)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_ENERGY, _character.getType().needs.energy.warning)),
                            new CommandEntry("Energy (c)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_ENERGY, _character.getType().needs.energy.critical)),
                            new CommandEntry("Food (f)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_FOOD, 100)),
                            new CommandEntry("Food (w)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_FOOD, _character.getType().needs.food.warning)),
                            new CommandEntry("Food (c)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_FOOD, _character.getType().needs.food.critical)),
                            new CommandEntry("Relation (f)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_RELATION, 100)),
                            new CommandEntry("Relation (w)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_RELATION, _character.getType().needs.relation.warning)),
                            new CommandEntry("Relation (c)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_RELATION, _character.getType().needs.relation.critical)),
                            new CommandEntry("Entertainment (f)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_ENTERTAINMENT, 100)),
                            new CommandEntry("Entertainment (w)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_ENTERTAINMENT, _character.getType().needs.joy.warning)),
                            new CommandEntry("Entertainment (c)", v -> _character.getNeeds().setValue(CharacterNeedsExtra.TAG_ENTERTAINMENT, _character.getType().needs.joy.critical))))
            ),
            new CommandEntry("Dump managers",       view -> {
                Log.notice("\n----------- dump -----------");
                ModuleManager.getInstance().getModules().forEach(GameModule::dump);
            }),
            new CommandEntry("Dump renders",       view -> {
                Log.notice("\n----------- dump -----------");
                MainRenderer.getInstance().getRenders().forEach(BaseRenderer::dump);
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
            UILabel lbCommand = new UILabel(100, 26);
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
        _view = new UIFrame(400, 800);
        _view.setBackgroundColor(0x121c1e);
        _view.setPosition(1200, 38);
        _view.setVisible(false);
        _view.setId("panel_debug");

        UILabel lbTitle = new UILabel();
        lbTitle.setText("Debug");
        lbTitle.setTextSize(16);
        lbTitle.setSize(200, 22);
        lbTitle.setBackgroundColor(0x55000000);
        lbTitle.setPadding(5);
        _view.addView(lbTitle);

        UILabel btBack = new UILabel();
        btBack.setText("[Back]");
        btBack.setTextSize(16);
        btBack.setSize(100, 32);
        btBack.setPosition(140, 0);
        btBack.setPadding(5);
        btBack.setOnClickListener(v -> {
            openFrame(COMMANDS);
        });
        _view.addView(btBack);

        _listCommands = new UIList(-1, -1);
        _listCommands.setPosition(0, 20);
        openFrame(COMMANDS);
        _view.addView(_listCommands);
//        view.setModule();

        UserInterface.getInstance()._views.add(_view);

        _lbMemory = new UILabel();
        _lbMemory.setTextSize(14);
        _lbMemory.setPosition(0, 0);
        UserInterface.getInstance()._views.add(_lbMemory);
    }

    @Override
    protected void onLoaded(Game game) {

        System.out.println("debug");

//        addWindow(WindowBuilder.create().setTitle("Debug").setContentLayout("panels/dev.yml").build(new UIWindowListener() {
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
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        int total = (int) (runtime.totalMemory() / mb);

//        _used = (_used * 7 + used) / 8;

//        _lbRenderTime.setText("Rendering: %dms", (int) Application.getRenderTime());
        _lbMemory.setText("Heap: " + String.valueOf(used) + " / " + String.valueOf(total) + " Mo");
//        _lbUpdate.setText(String.format("Update: %d/%d", Application.getLastUpdateDelay(), Application.getLastLongUpdateDelay()));
//        _lbFloor.setText("FPS: " + MainRenderer.getFPS());
//        _lbFrame.setText("tick: " + Game.getInstance().getTick() + " / frame: " + frame);

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

    public void onKeyPress(GameEventListener.Key key) {
        if (key == GameEventListener.Key.BACKSPACE) {
            openFrame(COMMANDS);
        }
//        if (key == GameEventListener.Key.TILDE) {
//            _view.setVisible(!_view.isVisible());
//        }
        if (key == GameEventListener.Key.ESCAPE) {
            _view.setVisible(false);
        }
    }

}
