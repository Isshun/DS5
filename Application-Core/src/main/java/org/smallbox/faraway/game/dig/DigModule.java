package org.smallbox.faraway.game.dig;

import com.badlogic.gdx.Input;
import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameAction.GameActionMode;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.area.AreaModule;
import org.smallbox.faraway.game.area.AreaModuleBase;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.JobModule;

import java.util.List;
import java.util.stream.Collectors;

@GameObject
public class DigModule extends AreaModuleBase<DigAction> {
    @Inject private JobModule jobModule;
    @Inject private AreaModule areaModule;
    @Inject private DigJobFactory digJobFactory;
    @Inject private GameActionManager gameActionManager;
    @Inject private DigUnderAction digUnderAction;
    @Inject private DigAction digAction;

    @OnInit
    public void init() {
        areaModule.addAreaClass(DigAction.class);
    }

    @Override
    protected void onModuleUpdate(Game game) {
        List<Parcel> parcelInDigArea = areaModule.getParcelsByType(DigAction.class);
        List<Parcel> parcelInDigJob = jobModule.getAll().stream()
                .filter(job -> job instanceof DigJob)
                .map(JobModel::getTargetParcel)
                .collect(Collectors.toList());

        // Create missing dig job
        parcelInDigArea.stream()
                .filter(parcel -> parcel.getRockInfo() != null)
                .filter(parcel -> !CollectionUtils.containsAny(parcelInDigJob, parcel))
                .forEach(parcel -> jobModule.add(digJobFactory.createJob(parcel)));
    }

    @Override
    public DigAction onNewArea() {
        return digAction;
    }

    @GameShortcut(key = Input.Keys.G)
    public void digMode() {
        gameActionManager.setAreaAction(GameActionMode.ADD_AREA, gameActionManager.getAction() == digAction ? digUnderAction : digAction);
    }

}
