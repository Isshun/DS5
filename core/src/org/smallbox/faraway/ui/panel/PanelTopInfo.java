package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.world.PowerModule;
import org.smallbox.faraway.game.module.world.TemperatureModule;
import org.smallbox.faraway.game.module.world.WeatherModule;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;
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
    public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
    }

    @Override
    public void onRefresh(int tick) {
        ((UILabel)findById("lb_world_tmp")).setText("Temperature: " + (int) ((TemperatureModule) ModuleManager.getInstance().getModule(TemperatureModule.class)).getTemperature() + "°");
        ((UILabel)findById("lb_hour")).setText("Hour: " + Game.getInstance().getHour() + "h");
        ((UILabel)findById("lb_day")).setText("Day: " + Game.getInstance().getDay());
        ((UILabel)findById("lb_year")).setText("Year: " + Game.getInstance().getYear());

        UILabel lbPlanet = (UILabel)findById("lb_planet");
        lbPlanet.setText("Planet: " + Game.getInstance().getPlanet().getInfo().name);
        lbPlanet.setOnClickListener(view -> {
            ((PanelPlanet) UserInterface.getInstance().getPanel(PanelPlanet.class)).select(Game.getInstance().getRegion());
        });
        lbPlanet.resetAllPos();

        WeatherModule weatherModule = (WeatherModule)ModuleManager.getInstance().getModule(WeatherModule.class);
        if (weatherModule != null && weatherModule.getWeather() != null) {
            ((UILabel) findById("lb_weather")).setText("Weather: " + weatherModule.getWeather().name);
            if (Constant.DEBUG) {
                findById("lb_weather").setOnClickListener(view -> weatherModule.next());
            }
        }

        PowerModule powerModule = (PowerModule)ModuleManager.getInstance().getModule(PowerModule.class);
        if (powerModule != null) {
            ((UILabel)findById("lb_power_production")).setText((int) powerModule.getProduce() + "kW/h");
            ((UILabel)findById("lb_power_storage")).setText((int) powerModule.getStored() + "kW");
        }
    }
}
