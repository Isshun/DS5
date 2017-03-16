package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.MainPanelController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.renderer.RoomRenderer;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.RoomTypeInfo;
import org.smallbox.faraway.util.Log;

import java.util.Comparator;

/**
 * Created by Alex on 26/04/2016.
 */
public class RoomPanelController extends LuaController {

    @BindComponent
    private UIEventManager uiEventManager;

    @BindModule
    private RoomModule roomModule;

    @BindLua
    private UIList listRoomsAdd;

    @BindLua
    private UIList listRoomsSub;

    @BindLuaController
    private MainPanelController mainPanelController;

    @BindComponent
    private RoomRenderer roomRenderer;

    @Override
    public void onReloadUI() {

        mainPanelController.addShortcut("Rooms", this);

        roomModule.getRoomClasses().stream()
                .sorted(Comparator.comparing(o -> o.getAnnotation(RoomTypeInfo.class).label()))
                .forEach(cls -> {

                    listRoomsAdd.addView(UILabel.create(null)
                            .setText(" + " + cls.getAnnotation(RoomTypeInfo.class).label())
                            .setTextColor(0xB4D4D3)
                            .setTextSize(18)
                            .setPadding(10)
                            .setSize(160, 40)
                            .setFocusBackgroundColor(0x25c9cb)
                            .setRegularBackgroundColor(0x121c1e)
                            .setOnClickListener(event -> {
                                roomRenderer.setMode(RoomRenderer.Mode.ADD, cls);
                                uiEventManager.setSelectionListener(parcels -> {
                                    Log.warning(RoomPanelController.class, "HELLO");
                                    roomRenderer.setMode(RoomRenderer.Mode.NONE, cls);
                                    roomModule.addRoom(cls, parcels);
                                });
                            })
                    );

                    listRoomsSub.addView(UILabel.create(null)
                            .setText(" - " + cls.getAnnotation(RoomTypeInfo.class).label())
                            .setTextColor(0xB4D4D3)
                            .setTextSize(18)
                            .setPadding(10)
                            .setSize(160, 40)
                            .setFocusBackgroundColor(0x25c9cb)
                            .setRegularBackgroundColor(0x121c1e)
                            .setOnClickListener(event -> {
                                roomRenderer.setMode(RoomRenderer.Mode.SUB, cls);
                                uiEventManager.setSelectionListener(parcels -> {
                                    Log.warning(RoomPanelController.class, "HELLO");
                                    roomRenderer.setMode(RoomRenderer.Mode.NONE, cls);
                                    roomModule.removeArea(parcels);
                                });
                                //            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.GARDEN);
                            })
                    );

                });

    }

    @Override
    protected void onNewGameUpdate(Game game) {
    }

    @Override
    public void onRefreshUI(int frame) {

    }

    @Override
    public void onMouseMove(GameEvent event) {
    }
}