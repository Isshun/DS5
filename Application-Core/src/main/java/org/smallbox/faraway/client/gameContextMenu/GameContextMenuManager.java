package org.smallbox.faraway.client.gameContextMenu;

import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Collection;

@GameObject
public class GameContextMenuManager {

    private Collection<GameContextMenuAction> actions;
    private GameContextMenu menu;

    @OnInit
    public void init() {
        actions = DependencyInjector.getInstance().getSubTypesOf(GameContextMenuAction.class);
    }

    public void open(ParcelModel parcel, int mouseX, int mouseY) {
        menu = new GameContextMenu(mouseX, mouseY);
        actions.stream()
                .filter(action -> action.check(parcel, mouseX, mouseY))
                .forEach(action -> menu.addEntry(action.getLabel(), mouseX, mouseY, action.getRunnable(parcel, mouseX, mouseY)));
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
