package org.smallbox.faraway.client;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.controller.SelectionInfoController;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.area.GameActionAreaListener;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.util.Log;

import java.util.Collection;
import java.util.List;

@GameObject
public class GameActionManager extends GameManager {

    @Inject
    private AreaModule areaModule;

    @Inject
    private SelectionInfoController selectionInfoController;

    private Collection<GameActionAreaListener> specializedAreaModules;
    private GameActionMode mode = GameActionMode.NONE;
    private AreaModel areaAction;
    private AreaTypeInfo areaTypeInfo;
    private Color actionColor;
    private String actionLabel;

    @OnInit
    private void init() {
        specializedAreaModules = DependencyInjector.getInstance().getSubTypesOf(GameActionAreaListener.class);
    }

    public void clearAction() {
        this.mode = GameActionMode.NONE;
        this.areaAction = null;
    }

    public void setAreaAction(GameActionMode mode, AreaModel areaModel) {
        Log.info("Area action: " + areaModel);
        this.mode = mode;
        this.areaAction = areaModel;
        this.areaTypeInfo = areaModel.getClass().getAnnotation(AreaTypeInfo.class);
        this.actionColor = new Color(areaModel.getClass().getAnnotation(AreaTypeInfo.class).color());
        this.actionLabel = areaModel.getClass().getAnnotation(AreaTypeInfo.class).label();
    }

    public GameActionMode getMode() {
        return mode;
    }

    public AreaModel getAreaAction() {
        return areaAction;
    }

    public AreaTypeInfo getAreaTypeInfo() {
        return areaTypeInfo;
    }

    public Color getActionColor() {
        return actionColor;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public void selectParcels(List<ParcelModel> parcelList) {
        if (areaAction != null) {
            parcelList.forEach(parcel -> areaAction.execute(parcel));
        }
    }

    public void selectParcel(ParcelModel parcel) {
        if (areaAction != null) {
            areaAction.execute(parcel);
        }

        else if (hasArea(parcel)) {
            selectArea(parcel);
        }

        else {
            selectionInfoController.onSelectParcelOld(parcel);
        }
    }

    public void removeArea(ParcelModel parcel) {
        specializedAreaModules.forEach(specializedAreaModule -> specializedAreaModule.removeArea(parcel));
    }

    public boolean hasArea(ParcelModel parcel) {
        return specializedAreaModules.stream().anyMatch(listener -> listener.hasArea(parcel));
    }

    public void selectArea(ParcelModel parcel) {
        specializedAreaModules.stream()
                .filter(listener -> listener.hasArea(parcel))
                .findFirst()
                .ifPresent(listener -> listener.selectArea(parcel));
    }
}
