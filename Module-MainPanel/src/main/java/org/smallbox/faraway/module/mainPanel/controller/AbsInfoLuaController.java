package org.smallbox.faraway.module.mainPanel.controller;

import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.module.mainPanel.MainPanelController;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Alex on 03/12/2016.
 */
public abstract class AbsInfoLuaController<T> extends LuaController {

    @BindLuaController
    private MainPanelController mainPanelController;

    private List<T> list;

    @Override
    public void onKeyPress(GameEventListener.Key key) {
        if (key == GameEventListener.Key.ESCAPE) {
            if (CollectionUtils.isNotEmpty(list)) {
                mainPanelController.setVisible(true);
                list = null;
            }
        }
    }

    @Override
    public boolean onClickOnParcel(List<ParcelModel> parcels) {
        list = parcels.stream()
                .map(this::getObjectOnParcel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(list)) {
            displayObjects();
        } else if (isVisible()) {
            mainPanelController.setVisible(true);
        }

        return CollectionUtils.isNotEmpty(list);
    }

    @Override
    public void onGameUpdate(Game game) {
        if (CollectionUtils.isNotEmpty(list)) {
            displayObjects();
        }
    }

    private void displayObjects() {
        setVisible(true);

        if (list.size() == 1) {
            onDisplayUnique(list.get(0));
        } else {
            onDisplayMultiple(list);
        }
    }

    protected abstract void onDisplayUnique(T t);

    protected abstract void onDisplayMultiple(List<T> list);

    protected abstract T getObjectOnParcel(ParcelModel parcel);

}
