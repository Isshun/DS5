package org.smallbox.faraway.client.gameContextMenu;

import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Collection;

@GameObject
public class GameContextMenuManager {

    @Inject
    private DependencyManager dependencyManager;

    private Collection<GameContextMenuAction> actions;
    private GameContextMenu menu;

    @OnInit
    public void init() {
        actions = dependencyManager.getSubTypesOf(GameContextMenuAction.class);
    }

    public void open(ParcelModel parcel, int mouseX, int mouseY) {
        if (parcel != null) {
            GameContextMenu menu = new GameContextMenu(mouseX, mouseY);
            actions.stream()
                    .filter(action -> action.check(parcel, mouseX, mouseY))
                    .forEach(action -> menu.addEntry(action.getLabel(), mouseX, mouseY, action.getRunnable(parcel, mouseX, mouseY)));
            if (CollectionUtils.isNotEmpty(menu.getEntries())) {
                this.menu = menu;
            }
        }
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
