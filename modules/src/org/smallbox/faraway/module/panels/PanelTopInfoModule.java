package org.smallbox.faraway.module.panels;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.module.world.PowerModule;
import org.smallbox.faraway.module.world.TemperatureModule;
import org.smallbox.faraway.module.world.WeatherModule;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UILabel;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 13/06/2015.
 */
public class PanelTopInfoModule extends GameUIModule {

    private class PanelTopInfoModuleWindow extends UIWindow {
        @Override
        protected void onCreate(UIWindow window, UIFrame content) {
        }

        @Override
        public void onRefresh(int tick) {
            ((UILabel) findById("lb_world_tmp")).setText("Temperature: " + (int) ((TemperatureModule) ModuleManager.getInstance().getModule(TemperatureModule.class)).getTemperature() + "°");
            ((UILabel) findById("lb_hour")).setText("Hour: " + Game.getInstance().getHour() + "h");
            ((UILabel) findById("lb_day")).setText("Day: " + Game.getInstance().getDay());
            ((UILabel) findById("lb_year")).setText("Year: " + Game.getInstance().getYear());

            UILabel lbPlanet = (UILabel) findById("lb_planet");
            lbPlanet.setText("Planet: " + Game.getInstance().getPlanet().getInfo().name);
            lbPlanet.setOnClickListener(view -> {
//                ((PanelPlanet) UserInterface.getInstance().getPanel(PanelPlanet.class)).select(Game.getInstance().getRegion());
            });
//            lbPlanet.resetAllPos();

            WeatherModule weatherModule = (WeatherModule) ModuleManager.getInstance().getModule(WeatherModule.class);
            if (weatherModule != null && weatherModule.getWeather() != null) {
                ((UILabel) findById("lb_weather")).setText("Weather: " + weatherModule.getWeather().name);
                if (Constant.DEBUG) {
                    findById("lb_weather").setOnClickListener(view -> weatherModule.next());
                }
            }

            PowerModule powerModule = (PowerModule) ModuleManager.getInstance().getModule(PowerModule.class);
            if (powerModule != null) {
                ((UILabel) findById("lb_power_production")).setText((int) powerModule.getProduce() + "kW/h");
                ((UILabel) findById("lb_power_storage")).setText((int) powerModule.getStored() + "kW");
            }
        }

        @Override
        protected String getContentLayout() {
            return "panels/top_info";
        }
    }

    @Override
    protected void onLoaded() {
        addWindow(new PanelTopInfoModuleWindow());
    }

    @Override
    protected void onUpdate(int tick) {
    }
}
