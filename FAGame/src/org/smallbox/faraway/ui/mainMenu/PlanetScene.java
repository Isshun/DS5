package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.PlanetModel;
import org.smallbox.faraway.ui.LayoutModel;

/**
 * Created by Alex on 02/06/2015.
 */
public class PlanetScene extends MainMenuScene {
    private static final Color COLOR_S3 = new Color(188, 28, 28);
    private static final Color COLOR_S2 = new Color(255, 137, 33);
    private static final Color COLOR_S1 = new Color(255, 238, 33);
    private static final Color COLOR_0 = new Color(120, 255, 255);
    private static final Color COLOR_1 = new Color(120, 255, 255);
    private static final Color COLOR_2 = new Color(120, 255, 255);

    private ViewFactory _viewFactory;

    public PlanetScene(MainMenu mainMenu, GFXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/planet_list.yml");
    }

    @Override
    public void onCreate(ViewFactory viewFactory) {
        _viewFactory = viewFactory;
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        FrameLayout framePlanetList = (FrameLayout) findById("frame_planet_list");
        framePlanetList.clearAllViews();

        int index = 0;
        for (PlanetModel planet: Application.getData().planets) {
            addPlanetListView(framePlanetList, planet, index++);
        }

        findById("bt_colonize").setOnClickListener(view -> {
            if (_mainMenu.getPlanet() != null) {
                _mainMenu.select(MainMenu.Scene.LAND_SITE);
            }
        });

        select(Application.getData().planets.get(0));
    }

    private void addPlanetListView(FrameLayout framePlanetList, PlanetModel planet, int index) {
        _viewFactory.load("data/ui/menu/planet_list_entry.yml", view -> {
            view.findById("frame_background").setVisible(false);
            ((TextView)view.findById("lb_planet")).setString(planet.name);
            ((TextView)view.findById("lb_type")).setString(planet.type);
            ((ImageView)view.findById("img_planet")).setImagePath(planet.image.thumb);
            view.setOnClickListener(v -> {
                for (View v1: framePlanetList.getViews()) {
                    v1.findById("frame_background").setVisible(false);
                }
                select(planet);
                view.findById("frame_background").setVisible(true);
            });
            view.setSize(260, 120);
            view.setPosition(20, 20 + index * 120);

            framePlanetList.addView(view);
        });
    }

    private void select(PlanetModel planet) {
        _mainMenu.select(planet);

        ((TextView)findById("lb_detail_planet")).setString(planet.name);
        ((TextView)findById("lb_detail_desc")).setString(planet.desc);

        formatPlanetStats((TextView)findById("lb_detail_water"), "Water", planet.stats.water);
        formatPlanetStats((TextView)findById("lb_detail_fertility"), "Fertility", planet.stats.fertility);
        formatPlanetStats((TextView)findById("lb_detail_atmosphere"), "Atmosphere", planet.stats.atmosphere);
        formatPlanetStats((TextView)findById("lb_detail_fauna"), "Fauna", planet.stats.fauna);
        formatPlanetStats((TextView)findById("lb_detail_flora"), "Flora", planet.stats.flora);

        formatPlanetHostileStats((TextView)findById("lb_detail_hostile_fauna"), "Hostile fauna", planet.stats.hostile_fauna);
        formatPlanetHostileStats((TextView)findById("lb_detail_hostile_humankind"), "Hostile humankind", planet.stats.hostile_humankind);
        formatPlanetHostileStats((TextView)findById("lb_detail_hostile_mechanic"), "Hostile mechanic", planet.stats.hostile_mechanic);
    }

    private void formatPlanetHostileStats(TextView textView, String label, int value) {
        if (value == 0) {
            textView.setString(StringUtils.getDashedString(GameData.getData().getString(label), "no", 39));
        } else {
            textView.setString(StringUtils.getDashedString(GameData.getData().getString(label), "yes (" + (value == 1 ? "few" : "many") + ")", 39));
        }
        switch (value) {
            case 0: textView.setColor(COLOR_0); break;
            case 1: textView.setColor(COLOR_S2); break;
            case 2: textView.setColor(COLOR_S3); break;
        }
    }

    private void formatPlanetStats(TextView textView, String label, int value) {
        textView.setString(StringUtils.getDashedString(
                GameData.getData().getString(label),
                StringUtils.getPlanetStatsText(value) + " (" + StringUtils.getPlanetStatsSymbol(value) + ")", 39));
        switch (value) {
            case -3: textView.setColor(COLOR_S3); break;
            case -2: textView.setColor(COLOR_S2); break;
            case -1: textView.setColor(COLOR_S1); break;
            case 0: textView.setColor(COLOR_0); break;
            case 1: textView.setColor(COLOR_1); break;
            case 2: textView.setColor(COLOR_2); break;
        }
    }
}
