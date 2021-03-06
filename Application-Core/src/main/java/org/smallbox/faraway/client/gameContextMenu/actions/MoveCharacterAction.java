package org.smallbox.faraway.client.gameContextMenu.actions;

import org.smallbox.faraway.client.gameContextMenu.GameContextMenuAction;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.job.MoveJobFactory;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.JobModule;

@GameObject
public class MoveCharacterAction implements GameContextMenuAction {
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private MoveJobFactory moveJobFactory;
    @Inject private JobModule jobModule;

    @Override
    public String getLabel() {
        return "Move character";
    }

    @Override
    public boolean check(Parcel parcel) {
        return gameSelectionManager.getSelected(CharacterModel.class) != null;
    }

    @Override
    public Runnable getRunnable(Parcel parcel, int mouseX, int mouseY) {
        CharacterModel character = gameSelectionManager.getSelected(CharacterModel.class);
        return () -> jobModule.add(moveJobFactory.createJob(parcel, character));
    }
}
