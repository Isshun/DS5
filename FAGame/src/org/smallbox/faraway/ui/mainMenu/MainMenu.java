package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.GameTimer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.LandingSiteModel;
import org.smallbox.faraway.game.model.planet.PlanetInfo;
import org.smallbox.faraway.game.model.TeamModel;
import org.smallbox.faraway.ui.panel.BasePanel;
import org.smallbox.faraway.ui.panel.LayoutFactory;

/**
 * Created by Alex on 02/06/2015.
 */
public class MainMenu {
    private final LayoutFactory _layoutFactory;
    private final ViewFactory   _viewFactory;
    private MainMenuScene       _currentScene;
    private MainMenuScene[]     _scenes;
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
    }

    public void close() {
        _isOpen = false;
    }

    public enum Scene {HOME, PLANETS, LAND_SITE, LOAD, TEAM}

    public MainMenu(LayoutFactory layoutFactory, ViewFactory viewFactory, GFXRenderer renderer) {
        _layoutFactory = layoutFactory;
        _viewFactory = viewFactory;
        _scenes = new MainMenuScene[] {
                new HomeScene(this, renderer, Scene.HOME),
                new LoadScene(this, renderer, Scene.LOAD),
                new PlanetScene(this, renderer, Scene.PLANETS),
                new TeamScene(this, renderer, Scene.TEAM),
                new LandingSiteScene(this, renderer, Scene.LAND_SITE)
        };
        _currentScene = _scenes[0];
        for (MainMenuScene scene: _scenes) {
            scene.init(viewFactory, layoutFactory, null, null, null);
        }
    }

    public void draw(GFXRenderer renderer, RenderEffect effect) {
        renderer.clear();
        _currentScene.draw(renderer, effect);
    }

    public void refresh(int update) {
        _refresh = update;
        for (BasePanel panel: _scenes) {
            panel.refresh(update);
        }

        // Refresh UI if needed by GameData (strings)
        if (GameData.getData().needUIRefresh) {
            GameData.getData().needUIRefresh = false;
            reload();
        }

        // Refresh UI if needed by UI files
        long lastResModified = Utils.getLastUIModified();
        if (update % 8 == 0 && lastResModified > _lastModified) {
            _lastModified = lastResModified;
            reload();
        }
    }

    private void reload() {
        for (MainMenuScene scene: _scenes) {
            scene.removeAllViews();
            scene.init(_viewFactory, _layoutFactory, null, null, null);
            scene.refresh(0);
        }
    }

    public void select(Scene sceneType) {
        _currentScene.setVisible(false);
        for (MainMenuScene scene: _scenes) {
            if (scene.getSceneType() == sceneType) {
                _currentScene = scene;
            }
        }
        _currentScene.setVisible(true);
        _currentScene.open();
    }

    public void onMouseEvent(GameTimer timer, GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        for (MainMenuScene scene: _scenes) {
            if (scene.onMouseEvent(timer, action, button, x, y)) {
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
