package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.planet.PlanetAreaModel;
import org.smallbox.faraway.core.game.model.planet.PlanetModel;
import org.smallbox.faraway.core.game.model.planet.RegionModel;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.core.util.Constant;

/**
 * Created by Alex on 17/06/2015.
 */
public class PanelPlanet extends BasePanel {
    private RegionModel _region;
    private PlanetModel _planet;

    public PanelPlanet() {
        super(null, null, 100, 100, 1700, 1000, "data/ui/panels/planet.yml");
        setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onDraw(GDXRenderer renderer, Viewport viewport) {
        if (_region != null) {
            UIFrame view = new UIFrame(10, 10);
            for (int x = 0; x < Constant.PLANET_WIDTH; x++) {
                for (int y = 0; y < Constant.PLANET_HEIGHT; y++) {
                    PlanetAreaModel area = _planet.getAreas(x, y);
                    view.setBackgroundColor(new Color(area.region.color));
                    renderer.draw(view, _x + 20 + x * 10, _y + 100 + y * 10);
                }
            }
        }
    }

    public void select(RegionModel region) {
        _isVisible = true;
        _region = region;
        _planet = region.getPlanet();
    }
}
