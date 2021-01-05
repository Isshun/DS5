package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.GameEventManager;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

@GameObject
public class InterfaceDashboardLayer extends DashboardLayerBase {

    @Inject
    private Game game;

    @Inject
    private GameSelectionManager gameSelectionManager;

    @Inject
    private LayerManager layerManager;

    @Inject
    private GameEventManager gameEventManager;

    @Inject
    private UIManager uiManager;

    @Override
    protected void onDraw(GDXRenderer renderer, int frame) {
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        drawDebug(renderer, "VIEWPORT", "Heap: " + ((heapSize - heapFreeSize) / 1000 / 1000) + "mb");

        drawDebug(renderer, "VIEWPORT", "Floor: " + layerManager.getViewport().getFloor());
        drawDebug(renderer, "VIEWPORT", "Size: " + layerManager.getViewport().getWidth() + " x " + layerManager.getViewport().getHeight());

        drawDebug(renderer, "WORLD", "Size: " + game.getInfo().worldWidth + " x " + game.getInfo().worldHeight + " x " + game.getInfo().worldFloors);
        drawDebug(renderer, "WORLD", "Ground floor: " + game.getInfo().groundFloor);

        if (gameSelectionManager.getSelected() != null) {
            gameSelectionManager.getSelected().forEach(selected -> drawDebug(renderer, "SELECTION", "Current: " + selected));
        }

        drawDebug(renderer, "Cursor screen position", gameEventManager.getMouseX() + " x " + gameEventManager.getMouseY());
        drawDebug(renderer, "Cursor world position", layerManager.getViewport().getWorldPosX(gameEventManager.getMouseX()) + " x " + layerManager.getViewport().getWorldPosY(gameEventManager.getMouseY()));

        ParcelModel parcel = WorldHelper.getParcel(
                layerManager.getViewport().getWorldPosX(gameEventManager.getMouseX()),
                layerManager.getViewport().getWorldPosY(gameEventManager.getMouseY()),
                layerManager.getViewport().getFloor());
        drawDebug(renderer, "Parcel isWalkable", parcel != null ? String.valueOf(parcel.isWalkable()) : "no parcel");
        drawDebug(renderer, "###############################", "");

        uiManager.getRootViews().forEach(rootView -> {
            drawDebug(renderer, rootView.getView().getPath(), rootView.getView().isVisible() ? "visible" : "");
        });
    }

}
