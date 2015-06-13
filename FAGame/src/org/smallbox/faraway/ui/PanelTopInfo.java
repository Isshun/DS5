package org.smallbox.faraway.ui;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.ui.panel.BasePanel;

/**
 * Created by Alex on 13/06/2015.
 */
public class PanelTopInfo extends BasePanel {
    private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
    private static final int FRAME_HEIGHT = 32;

    public PanelTopInfo() {
        super(UserInterface.Mode.NONE, null, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, "data/ui/panels/top_info.yml");
    }

    @Override
    protected void onCreate(ViewFactory factory) {
        setBackgroundColor(Colors.BT_INACTIVE);

        View border = factory.createColorView(_width, 4);
        border.setBackgroundColor(Colors.BACKGROUND);
        border.setPosition(_x, _y + _height);
        addView(border);

        setAlwaysVisible(true);
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
    }

    @Override
    public void onRefresh(int tick) {
        ((TextView)findById("lb_world_tmp")).setString("Temperature: " + Game.getWorldManager().getTemperature() + "°");
        ((TextView)findById("lb_hour")).setString("Hour: " + Game.getInstance().getHour() + "h");
        ((TextView)findById("lb_day")).setString("Day: " + Game.getInstance().getDay());
        ((TextView)findById("lb_year")).setString("Year: " + Game.getInstance().getYear());

        if (Game.getWeatherManager() != null && Game.getWeatherManager().getWeather() != null) {
            ((TextView) findById("lb_weather")).setString("Weather: " + Game.getWeatherManager().getWeather().name);
        }
    }
}
