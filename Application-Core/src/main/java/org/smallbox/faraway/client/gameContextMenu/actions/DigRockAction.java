package org.smallbox.faraway.client.gameContextMenu.actions;

import org.smallbox.faraway.client.gameContextMenu.GameContextMenuAction;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.dig.DigJobFactory;
import org.smallbox.faraway.modules.job.JobModule;

@GameObject
public class DigRockAction implements GameContextMenuAction {

    @Inject
    private DigJobFactory digJobFactory;

    @Inject
    private JobModule jobModule;

    @Override
    public String getLabel() {
        return "Dig rock";
    }

    @Override
    public boolean check(Parcel parcel, int mouseX, int mouseY) {
        return parcel.getRockInfo() != null;
    }

    @Override
    public Runnable getRunnable(Parcel parcel, int mouseX, int mouseY) {
        return () -> jobModule.add(digJobFactory.createJob(parcel));
    }
}
