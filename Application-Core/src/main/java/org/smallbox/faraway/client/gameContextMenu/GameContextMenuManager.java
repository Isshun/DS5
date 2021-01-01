package org.smallbox.faraway.client.gameContextMenu;

import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.job.MoveJobFactory;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModule;

import java.util.Optional;

@GameObject
public class GameContextMenuManager {

    @Inject
    private GameSelectionManager gameSelectionManager;

    @Inject
    private MoveJobFactory moveJobFactory;

    @Inject
    private JobModule jobModule;

    private GameContextMenu menu;

    public void open(int mouseX, int mouseY, ParcelModel parcel) {
        menu = new GameContextMenu(mouseX, mouseY);

        Optional.ofNullable(gameSelectionManager.getSelected(CharacterModel.class)).ifPresent(character ->
                menu.addEntry("Move character", mouseX, mouseY, () -> jobModule.addJob(moveJobFactory.createJob(parcel, character))));

        menu.addEntry("Take object", mouseX, mouseY);
        menu.addEntry("Use object", mouseX, mouseY);
        menu.addEntry("Attack", mouseX, mouseY);
    }

    public GameContextMenu getMenu() {
        return menu;
    }

    public void click(int x, int y) {
        if (menu != null) {
            menu.getEntries().stream().filter(entry -> entry.contains(x, y)).findFirst().ifPresent(GameContextMenuEntry::action);
            menu = null;
        }
    }
}
