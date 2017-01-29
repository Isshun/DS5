package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Alex on 03/12/2016.
 */
public abstract class AbsInfoLuaController<T> extends LuaController {

    @BindLuaController
    private MainPanelController mainPanelController;

    protected List<T> list;

    public void display(T object) {
        list = Collections.singletonList(object);
        mainPanelController.setCurrentController(this);
        displayObjects();
    }

    @Override
    public boolean onKeyPress(GameEventListener.Key key) {
        if (key == GameEventListener.Key.ESCAPE && CollectionUtils.isNotEmpty(list)) {
            mainPanelController.setVisible(true);
            list = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean onClickOnParcel(List<ParcelModel> parcels) {
        list = parcels.stream()
                .map(this::getObjectOnParcel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(list)) {
            mainPanelController.setCurrentController(this);
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
        if (mainPanelController.getCurrentController() == this) {
            setVisible(true);

            if (list.size() == 1) {
                onDisplayUnique(list.get(0));
            } else {
                onDisplayMultiple(list);
            }
        }
    }

    protected abstract void onDisplayUnique(T t);

    protected abstract void onDisplayMultiple(List<T> list);

    protected abstract T getObjectOnParcel(ParcelModel parcel);

}
