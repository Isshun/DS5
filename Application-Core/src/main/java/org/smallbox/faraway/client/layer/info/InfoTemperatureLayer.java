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
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.util.transition.ColorTransition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerLevel.INFO_LEVEL, visible = false)
public class InfoTemperatureLayer extends BaseMapLayer {
    @Inject private WorldModule worldModule;
    @Inject private TemperatureModule temperatureModule;
    @Inject private InputManager inputManager;
    @Inject private UIRenderer uiRenderer;

    private final Map<Integer, Color> cache = new ConcurrentHashMap<>();

    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        worldModule.getAll().stream()
                .filter(parcel -> parcel.z == viewport.getFloor())
                .forEach(parcel -> renderer.drawRectangleOnMap(parcel, TILE_SIZE, TILE_SIZE, getColor(parcel), 0, 0));
    }

    private Color getColor(Parcel parcel) {
        return cache.computeIfAbsent(Utils.bound(-80, 120, (int)temperatureModule.getTemperature(parcel)), this::buildColor);
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
