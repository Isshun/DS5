package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbsInfoLuaController<T extends ObjectModel> extends LuaController {

    protected Queue<T> listSelected = new ConcurrentLinkedQueue<>();

    public void display(T object) {
        setVisible(true);
        listSelected.clear();
        listSelected.add(object);
        displayObjects();
    }

    // TODO: verifier que la liste des parcel contient au moins un item affichable
    public void displayToto(Collection<ParcelModel> parcelList) {
        setVisible(true);
        listSelected.clear();
        parcelList.forEach(parcel -> {
            T object = getObjectOnParcel(parcel);
            if (object != null) {
                listSelected.add(object);
            }
        });
        displayObjects();
    }

    @Override
    public boolean onKeyPress(int key) {
        if (key == Input.Keys.ESCAPE && CollectionUtils.isNotEmpty(listSelected)) {
//            mainPanelController.setVisible(true);
            listSelected.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean onClickOnParcel(List<ParcelModel> parcels) {
//        list = parcels.stream()
//                .map(this::getObjectOnParcel)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        if (CollectionUtils.isNotEmpty(list)) {
//            setVisible(true);
//            displayObjects();
//        }
//
//        return CollectionUtils.isNotEmpty(list);
        return false;
    }

    @Override
    public void onControllerUpdate() {
        if (CollectionUtils.isNotEmpty(listSelected)) {
            displayObjects();
        } else {
            DependencyInjector.getInstance().getDependency(MainPanelController.class).setVisible(true);
        }
    }

    protected void closePanel() {
        listSelected.clear();
        DependencyInjector.getInstance().getDependency(MainPanelController.class).setVisible(true);
    }

    private void displayObjects() {
        if (isVisible()) {
            if (listSelected.size() == 1) {
                onDisplayUnique(listSelected.peek());
            } else {
                onDisplayMultiple(listSelected);
            }
            DependencyInjector.getInstance().getDependency(GameSelectionManager.class).setSelected(listSelected);
        } else {
            DependencyInjector.getInstance().getDependency(GameSelectionManager.class).setSelected(null);
        }
    }

    protected abstract void onDisplayUnique(T t);

    protected abstract void onDisplayMultiple(Queue<T> objects);

    public abstract T getObjectOnParcel(ParcelModel parcel);

}
