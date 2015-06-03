package org.smallbox.faraway.ui.mainMenu;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.GameTimer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.UIEventManager;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.manager.Utils;
import org.smallbox.faraway.model.LandingSiteModel;
import org.smallbox.faraway.model.PlanetModel;
import org.smallbox.faraway.model.TeamModel;
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
    private PlanetModel         _planet;
    private LandingSiteModel    _landingSite;
    private TeamModel           _team;

    public PlanetModel getPlanet() {
        return _planet;
    }

    public LandingSiteModel getLandingSite() {
        return _landingSite;
    }

    public void select(LandingSiteModel landingSite) {
        _landingSite = landingSite;
    }

    public enum Scene {HOME, PLANETS, LAND_SITE, TEAM}

    public MainMenu(LayoutFactory layoutFactory, ViewFactory viewFactory, GFXRenderer renderer) {
        _layoutFactory = layoutFactory;
        _viewFactory = viewFactory;
        _scenes = new MainMenuScene[] {
                new HomeScene(this, renderer, Scene.HOME),
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

        long lastResModified = Utils.getLastUIModified();
        if (update % 8 == 0 && lastResModified > _lastModified) {
            SpriteManager.getInstance().loadStrings();
            _lastModified = lastResModified;
            reload();
        }
    }

    private void reload() {
        for (MainMenuScene scene: _scenes) {
            scene.clearAllViews();
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

    public void select(PlanetModel planet) {
        _planet = planet;
    }

}
