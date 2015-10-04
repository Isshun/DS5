//package org.smallbox.faraway.modules.dev;
//
//import org.smallbox.faraway.data.factory.world.WorldFactory;
//import org.smallbox.faraway.engine.renderer.MainRenderer;
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.game.model.GameData;
//import org.smallbox.faraway.game.model.character.AndroidModel;
//import org.smallbox.faraway.game.model.character.DroidModel;
//import org.smallbox.faraway.game.model.character.HumanModel;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.ConsumableModel;
//import org.smallbox.faraway.game.module.*;
//import org.smallbox.faraway.game.module.base.RoomModule;
//import org.smallbox.faraway.modules.quest.QuestModule;
//import org.smallbox.faraway.ui.JobDebugPanel;
//import org.smallbox.faraway.ui.UserInterface;
//import org.smallbox.faraway.ui.engine.OnClickListener;
//import org.smallbox.faraway.ui.engine.ViewFactory;
//import org.smallbox.faraway.ui.engine.view.UIFrame;
//import org.smallbox.faraway.ui.engine.view.UILabel;
//import org.smallbox.faraway.ui.engine.view.View;
//import org.smallbox.faraway.ui.panel.debug.ParcelDebugPanel;
//import org.smallbox.faraway.util.Log;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.stream.Collectors;
//
///**
// * Created by Alex on 30/08/2015.
// */
//public class DebugModule extends GameUIModule {
//    private CharacterModel _character;
//
//    private UIFrame         mView;
//
//    private static class CommandEntry {
//        public final OnClickListener    listener;
//        public final String             label;
//
//        public CommandEntry(String label, OnClickListener listener) {
//            this.label = label;
//            this.listener = listener;
//        }
//    }
//
//    private CommandEntry[] COMMANDS = new CommandEntry[] {
//            new CommandEntry("Add item...",         view ->
//                    openSubFrame(
//                            GameData.getData().items.stream()
//                                    .filter(item -> item.isUserItem)
//                                    .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
//                                    .collect(Collectors.toList()))
//            ),
//            new CommandEntry("Add consumable...",   view ->
//                    openSubFrame(
//                            GameData.getData().items.stream()
//                                    .filter(item -> item.isConsumable)
//                                    .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
//                                    .collect(Collectors.toList()))
//            ),
//            new CommandEntry("Add resource...",     view ->
//                    openSubFrame(
//                            GameData.getData().items.stream()
//                                    .filter(item -> item.isResource)
//                                    .map(info -> new CommandEntry(info.label, v -> UserInterface.getInstance().putDebug(info)))
//                                    .collect(Collectors.toList()))
//            ),
//            new CommandEntry("Re-gen",              view -> {
//                new WorldFactory().create(ModuleHelper.getWorldModule(), Game.getInstance().getRegion().getInfo());
//                MainRenderer.getInstance().getWorldRenderer().refreshAll();
//            }),
//            new CommandEntry("Add crew (human)",    view -> ModuleHelper.getCharacterModule().addRandom(HumanModel.class)),
//            new CommandEntry("Add crew (android)",  view -> ModuleHelper.getCharacterModule().addRandom(AndroidModel.class)),
//            new CommandEntry("Add crew (droid)",    view -> ModuleHelper.getCharacterModule().addRandom(DroidModel.class)),
//            new CommandEntry("Kill selected",       view -> _character.setIsDead()),
//            new CommandEntry("Kill all",            view -> ModuleHelper.getCharacterModule().getCharacters().forEach(CharacterModel::setIsDead)),
//            new CommandEntry("remove characters",   view -> ModuleHelper.getCharacterModule().getCharacters().clear()),
//            new CommandEntry("Launch quest",        view -> ((QuestModule)ModuleManager.getInstance().getModule(QuestModule.class)).launchRandomQuest()),
//            new CommandEntry("Refresh rooms",       view -> ((RoomModule)ModuleManager.getInstance().getModule(RoomModule.class)).refreshRooms()),
//            new CommandEntry("Remove rubbles",      view -> {
//                for (ConsumableModel consumable : ModuleHelper.getWorldModule().getConsumables().stream().filter(res -> "base.rubble".equals(res.getInfo().name)).collect(Collectors.toList())) {
//                    ModuleHelper.getWorldModule().removeConsumable(consumable);
//                }
//            }
//            ),
//            new CommandEntry("Set need...",         view ->
//                    openSubFrame(
//                            Arrays.asList(new CommandEntry[]{
//                                    new CommandEntry("Energy (f)", v -> _character.getNeeds().energy = 100),
//                                    new CommandEntry("Energy (w)", v -> _character.getNeeds().energy = _character.getType().needs.energy.warning),
//                                    new CommandEntry("Energy (c)", v -> _character.getNeeds().energy = _character.getType().needs.energy.critical),
//                                    new CommandEntry("Food (f)", v -> _character.getNeeds().food = 100),
//                                    new CommandEntry("Food (w)", v -> _character.getNeeds().food = _character.getType().needs.food.warning),
//                                    new CommandEntry("Food (c)", v -> _character.getNeeds().food = _character.getType().needs.food.critical),
//                                    new CommandEntry("Relation (f)", v -> _character.getNeeds().relation = 100),
//                                    new CommandEntry("Relation (w)", v -> _character.getNeeds().relation = _character.getType().needs.relation.warning),
//                                    new CommandEntry("Relation (c)", v -> _character.getNeeds().relation = _character.getType().needs.relation.critical),
//                                    new CommandEntry("Joy (f)", v -> _character.getNeeds().joy = 100),
//                                    new CommandEntry("Joy (w)", v -> _character.getNeeds().joy = _character.getType().needs.joy.warning),
//                                    new CommandEntry("Joy (c)", v -> _character.getNeeds().joy = _character.getType().needs.joy.critical),
//                            }))
//            ),
//            new CommandEntry("Dump managers",       view -> {
//                Log.notice("\n----------- dump -----------");
//                ModuleManager.getInstance().getModules().forEach(manager -> manager.dump());
//            }),
//            new CommandEntry("Dump renders",       view -> {
//                Log.notice("\n----------- dump -----------");
//                MainRenderer.getInstance().getRenders().forEach(renderer -> renderer.dump());
//            }),
//            new CommandEntry("Dump panels",       view -> {
//                Log.notice("\n----------- dump -----------");
//                Arrays.asList(UserInterface.getInstance().getPanels()).forEach(panel -> panel.dump());
//            }),
//            new CommandEntry("Temperature debug",       view -> ModuleManager.getInstance().toggleModule(TemperatureDebugModule.class)),
////            new CommandEntry("Oxygen debug",            view -> UserInterface.getInstance().getPanel(OxygenManagerPanel.class).setVisible(true)),
//            new CommandEntry("Job detail",              view -> UserInterface.getInstance().getPanel(JobDebugPanel.class).setVisible(true)),
//            new CommandEntry("Parcel detail",           view -> UserInterface.getInstance().getPanel(ParcelDebugPanel.class).setVisible(true)),
//    };
//
//    private void openSubFrame(Collection<CommandEntry> commands) {
//        final UIFrame frameCommands = (UIFrame) mView.findById("frame_dev_commands");
//        frameCommands.setVisible(false);
//
//        final UIFrame frameConsumable = (UIFrame) mView.findById("frame_dev_sub");
//        frameConsumable.setVisible(true);
//        frameConsumable.removeAllViews();
//
//        int index = 0;
//        for (CommandEntry command: commands) {
//            UILabel lbConsumable = ViewFactory.getInstance().createTextView(100, 26);
//            lbConsumable.setTextSize(14);
//            lbConsumable.setTextAlign(View.Align.CENTER_VERTICAL);
//            lbConsumable.setPosition(10, index++ * 26);
//            lbConsumable.setOnClickListener(view -> {
//                command.listener.onClick(view);
//            });
//            lbConsumable.setText(command.label);
//            lbConsumable.setSize(200, 26);
//            frameConsumable.addView(lbConsumable);
//        }
//    }
//
//    @Override
//    protected void onLoaded() {
//        addWindow(WindowBuilder.create().setTitle("Debug").setContentLayout("panels/dev.yml").build(new WindowListener() {
//            @Override
//            public void onCreate(UIWindow window, UIFrame view) {
//                mView = view;
//                UIFrame frameCommands = (UIFrame) view.findById("frame_dev_commands");
//
//                int index = 0;
//                for (CommandEntry entry : COMMANDS) {
//                    UILabel lbCommand = ViewFactory.getInstance().createTextView(100, 26);
//                    lbCommand.setTextSize(14);
//                    lbCommand.setTextAlign(View.Align.CENTER_VERTICAL);
//                    lbCommand.setPosition(10, index++ * 26);
//                    lbCommand.setOnClickListener(entry.listener);
//                    lbCommand.setText(entry.label);
//                    lbCommand.setSize(200, 26);
//                    frameCommands.addView(lbCommand);
//                }
//
//                view.findById("bt_back").setOnClickListener(v -> {
//                    view.findById("frame_dev_commands").setVisible(true);
//                    view.findById("frame_dev_sub").setVisible(false);
//                });
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
//    }
//
//    @Override
//    protected void onUpdate(int tick) {
//        if (mView != null) {
//            ((UILabel) mView.findById("lb_mouse")).setText(
//                    UserInterface.getInstance().getMouseX() + "x" + UserInterface.getInstance().getMouseY());
//        }
//    }
//
//    @Override
//    public void onSelectCharacter(CharacterModel character) {
//        _character = character;
//    }
//
////    @Override
////    protected void onWindowRefresh(int update) {
////    }
//
//}
