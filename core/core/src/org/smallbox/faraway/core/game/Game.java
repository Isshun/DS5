package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.planet.PlanetModel;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.ui.GameActionExtra;
import org.smallbox.faraway.ui.GameSelectionExtra;
import org.smallbox.faraway.ui.UICursor;
import org.smallbox.faraway.ui.UserInterface;

import java.util.Collection;

public class Game {
    public static final int[]               TICK_INTERVALS = {-1, 320, 200, 75, 10};

    // Update
    private long                            _nextUpdate;
    private int                             _tickInterval = TICK_INTERVALS[1];

    // Render
    private int                             _frame;
    private double                          _animationProgress;
    private boolean[]                       _directions = new boolean[4];
    private Viewport                        _viewport;

    private boolean                         _isRunning;
    private GameActionExtra                 _gameAction;
    private GameSelectionExtra              _selector;
    private static Game                     _self;
    private final GameInfo                  _info;
    private GameConfig                      _config;
    private final Collection<GameModule>    _modulesBase;
    private final Collection<GameModule>    _modulesThird;
    private PlanetModel                     _planet;
    private int                             _hour = 5;
    private int                             _day;
    private int                             _year;
    private static int                      _tick;
    private String                          _display;

    public void                             setDisplay(String display) { _display = display; }
    public boolean                          isRunning() { return _isRunning; }
    public static Game                      getInstance() { return _self; }
    public int                              getHour() { return _hour; }
    public int                              getDay() { return _day; }
    public int                              getYear() { return _year; }
    public Viewport                         getViewport() { return _viewport; }
    public static int                       getUpdate() { return _tick; }
    public PlanetModel                      getPlanet() { return _planet; }
    public long                             getTick() { return _tick; }
    public String                           getDisplay() { return _display; }
    public GameActionExtra                  getInteraction() { return _gameAction; }
    public GameSelectionExtra               getSelector() { return _selector; }

    public Game(GameInfo info, GameConfig config) {
        _self = this;
        _viewport = new Viewport(400, 300);
        _selector = new GameSelectionExtra();
        _gameAction = new GameActionExtra(_viewport, _selector);
        _info = info;
        _config = config;
        _isRunning = true;
        _modulesBase = ModuleManager.getInstance().getModulesBase();
        _modulesThird = ModuleManager.getInstance().getModulesThird();
        _planet = new PlanetModel(info.planet);
        _directions = Application.getInstance().getInputProcessor().getDirection();
        _tick = 0;

        GDXRenderer.getInstance().setViewport(_viewport);
    }

    public void init() {
        MainRenderer.getInstance().init(Data.config, this);
        Application.getInstance().notify(GameObserver::onGameStart);
        Application.getInstance().notify(observer -> observer.onHourChange(_hour));
        Application.getInstance().notify(observer -> observer.onDayChange(_day));
        Application.getInstance().notify(observer -> observer.onYearChange(_year));
        Application.getInstance().notify(observer -> observer.onFloorChange(WorldHelper.getCurrentFloor()));
    }

    public void                     clearCursor() { _gameAction.setCursor(null); }
    public void                     clearSelection() { _selector.clear(); }
    public void                     setCursor(UICursor cursor) { _gameAction.setCursor(cursor); }
    public void                     setCursor(String cursorName) { _gameAction.setCursor(Data.getData().getCursor(cursorName)); }

    public void update() {
        // Update
        if (_nextUpdate < System.currentTimeMillis() && _isRunning) {
            _nextUpdate = System.currentTimeMillis() + _tickInterval;
            _tick += 1;
            MainRenderer.getInstance().onUpdate();
            onUpdate(_tick);
        }
    }

    protected void onUpdate(int tick) {
        if (!_isRunning) {
            return;
        }

        LuaModuleManager.getInstance().update();

        _modulesBase.stream().filter(GameModule::isLoaded).forEach(module -> module.update(tick));
        _modulesThird.stream().filter(GameModule::isLoaded).forEach(module -> module.update(tick));

        if (tick % _config.tickPerHour == 0) {
            if (++_hour >= _planet.getInfo().dayDuration) {
                _hour = 0;
                if (++_day >= _planet.getInfo().yearDuration) {
                    _day = 1;
                    _year++;
                    Application.getInstance().notify(observer -> observer.onYearChange(_year));
                }
                Application.getInstance().notify(observer -> observer.onDayChange(_day));
            }
            Application.getInstance().notify(observer -> observer.onHourChange(_hour));
        }

        _tick = tick;
    }

    public GameInfo getInfo() { return _info; }

    public void setRunning(boolean isRunning) {
        _isRunning = isRunning;
        Application.getInstance().notify(isRunning ? GameObserver::onGameResume : GameObserver::onGamePaused);
    }

    public void render(GDXRenderer renderer, Viewport viewport) {
        // Draw
        if (!GameManager.getInstance().isRunning()) {
            _animationProgress = 1 - ((double) (_nextUpdate - System.currentTimeMillis()) / _tickInterval);
        }

        MainRenderer.getInstance().onDraw(renderer, viewport, _animationProgress);

        if (_isRunning) {
            if (_directions[0]) { _viewport.move(20, 0); }
            if (_directions[1]) { _viewport.move(0, 20); }
            if (_directions[2]) { _viewport.move(-20, 0); }
            if (_directions[3]) { _viewport.move(0, -20); }
        }

        MainRenderer.getInstance().onRefresh(_frame);

        // TODO
        try {
            UserInterface.getInstance().onRefresh(_frame);
        } catch (Exception e) {
            e.printStackTrace();
        }

        _gameAction.draw(renderer);
        _frame++;
    }

    public void setSpeed(int speed) {
        _tickInterval = TICK_INTERVALS[Math.max(0, Math.min(4, speed))];
        _isRunning = _tickInterval > 0;
        Application.getInstance().notify(observer -> observer.onSpeedChange(speed));
    }
}
