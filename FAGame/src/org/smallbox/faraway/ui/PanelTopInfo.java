package org.smallbox.faraway.ui;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.PowerManager;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.manager.WeatherManager;
import org.smallbox.faraway.ui.engine.*;
import org.smallbox.faraway.ui.panel.BasePanel;
import org.smallbox.faraway.util.Constant;

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

        TextView lbPlanet = (TextView)findById("lb_planet");
        lbPlanet.setString("Planet: " + Game.getInstance().getPlanet().getInfo().name);
        lbPlanet.setOnClickListener(view -> {
            ((PanelPlanet) UserInterface.getInstance().getPanel(PanelPlanet.class)).select(Game.getInstance().getRegion());
        });
        lbPlanet.resetAllPos();

        WeatherManager weatherManager = (WeatherManager)Game.getInstance().getManager(WeatherManager.class);
        if (weatherManager != null && weatherManager.getWeather() != null) {
            ((TextView) findById("lb_weather")).setString("Weather: " + weatherManager.getWeather().name);
            if (Constant.DEBUG) {
                findById("lb_weather").setOnClickListener(view -> weatherManager.next());
            }
        }

        PowerManager powerManager = (PowerManager)Game.getInstance().getManager(PowerManager.class);
        if (powerManager != null) {
            ((TextView)findById("lb_power_production")).setString((int)powerManager.getProduce() + "kW/h");
            ((TextView)findById("lb_power_storage")).setString((int)powerManager.getStored() + "kW");
        }
    }
}
