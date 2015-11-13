package org.smallbox.faraway.core.game;

import com.almworks.sqlite4java.SQLiteConnection;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.engine.renderer.ExteriorRenderer;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.planet.PlanetModel;
import org.smallbox.faraway.core.game.module.world.DBRunnable;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.GameActionExtra;
import org.smallbox.faraway.ui.GameSelectionExtra;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.UIImage;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class Game extends BaseGame {
    private static Game                     _self;
    private final GameInfo                  _info;
    private GameConfig                      _config;
    private final Collection<GameModule>    _modulesBase;
    private final List<GameModule>          _modulesThird;
    private GameSerializer.GameSave         _save;
    private PlanetModel                     _planet;
//    private RegionModel                     _region;
    private int                             _hour = 5;
    private int                             _day;
    private int                             _year;

    private static int                      _tick;
    private String                          _display;

    public void                             toggleRunning() { _isRunning = !_isRunning; }
    public void                             setRunning(boolean running) { _isRunning = running; }
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
        Log.debug("Game");

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

        GDXRenderer.getInstance().setViewport(_viewport);
        _tick = 0;

        Log.info("Game: onCreate");
        Log.info("Game:\tdone");
    }

    public void init() {
        System.out.println("Load base modules");
        _modulesBase.stream().filter(GameModule::isLoaded).filter(module -> module.getModulePriority() > 0).forEach(module -> module.load(this));
        _modulesBase.stream().filter(GameModule::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(module -> module.load(this));

        System.out.println("Load third party modules");
        _modulesThird.stream().filter(GameModule::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(module -> module.load(this));

        Application.getInstance().notify(GameObserver::onReloadUI);
        Application.getInstance().notify(observer -> observer.onFloorChange(WorldHelper.getCurrentFloor()));
    }

    @Override
    public void onUpdateDo() {
        _modulesBase.stream().filter(GameModule::isLoaded).forEach(GameModule::onUpdateDo);
    }

    @Override
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

    public void    load(GameInfo gameInfo, GameInfo.GameSaveInfo saveInfo, GameSerializer.GameSerializerInterface listener) {
        long time = System.currentTimeMillis();

        GameSerializer.load(gameInfo, new File("data/saves", gameInfo.name), saveInfo.filename, listener);
        _save = null;

        Log.info("Game loaded (2): " + (System.currentTimeMillis() - time) + "ms");
    }

    public GameInfo getInfo() { return _info; }

    public void setPaused(boolean pause) {
        _paused = pause;
        Application.getInstance().notify(pause ? GameObserver::onGamePaused : GameObserver::onGameResume);
    }
}
