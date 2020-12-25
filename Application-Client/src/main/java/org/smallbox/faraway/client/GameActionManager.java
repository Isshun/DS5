package org.smallbox.faraway.client;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.util.Log;

import java.util.List;

@GameObject
public class GameActionManager extends GameManager {

    private GameActionMode mode = GameActionMode.NONE;
    private AreaModel areaAction;
    private AreaTypeInfo areaTypeInfo;
    private Color actionColor;
    private String actionLabel;

    public void setMode(GameActionMode mode) {
        this.mode = mode;
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
        parcelList.forEach(parcel -> areaAction.execute(parcel));
    }

}
