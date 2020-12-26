package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.selection.SelectionManager;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.MainPanelController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.render.layer.RoomLayer;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.RoomTypeInfo;
import org.smallbox.faraway.util.Log;

import java.util.Comparator;

@GameObject
public class RoomPanelController extends LuaController {

    @Inject
    protected SelectionManager selectionManager;

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private RoomModule roomModule;

    @BindLua
    private UIList listRoomsAdd;

    @BindLua
    private UIList listRoomsSub;

    @Inject
    private MainPanelController mainPanelController;

    @Inject
    private RoomLayer roomLayer;

    @AfterGameLayerInit
    public void afterGameLayerInit() {

        mainPanelController.addShortcut("Rooms", this);

        roomModule.getRoomClasses().stream()
                .sorted(Comparator.comparing(o -> o.getAnnotation(RoomTypeInfo.class).label()))
                .forEach(cls -> {

                    listRoomsAdd.addView(UILabel.create(null)
                            .setText(" + " + cls.getAnnotation(RoomTypeInfo.class).label())
                            .setTextColor(0xB4D4D3ff)
                            .setTextSize(18)
                            .setPadding(10)
                            .setSize(160, 40)
                            .setFocusBackgroundColor(0x25c9cbff)
                            .setRegularBackgroundColor(0x121c1eff)
                            .setOnClickListener((int x, int y) -> {
                                roomLayer.setMode(RoomLayer.Mode.ADD, cls);
                                selectionManager.setSelectionListener(parcels -> {
                                    Log.warning(RoomPanelController.class, "HELLO");
                                    roomLayer.setMode(RoomLayer.Mode.NONE, cls);
                                    roomModule.addRoom(cls, parcels);
                                    return true;
                                });
                            })
                    );

                    listRoomsSub.addView(UILabel.create(null)
                            .setText(" - " + cls.getAnnotation(RoomTypeInfo.class).label())
                            .setTextColor(0xB4D4D3ff)
                            .setTextSize(18)
                            .setPadding(10)
                            .setSize(160, 40)
                            .setFocusBackgroundColor(0x25c9cbff)
                            .setRegularBackgroundColor(0x121c1eff)
                            .setOnClickListener((int x, int y) -> {
                                roomLayer.setMode(RoomLayer.Mode.SUB, cls);
                                selectionManager.setSelectionListener(parcels -> {
                                    Log.warning(RoomPanelController.class, "HELLO");
                                    roomLayer.setMode(RoomLayer.Mode.NONE, cls);
                                    roomModule.removeArea(parcels);
                                    return true;
                                });
                                //            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.GARDEN);
                            })
                    );

                });

    }

    @Override
    public void onRefreshUI(int frame) {

    }

    @Override
    public void onMouseMove(int x, int y, int button) {
    }
}
