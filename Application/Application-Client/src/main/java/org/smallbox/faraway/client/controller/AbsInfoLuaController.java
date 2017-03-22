package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
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
        setVisible(true);
        list = Collections.singletonList(object);
        displayObjects();
    }

    @Override
    public boolean onKeyPress(int key) {
        if (key == Input.Keys.ESCAPE && CollectionUtils.isNotEmpty(list)) {
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
            setVisible(true);
            displayObjects();
        }

        return CollectionUtils.isNotEmpty(list);
    }

    @Override
    public void onControllerUpdate() {
        if (CollectionUtils.isNotEmpty(list)) {
            displayObjects();
        } else {
            mainPanelController.setVisible(true);
        }
    }

    protected void closePanel() {
        list = null;
        mainPanelController.setVisible(true);
    }

    private void displayObjects() {
        if (isVisible()) {
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
