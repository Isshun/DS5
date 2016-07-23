package org.smallbox.faraway.core.game;

import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.LuaControllerManager;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.renderer.*;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.planet.PlanetModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.GameActionExtra;
import org.smallbox.faraway.ui.GameSelectionExtra;
import org.smallbox.faraway.ui.UICursor;
import org.smallbox.faraway.ui.UserInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    public enum GameModuleState {UNINITIALIZED, CREATED, STARTED}
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
    private PlanetModel                     _planet;
    private int                             _hour = 5;
    private int                             _day;
    private int                             _year;
    private static int                      _tick;
    private Map<String, Boolean>            _displays;
    private int                             _speed = 1;
    private int                             _lastSpeed = 1;
    private List<GameModule>                _modules;
    private List<BaseRenderer>              _renders;
    private BaseRenderer                    _miniMapRenderer;
    private GameModuleState _state = GameModuleState.UNINITIALIZED;

    public GameModuleState  getState() { return _state; }

    public void setDisplay(String displayName, boolean isActive) {
        _displays.put(displayName, isActive);
        Application.getInstance().notify(observer -> observer.onDisplayChange(displayName, _displays.get(displayName)));
    }

    public void toggleDisplay(String displayName) {
        _displays.put(displayName, !_displays.containsKey(displayName) || !_displays.get(displayName));
        Application.getInstance().notify(observer -> observer.onDisplayChange(displayName, _displays.get(displayName)));
    }

    public boolean                          isRunning() { return _isRunning; }
    public boolean                          hasDisplay(String displayName) { return _displays.containsKey(displayName) && _displays.get(displayName); }
    public void                             toggleRunning() { setRunning(!_isRunning); }
    public void                             toggleSpeed0() { setSpeed(_speed == 0 ? _lastSpeed : 0); }

    public static Game                      getInstance() { return _self; }
    public int                              getHour() { return _hour; }
    public int                              getDay() { return _day; }
    public int                              getYear() { return _year; }
    public Viewport                         getViewport() { return _viewport; }
    public static int                       getUpdate() { return _tick; }
    public PlanetModel                      getPlanet() { return _planet; }
    public long                             getTick() { return _tick; }
    public GameActionExtra                  getInteraction() { return _gameAction; }
    public GameSelectionExtra               getSelector() { return _selector; }
    public int                              getSpeed() { return _speed; }
    public int                              getLastSpeed() { return _lastSpeed; }
    public Collection<GameModule>           getModules() { return _modules; }
    public Collection<BaseRenderer>         getRenders() { return _renders; }

    public Game(GameInfo info) {
        _self = this;
        _viewport = new Viewport(400, 300);
        _selector = new GameSelectionExtra();
        _gameAction = new GameActionExtra(_viewport, _selector);
        _info = info;
        _isRunning = true;
        _planet = new PlanetModel(info.planet);
        _directions = Application.getInstance().getInputProcessor().getDirection();
        _displays = new HashMap<>();
        _tick = 0;

        GDXRenderer.getInstance().setViewport(_viewport);
    }

    /**
     * Call game modules onGameCreate method and construct renders
     * createGame() is call before game load or createGame
     * createGame() is call before startGame()
     */
    public void createModules() {
        Log.info("============ CREATE GAME ============");

        _renders = new ArrayList<>();

        // Create mini-map render
        _miniMapRenderer = new MinimapRenderer();
        Application.getInstance().addObserver(_miniMapRenderer);

        // Call onGameCreate method to each modules
        _modules = ModuleManager.getInstance().getGameModules().stream().filter(ModuleBase::isLoaded).collect(Collectors.toList());
        _modules.sort((o1, o2) -> o2.getModulePriority() - o1.getModulePriority());
        _modules.forEach(module -> module.createGame(this));

        // Sort renders by level and add them to observers
        _renders.sort((r1, r2) -> r1.getLevel() - r2.getLevel());
        _renders.forEach(renderer -> Application.getInstance().addObserver(renderer));

        _state = GameModuleState.CREATED;
    }

    public void start() {
        startModules();

        // Notify controller
        LuaControllerManager.getInstance().gameStart(this);

        MainRenderer.getInstance().init(this, _renders, _miniMapRenderer);

        Application.getInstance().notify(observer -> observer.onHourChange(_hour));
        Application.getInstance().notify(observer -> observer.onDayChange(_day));
        Application.getInstance().notify(observer -> observer.onYearChange(_year));
        Application.getInstance().notify(observer -> observer.onFloorChange(WorldHelper.getCurrentFloor()));
    }

    private void startModules() {
        _modules.stream().filter(ModuleBase::isLoaded).forEach(module -> module.startGame(this));
        _renders.stream().filter(BaseRenderer::isLoaded).forEach(renderer -> renderer.startGame(this));
        _miniMapRenderer.startGame(this);

        _state = GameModuleState.STARTED;
    }

    public void                     clearCursor() { _gameAction.setCursor(null); }
//    public void                     clearSelection() { _selector.clear(); }
    public void                     setCursor(UICursor cursor) { _gameAction.setCursor(cursor); }
    public void                     setCursor(String cursorName) { _gameAction.setCursor(Data.getData().getCursor(cursorName)); }

    public void update() {
        // Update
        if (_nextUpdate < System.currentTimeMillis() && _isRunning) {
            _nextUpdate = System.currentTimeMillis() + _tickInterval;
            _tick += 1;
            MainRenderer.getInstance().onUpdate();
            LuaControllerManager.getInstance().gameUpdate(this);
            onUpdate(_tick);
        }
    }

    protected void onUpdate(int tick) {
        if (!_isRunning) {
            return;
        }

        ModuleManager.getInstance().getGameModules().stream().filter(ModuleBase::isLoaded).forEach(module -> module.updateGame(this, tick));

        if (tick % Application.getInstance().getConfig().game.tickPerHour == 0) {
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

//        _gameAction.draw(renderer);
        _frame++;
    }

    public void setSpeed(int speed) {
        if (speed != 0) {
            _lastSpeed = speed;
        }
        _speed = speed;
        _tickInterval = TICK_INTERVALS[Math.max(0, Math.min(4, speed))];
        _isRunning = _tickInterval > 0;
        Application.getInstance().notify(observer -> observer.onSpeedChange(speed));
    }
}
