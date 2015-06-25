package org.smallbox.faraway.ui;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.planet.PlanetAreaModel;
import org.smallbox.faraway.ui.engine.ColorView;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.panel.BasePanel;

/**
 * Created by Alex on 17/06/2015.
 */
public class PanelPlanet extends BasePanel {
    public PanelPlanet() {
        super(null, null, 100, 100, 1700, 1000, "data/ui/panels/planet.yml");
        setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onDraw(GFXRenderer renderer, RenderEffect effect) {
        if (isVisible()) {
            ColorView view = ViewFactory.getInstance().createColorView(10, 10);
            for (int x = 0; x < 100; x++) {
                for (int y = 0; y < 80; y++) {
                    PlanetAreaModel area = Game.getInstance().getPlanet().getAreas(x, y);
                    view.setBackgroundColor(area.type == 0 ? Color.CYAN : Color.GREEN);
                    renderer.draw(view, _x + 20 + x * 10, _y + 100 + y * 10);
                }
            }
        }
    }

}
