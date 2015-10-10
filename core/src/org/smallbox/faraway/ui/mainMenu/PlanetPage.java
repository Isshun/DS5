package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.planet.PlanetInfo;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UIImage;
import org.smallbox.faraway.ui.engine.views.UILabel;
import org.smallbox.faraway.ui.engine.views.View;
import org.smallbox.faraway.util.StringUtils;

/**
 * Created by Alex on 02/06/2015.
 */
public class PlanetPage extends MainMenuPage {
    private static final Color COLOR_S3 = new Color(188, 28, 28);
    private static final Color COLOR_S2 = new Color(255, 137, 33);
    private static final Color COLOR_S1 = new Color(255, 238, 33);
    private static final Color COLOR_0 = new Color(120, 255, 255);
    private static final Color COLOR_1 = new Color(120, 255, 255);
    private static final Color COLOR_2 = new Color(120, 255, 255);

    private ViewFactory _viewFactory;

    public PlanetPage(MainMenu mainMenu, GDXRenderer renderer, MainMenu.Scene scene) {
        super(mainMenu, renderer, scene, "data/ui/menu/planet_list.yml");
    }

    @Override
    public void onCreate(ViewFactory viewFactory) {
        _viewFactory = viewFactory;
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, UIFrame panel) {
        UIFrame framePlanetList = (UIFrame) findById("frame_planet_list");
        framePlanetList.removeAllViews();

        int index = 0;
        for (PlanetInfo planet: GameData.getData().planets) {
            addPlanetListView(framePlanetList, planet, index++);
        }

        findById("bt_colonize").setOnClickListener(view -> {
            if (_mainMenu.getPlanet() != null) {
                _mainMenu.select(MainMenu.Scene.LAND_SITE);
            }
        });

        select(GameData.getData().planets.get(0));
    }

    private void addPlanetListView(UIFrame framePlanetList, PlanetInfo planet, int index) {
        _viewFactory.load("data/ui/menu/planet_list_entry.yml", view -> {
            view.findById("frame_background").setVisible(false);
            ((UILabel)view.findById("lb_planet")).setText(planet.name);
            ((UILabel)view.findById("lb_type")).setText(planet.type);

            if (planet.image != null) {
                ((UIImage) view.findById("img_planet")).setImage(planet.image.thumb);
            }

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

    private void select(PlanetInfo planet) {
        _mainMenu.select(planet);

        ((UILabel)findById("lb_detail_planet")).setText(planet.name);
        ((UILabel)findById("lb_detail_desc")).setText(planet.desc);

        formatPlanetStats((UILabel)findById("lb_detail_water"), "Water", planet.stats.water);
        formatPlanetStats((UILabel)findById("lb_detail_fertility"), "Fertility", planet.stats.fertility);
        formatPlanetStats((UILabel)findById("lb_detail_atmosphere"), "Atmosphere", planet.stats.atmosphere);
        formatPlanetStats((UILabel)findById("lb_detail_fauna"), "Fauna", planet.stats.fauna);
        formatPlanetStats((UILabel)findById("lb_detail_flora"), "Flora", planet.stats.flora);

        formatPlanetHostileStats((UILabel)findById("lb_detail_hostile_fauna"),      "Hostile fauna",        planet.stats.hostile_fauna);
        formatPlanetHostileStats((UILabel)findById("lb_detail_hostile_humankind"),  "Hostile humankind",    planet.stats.hostile_humankind);
        formatPlanetHostileStats((UILabel)findById("lb_detail_hostile_mechanic"),   "Hostile mechanic",     planet.stats.hostile_mechanic);
    }

    private void formatPlanetHostileStats(UILabel textView, String label, int value) {
        if (value == 0) {
            textView.setText(StringUtils.getDashedString(label, "no", 39));
        } else {
            textView.setText(StringUtils.getDashedString(label, "yes (" + (value == 1 ? "few" : "many") + ")", 39));
        }
        switch (value) {
            case 0: textView.setTextColor(COLOR_0); break;
            case 1: textView.setTextColor(COLOR_S2); break;
            case 2: textView.setTextColor(COLOR_S3); break;
        }
    }

    private void formatPlanetStats(UILabel textView, String label, int value) {
        textView.setText(StringUtils.getDashedString(
                label,
                StringUtils.getPlanetStatsText(value) + " (" + StringUtils.getPlanetStatsSymbol(value) + ")", 39));
        switch (value) {
            case -3: textView.setTextColor(COLOR_S3); break;
            case -2: textView.setTextColor(COLOR_S2); break;
            case -1: textView.setTextColor(COLOR_S1); break;
            case 0: textView.setTextColor(COLOR_0); break;
            case 1: textView.setTextColor(COLOR_1); break;
            case 2: textView.setTextColor(COLOR_2); break;
        }
    }
}
