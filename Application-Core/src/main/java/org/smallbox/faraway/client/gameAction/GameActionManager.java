package org.smallbox.faraway.client.gameAction;

import com.badlogic.gdx.graphics.Color;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.ui.extra.Colors;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameAction.OnGameSelectAction;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaModule;
import org.smallbox.faraway.game.area.AreaTypeInfo;
import org.smallbox.faraway.game.building.BuildJobFactory;
import org.smallbox.faraway.game.building.DestructJobFactory;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.world.ObjectModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.util.log.Log;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@GameObject
public class GameActionManager extends GameManager {
    @Inject private AreaModule areaModule;
    @Inject private JobModule jobModule;
    @Inject private BuildJobFactory buildJobFactory;
    @Inject private DestructJobFactory destructJobFactory;
    @Inject private DependencyManager dependencyManager;
    @Inject private PlantModule plantModule;

    private Map<Class<? extends Annotation>, Map<OnGameSelectAction, Consumer<ObjectModel>>> gameActionsConsumers;
    private Collection<GameActionAreaListener> specializedAreaModules;
    private GameActionMode mode = GameActionMode.NONE;
    private OnSelectParcelListener areaAction;
    private AreaTypeInfo areaTypeInfo;
    private Color actionColor;
    private String actionLabel;
    private ItemInfo itemInfo;

    @OnInit
    private void init() {
        specializedAreaModules = dependencyManager.getSubTypesOf(GameActionAreaListener.class);
        gameActionsConsumers = new ConcurrentHashMap<>();
        gameActionsConsumers.put(OnGameSelectAction.class, dependencyManager.getMethodsAnnotatedBy(OnGameSelectAction.class));
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

    public void setBuildAction(ItemInfo itemInfo) {
        this.mode = GameActionMode.BUILD;
        this.itemInfo = itemInfo;
        this.actionColor = Color.BLUE;
        this.actionLabel = "Build " + itemInfo.label;
        this.areaAction = parcel -> {
            if (StringUtils.equals(itemInfo.type, "plant")) {
                plantModule.addPlant(itemInfo, parcel);
            } else {
                jobModule.add(buildJobFactory.createJob(itemInfo, parcel));
            }
        };
    }

    @GameShortcut("action/cancel")
    public void setCancelAction() {
        this.mode = GameActionMode.CANCEL;
        this.actionColor = Colors.COLOR_CURSOR;
        this.actionLabel = "Cancel";
        this.areaAction = parcel -> jobModule.removeIf(job -> job.getTargetParcel() == parcel);
    }

    @GameShortcut("action/destruct")
    public void setDestructAction() {
        this.mode = GameActionMode.DESTRUCT;
        this.actionColor = Colors.COLOR_CURSOR;
        this.actionLabel = "Destruct";
        this.areaAction = parcel -> jobModule.add(destructJobFactory.createJob(parcel));
    }

    public GameActionMode getMode() {
        return mode;
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

    public OnSelectParcelListener getAction() {
        return areaAction;
    }

    public void selectParcels(List<Parcel> parcelList) {
        if (areaAction != null) {
            parcelList.forEach(parcel -> areaAction.onParcelSelected(parcel));
        }
    }

    public void selectParcel(Parcel parcel) {
        if (areaAction != null) {
            areaAction.onParcelSelected(parcel);
        }

//        else if (hasArea(parcel)) {
//            selectArea(parcel);
//        }
//
//        else {
//            GroundInfoController.onSelectParcelOld(parcel);
//        }
    }

    public void removeArea(Parcel parcel) {
        specializedAreaModules.forEach(specializedAreaModule -> specializedAreaModule.removeArea(parcel));
    }

//    public boolean hasArea(ParcelModel parcel) {
//        return specializedAreaModules.stream().anyMatch(listener -> listener.hasArea(parcel));
//    }
//
//    public void selectArea(ParcelModel parcel) {
//        specializedAreaModules.stream()
//                .filter(listener -> listener.hasArea(parcel))
//                .findFirst()
//                .ifPresent(listener -> listener.selectArea(parcel));
//    }

    public boolean hasAction() {
        return mode != GameActionMode.NONE;
    }

    public void callActions(Class<? extends Annotation> annotation, ObjectModel object) {
        gameActionsConsumers.get(annotation).entrySet().stream()
                .filter(entry -> entry.getKey().value().isInstance(object))
                .forEach(entry -> entry.getValue().accept(object));
    }

}
