package org.smallbox.faraway.client.debug;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;

/**
 * Created by Alex on 31/07/2016.
 */
@GameRenderer(level = MainRenderer.CONSUMABLE_RENDERER_LEVEL + 1)
public class DebugConsumableRenderer extends BaseRenderer {

    @BindModule
    private ConsumableModule consumableModule;

    @BindModule
    private JobModule jobModule;

    @Override
    public void onGameCreate(Game game) {
        setVisibility(false);
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        consumableModule.getConsumables()
                .forEach(consumable -> {
                    renderer.drawOnMap(consumable.getParcel().x, consumable.getParcel().y, consumableModule.hasLock(consumable) ? Color.CORAL : Color.CYAN);

                    renderer.drawOnMap(consumable.getParcel().x, consumable.getParcel().y, consumable.getLabel(), 14, Color.BLACK, 1, 1);
                    renderer.drawOnMap(consumable.getParcel().x, consumable.getParcel().y, consumable.getLabel(), 14, Color.WHITE);

                    renderer.drawOnMap(consumable.getParcel().x, consumable.getParcel().y, "x" + consumable.getQuantity(), 14, Color.BLACK, 1, 17);
                    renderer.drawOnMap(consumable.getParcel().x, consumable.getParcel().y, "x" + consumable.getQuantity(), 14, Color.WHITE, 0, 16);
                });
    }

    @Override
    public void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        if (action == GameEventListener.Action.RELEASED && key == GameEventListener.Key.F10) {
            toggleVisibility();
        }
    }

}
