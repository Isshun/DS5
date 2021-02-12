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
import org.smallbox.faraway.util.transition.ColorTransition;

import java.util.Optional;

@GameObject
@GameLayer(level = LayerLevel.INFO_POPUP_LEVEL, visible = false)
public class InfoTemperaturePopupLayer extends BaseMapLayer {
    @Inject private WorldModule worldModule;
    @Inject private TemperatureModule temperatureModule;
    @Inject private InputManager inputManager;
    @Inject private UIRenderer uiRenderer;

    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        Optional.ofNullable(worldModule.getParcel(viewport.getWorldPosX(inputManager.getMouseX()), viewport.getWorldPosY(inputManager.getMouseY()), viewport.getFloor())).ifPresent(parcel -> {
            Color color = buildColor((int) temperatureModule.getTemperature(parcel));
            Color color2 = buildColor((int) temperatureModule.getTemperature(parcel));
            color2.a = 0.25f;
            uiRenderer.drawRectangle(inputManager.getMouseX() + 10, inputManager.getMouseY() - 10, 100, 50, color2);
            uiRenderer.drawCadre(inputManager.getMouseX() + 10, inputManager.getMouseY() - 10, 100, 50, color, 4);
            uiRenderer.drawText(inputManager.getMouseX() + 20, inputManager.getMouseY(), String.valueOf(Math.round(temperatureModule.getTemperature(parcel))), color, 22, false, "sui", 2);
        });
    }

    private Color buildColor(Integer temperature) {
        if (temperature < 20) {
            return new Color(new ColorTransition(0x0000ff80, 0x00ff0080).getValue((temperature + 80) / 100f));
        } else {
            return new Color(new ColorTransition(0x00ff0080, 0xff000080).getValue((temperature - 20) / 100f));
        }
    }

    @GameShortcut("info/temperature")
    public void display() {
        toggleVisibility();
    }

}
