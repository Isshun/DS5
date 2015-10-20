package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.TeamModel;
import org.smallbox.faraway.core.game.model.planet.LandingSiteModel;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.ui.engine.LayoutFactory;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.panel.BasePanel;
import org.smallbox.faraway.core.util.Utils;

/**
 * Created by Alex on 02/06/2015.
 */
public class MainMenu {
    private final LayoutFactory _layoutFactory;
    private final ViewFactory   _viewFactory;
    private MainMenuPage _currentScene;
    private MainMenuPage[]     _scenes;
    private int                 _refresh;
    private long                _lastModified;
    private PlanetInfo          _planet;
    private LandingSiteModel    _landingSite;
    private TeamModel           _team;
    private boolean             _isOpen;

    public PlanetInfo getPlanet() {
        return _planet;
    }

    public LandingSiteModel getLandingSite() {
        return _landingSite;
    }

    public void select(LandingSiteModel landingSite) {
        _landingSite = landingSite;
    }

    public boolean isOpen() {
        return _isOpen;
    }

    public void open() {
        _isOpen = true;
        _currentScene.setVisible(true);
    }

    public void close() {
        _isOpen = false;
        _currentScene.setVisible(false);
    }

    public enum Scene {HOME, PLANETS, LAND_SITE, LOAD, TEAM}

    public MainMenu(LayoutFactory layoutFactory, ViewFactory viewFactory, GDXRenderer renderer) {
        _layoutFactory = layoutFactory;
        _viewFactory = viewFactory;
        _scenes = new MainMenuPage[] {
                new HomePage(this, renderer, Scene.HOME),
                new LoadPage(this, renderer, Scene.LOAD),
                new PlanetPage(this, renderer, Scene.PLANETS),
                new LandingSitePage(this, renderer, Scene.LAND_SITE),
                new TeamPage(this, renderer, Scene.TEAM),
        };
        _currentScene = _scenes[0];
        for (MainMenuPage scene: _scenes) {
            scene.init(viewFactory, layoutFactory, null, null);
        }
    }

    public void draw(GDXRenderer renderer, Viewport viewport) {
        renderer.clear();
        _currentScene.draw(renderer, 0, 0);
    }

    public void refresh(int frame) {
        _refresh = frame;
        for (BasePanel panel: _scenes) {
            panel.refresh(frame);
        }

        // Refresh UI if needed by GameData (strings)
        if (GameData.getData().needUIRefresh) {
            GameData.getData().needUIRefresh = false;
            reload();
        }

        // Refresh UI if needed by UI files
        if (frame % 8 == 0) {
            long lastResModified = Utils.getLastUIModified();
            if (lastResModified > _lastModified) {
                _lastModified = lastResModified;
                reload();
            }
        }
    }

    private void reload() {
        for (MainMenuPage scene: _scenes) {
            scene.removeAllViews();
            scene.init(_viewFactory, _layoutFactory, null, null);
            scene.refresh(0);
        }
    }

    public void select(Scene sceneType) {
        _currentScene.setVisible(false);
        for (MainMenuPage scene: _scenes) {
            if (scene.getSceneType() == sceneType) {
                _currentScene = scene;
            }
        }
        _currentScene.setVisible(true);
        _currentScene.open();
    }

    public void onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        for (MainMenuPage scene: _scenes) {
            if (scene.onMouseEvent(action, button, x, y)) {
                return;
            }
        }

        if (action == GameEventListener.Action.PRESSED || action == GameEventListener.Action.RELEASED) {
            switch (button) {
                case LEFT:
                    if (action == GameEventListener.Action.RELEASED) {
                        // Is consume by EventManager
                        if (UIEventManager.getInstance().click(x, y)) {
                            // Nothing to do !
                        }
                    }
                    break;

                case MIDDLE:
                    break;

                case RIGHT:
                    if (action == GameEventListener.Action.RELEASED) {
                        // Is consume by EventManager
                        if (UIEventManager.getInstance().rightClick(x, y)) {
                            // Nothing to do !
                        }
                    }
                    break;
            }
        }
    }

    public void select(PlanetInfo planet) {
        _planet = planet;
    }

}
