package org.smallbox.faraway.client.layer.info;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.input.InputManager;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.UIRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.temperature.TemperatureModule;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.Optional;

@GameObject
@GameLayer(level = LayerLevel.INFO_POPUP_LEVEL, visible = false)
public class InfoTemperaturePopupLayer extends BaseMapLayer {
    @Inject private WorldModule worldModule;
    @Inject private TemperatureModule temperatureModule;
    @Inject private InputManager inputManager;
    @Inject private UIRenderer uiRenderer;

    private Color color = new Color(0xff0000ff);

    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        Optional.ofNullable(worldModule.getParcel(viewport.getWorldPosX(inputManager.getMouseX()), viewport.getWorldPosY(inputManager.getMouseY()), viewport.getFloor())).ifPresent(parcel -> {
            uiRenderer.drawRectangle(inputManager.getMouseX() + 10, inputManager.getMouseY() - 10, 200, 200, Color.WHITE);
            uiRenderer.drawText(inputManager.getMouseX() + 20, inputManager.getMouseY(), String.valueOf(temperatureModule.getTemperature(parcel)), color, 22, false, "sui", 2);
        });
    }

    @GameShortcut("info/temperature")
    public void display() {
        toggleVisibility();
    }

}
