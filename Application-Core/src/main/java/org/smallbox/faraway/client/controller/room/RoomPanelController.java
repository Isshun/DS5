package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.MainPanelController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.layer.area.RoomLayer;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.game.room.RoomModule;

@GameObject
public class RoomPanelController extends LuaController {

    @Inject
    protected GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private RoomModule roomModule;

    @BindLua
    private UIList listRoomsAdd;

    @BindLua
    private UIList listRoomsSub;
    @Inject private MainPanelController mainPanelController;
    @Inject private RoomLayer roomLayer;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
//        roomModule.getRoomClasses().stream()
//                .sorted(Comparator.comparing(o -> o.getAnnotation(RoomTypeInfo.class).label()))
//                .forEach(cls -> {
//
//                    listRoomsAdd.addView(UILabel.create(null)
//                            .setText(" + " + cls.getAnnotation(RoomTypeInfo.class).label())
//                            .setTextColor(0xB4D4D3ff)
//                            .setTextSize(18)
//                            .getGeometry().setPadding(10, 10, 10, 10)
//                            .setSize(160, 40)
//                            .setFocusBackgroundColor(0x25c9cbff)
//                            .setRegularBackgroundColor(0x121c1eff)
//                            .getEvents().setOnClickListener(() -> {
//                                roomLayer.setMode(RoomLayer.Mode.ADD, cls);
//                                gameSelectionManager.setSelectionListener(parcels -> {
//                                    Log.warning(RoomPanelController.class, "HELLO");
//                                    roomLayer.setMode(RoomLayer.Mode.NONE, cls);
//                                    roomModule.addRoom(cls, parcels);
//                                    return true;
//                                });
//                            })
//                    );
//
//                    listRoomsSub.addView(UILabel.create(null)
//                            .setText(" - " + cls.getAnnotation(RoomTypeInfo.class).label())
//                            .setTextColor(0xB4D4D3ff)
//                            .setTextSize(18)
//                            .getGeometry().setPadding(10, 10, 10, 10)
//                            .setSize(160, 40)
//                            .setFocusBackgroundColor(0x25c9cbff)
//                            .setRegularBackgroundColor(0x121c1eff)
//                            .getEvents().setOnClickListener(() -> {
//                                roomLayer.setMode(RoomLayer.Mode.SUB, cls);
//                                gameSelectionManager.setSelectionListener(parcels -> {
//                                    Log.warning(RoomPanelController.class, "HELLO");
//                                    roomLayer.setMode(RoomLayer.Mode.NONE, cls);
//                                    roomModule.removeArea(parcels);
//                                    return true;
//                                });
//                                //            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.GARDEN);
//                            })
//                    );
//
//                });
//
    }

    @Override
    public void onRefreshUI(int frame) {

    }

    @Override
    public void onMouseMove(int x, int y, int button) {
    }
}
