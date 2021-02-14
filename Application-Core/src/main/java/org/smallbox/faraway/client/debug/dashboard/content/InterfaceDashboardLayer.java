package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.input.GameEventManager;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;

@GameObject
public class InterfaceDashboardLayer extends DashboardLayerBase {
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private LayerManager layerManager;
    @Inject private GameEventManager gameEventManager;
    @Inject private UIManager uiManager;
    @Inject private Viewport viewport;
    @Inject private Game game;

    @Override
    protected void onDraw(BaseRenderer renderer, int frame) {
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        drawDebug(renderer, "VIEWPORT", "Heap: " + ((heapSize - heapFreeSize) / 1000 / 1000) + "mb");

        drawDebug(renderer, "VIEWPORT", "Floor: " + viewport.getFloor());
        drawDebug(renderer, "VIEWPORT", "Size: " + viewport.getWidth() + " x " + viewport.getHeight());

        drawDebug(renderer, "WORLD", "Size: " + game.getInfo().worldWidth + " x " + game.getInfo().worldHeight + " x " + game.getInfo().worldFloors);
        drawDebug(renderer, "WORLD", "Ground floor: " + game.getInfo().groundFloor);

        if (gameSelectionManager.getSelected() != null) {
            gameSelectionManager.getSelected().forEach(selected -> drawDebug(renderer, "SELECTION", "Current: " + selected));
        }

        drawDebug(renderer, "Cursor screen position", gameEventManager.getMouseX() + " x " + gameEventManager.getMouseY());
        drawDebug(renderer, "Cursor world position", viewport.getWorldPosX(gameEventManager.getMouseX()) + " x " + viewport.getWorldPosY(gameEventManager.getMouseY()));

        Parcel parcel = WorldHelper.getParcel(
                viewport.getWorldPosX(gameEventManager.getMouseX()),
                viewport.getWorldPosY(gameEventManager.getMouseY()),
                viewport.getFloor());
        drawDebug(renderer, "Parcel isWalkable", parcel != null ? String.valueOf(parcel.isWalkable()) : "no parcel");
        drawDebug(renderer, "###############################", "");

        uiManager.getRootViews().forEach(rootView -> drawDebug(renderer, rootView.getView().getPath(), rootView.getView().isVisible() ? "visible" : ""));
    }

}
