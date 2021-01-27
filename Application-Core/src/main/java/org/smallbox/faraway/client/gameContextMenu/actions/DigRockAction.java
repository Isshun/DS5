package org.smallbox.faraway.client.gameContextMenu.actions;

import org.smallbox.faraway.client.gameContextMenu.GameContextMenuAction;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.dig.factory.DigRockJobFactory;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.world.Parcel;

@GameObject
public class DigRockAction implements GameContextMenuAction {
    @Inject private DigRockJobFactory digRockJobFactory;
    @Inject private JobModule jobModule;

    @Override
    public String getLabel() {
        return "Dig rock";
    }

    @Override
    public boolean check(Parcel parcel) {
        return parcel.getRockInfo() != null;
    }

    @Override
    public Runnable getRunnable(Parcel parcel, int mouseX, int mouseY) {
        return () -> jobModule.add(digRockJobFactory.createJob(parcel));
    }
}
