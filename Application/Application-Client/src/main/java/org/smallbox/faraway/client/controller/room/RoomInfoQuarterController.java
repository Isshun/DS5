package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.QuarterRoom;
import org.smallbox.faraway.modules.room.model.RoomModel;

import java.util.Queue;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class RoomInfoQuarterController extends AbsInfoLuaController<RoomModel> {

    @BindComponent
    private RoomModule roomModule;

    @BindComponent
    private CharacterModule characterModule;

    @BindComponent
    private Data data;

    @BindLua
    private UILabel lbOwner;

    @BindLua
    private UIList listCharacters;

    @BindLua
    private UILabel btDefineOwner;

    @Override
    protected void onDisplayUnique(RoomModel room) {
        setVisible(true);

        if (room.getOwner() != null) {
            btDefineOwner.setVisible(false);
            lbOwner.setVisible(true);
            lbOwner.setText("Owner: " + room.getOwner().getName());
        } else {
            btDefineOwner.setVisible(true);
            lbOwner.setVisible(false);
        }
    }

    @Override
    protected void onDisplayMultiple(Queue<RoomModel> objects) {
    }

    @Override
    public RoomModel getObjectOnParcel(ParcelModel parcel) {
        RoomModel room = roomModule.getRoom(parcel);
        return room instanceof QuarterRoom ? room : null;
    }

    @BindLuaAction
    private void onDefineOwner(View view) {
        listCharacters.removeAllViews();

        characterModule.getCharacters().forEach(character -> listCharacters.addView(UILabel.create(null)
                .setText(character.getName())
                .setTextColor(0x5588bbff)
                .setSize(100, 20)
                .setOnClickListener((x, y) -> {
                    listSelected.forEach(room -> room.setOwner(character));
                    listCharacters.setVisible(false);
                })));

        listCharacters.setVisible(true);
    }

}
