package org.smallbox.faraway.client.gameAction;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.dependencyInjector.gameAction.OnGameSelectAction;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.modules.building.BuildJobFactory;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.util.log.Log;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@GameObject
public class GameActionManager extends GameManager {

    @Inject
    private AreaModule areaModule;

    @Inject
    private JobModule jobModule;

    @Inject
    private BuildJobFactory buildJobFactory;

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
        specializedAreaModules = DependencyInjector.getInstance().getSubTypesOf(GameActionAreaListener.class);
        gameActionsConsumers = new ConcurrentHashMap<>();
        gameActionsConsumers.put(OnGameSelectAction.class, DependencyInjector.getInstance().getMethodsAnnotatedBy(OnGameSelectAction.class));
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
            jobModule.addJob(buildJobFactory.createJob(itemInfo, parcel));
        };
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

    public void selectParcels(List<ParcelModel> parcelList) {
        if (areaAction != null) {
            parcelList.forEach(parcel -> areaAction.onParcelSelected(parcel));
        }
    }

    public void selectParcel(ParcelModel parcel) {
        if (areaAction != null) {
            areaAction.onParcelSelected(parcel);
        }

//        else if (hasArea(parcel)) {
//            selectArea(parcel);
//        }
//
//        else {
//            selectionInfoController.onSelectParcelOld(parcel);
//        }
    }

    public void removeArea(ParcelModel parcel) {
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
