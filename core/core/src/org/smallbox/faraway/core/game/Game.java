package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.data.factory.world.WorldFactory;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.LightRenderer;
import org.smallbox.faraway.core.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.planet.PlanetModel;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.model.planet.RegionModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class Game extends BaseGame {
    private static Game                     _self;
    private final String                    _fileName;
    private final LuaModuleManager          _luaModuleManager;
    private GameConfig                      _config;
    private final Collection<GameModule>    _modulesBase;
    private final List<GameModule>          _modulesThird;
    private List<GameObserver>              _observers = new ArrayList<>();
    private GameSerializer.GameSave         _save;
    private PlanetModel                     _planet;
    private RegionModel                     _region;
    private int                             _hour = 5;
    private int                             _day;
    private int                             _year;
    private GameInfo                        _info = new GameInfo();
    private boolean[]                       _directions = new boolean[4];

    private static int                      _tick;
    private Viewport                         _viewport;
    private List<GameModule.EventListener>  _eventListeners = new ArrayList<>();
    private String                          _display;

    public void                             toggleRunning() { _isRunning = !_isRunning; }
    public void                             addObserver(GameObserver observer) { _observers.add(observer); }
    public void                             setRunning(boolean running) { _isRunning = running; }
    public void                             setDisplay(String display) { _display = display; }

    public boolean                          isRunning() { return _isRunning; }
    public Collection<GameModule>           getModules() { return _modulesBase; }
    public static Game                      getInstance() { return _self; }
    public int                              getHour() { return _hour; }
    public int                              getDay() { return _day; }
    public int                              getYear() { return _year; }
    public Viewport                         getViewport() { return _viewport; }
    public static int                       getUpdate() { return _tick; }
    public PlanetModel                      getPlanet() { return _planet; }
    public String                           getFileName() { return _fileName; }
    public long                             getTick() { return _tick; }
    public RegionModel                      getRegion() { return _region; }
    public String                           getDisplay() { return _display; }

    public Game(int width, int height, Data data, GameConfig config, String fileName, ParticleRenderer particleRenderer, LightRenderer lightRenderer, RegionInfo regionInfo) {
        Log.debug("Game");

        _self = this;
        _config = config;
        _fileName = fileName;
        _isRunning = true;
        _viewport = new Viewport(400, 300);
        _modulesBase = ModuleManager.getInstance().getModulesBase();
        _modulesThird = ModuleManager.getInstance().getModulesThird();

        _luaModuleManager = new LuaModuleManager();
        _observers.add(_luaModuleManager);

        GDXRenderer.getInstance().setViewport(_viewport);
        _tick = 0;

        Log.info("Game: onCreate");

        setRegion(regionInfo);

        Log.info("Game:\tdone");
    }

    public void setRegion(RegionInfo regionInfo) {
        if (regionInfo != null) {
            _planet = new PlanetModel(regionInfo.planet);
            _region = new RegionModel(_planet, regionInfo);
        }
    }

    public void init(WorldFactory factory) {
        _modulesBase.stream().filter(GameModule::isLoaded).filter(module -> module.getModulePriority() > 0).forEach(GameModule::create);
        _observers.addAll(ModuleManager.getInstance().getModules());
        _observers.addAll(ModuleManager.getInstance().getRenders());
        _luaModuleManager.init();
        _modulesBase.stream().filter(GameModule::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(GameModule::create);

        System.out.println("Load third party modules");
        _modulesThird.stream().filter(GameModule::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(GameModule::create);

        notify(GameObserver::onReloadUI);
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

        _luaModuleManager.update();

        _modulesBase.stream().filter(GameModule::isLoaded).forEach(module -> module.update(tick));
        _modulesThird.stream().filter(GameModule::isLoaded).forEach(module -> module.update(tick));

        if (tick % _config.tickPerHour == 0) {
            if (++_hour >= _planet.getInfo().dayDuration) {
                _hour = 0;
                if (++_day >= _planet.getInfo().yearDuration) {
                    _day = 1;
                    _year++;
                    notify(observer -> observer.onYearChange(_year));
                }
                notify(observer -> observer.onDayChange(_day));
            }
            notify(observer -> observer.onHourChange(_hour));
        }

        _tick = tick;
    }

    @Override
    protected void onRender(int frame) {
        if (_directions[0]) { _viewport.move(20, 0); }
        if (_directions[1]) { _viewport.move(0, 20); }
        if (_directions[2]) { _viewport.move(-20, 0); }
        if (_directions[3]) { _viewport.move(0, -20); }
    }

    public void    load() {
        String filePath = "data/saves/" + _fileName;

        long time = System.currentTimeMillis();

//        loadListener.onLoad("Load game");
        GameSerializer.load(filePath, _save);
        _save = null;
        System.gc();

        Log.info("Game loaded (2): " + (System.currentTimeMillis() - time) + "ms");

//        loadListener.onLoad("Init world old");
//        WorldFactory.cleanRock();
    }

    public void    save(final String fileName) {
        GameSerializer.save("data/saves/" + fileName, _modulesBase, _modulesThird);
    }

    public void notify(Consumer<GameObserver> action) {
        _observers.stream().forEach(action::accept);
    }

    public void preload() {
        // TODO magic
    }

    public void setInputDirection(boolean[] directions) {
        _directions = directions;
    }

    public GameInfo getInfo() { return _info; }

    public void removeObserver(GameModule observer) {
        _observers.remove(observer);
    }

    public void addEventListener(GameModule.EventListener listener) {
        _eventListeners.add(listener);
    }

    public void removeEventListener(GameModule.EventListener listener) {
        _eventListeners.remove(listener);
    }

    public void notify(String tag, Object data) {
        _eventListeners.forEach(listener -> {
//            listener.
//            listener.onEvent(data);
        });
//        _observers.stream().forEach(action::accept);
    }

    public LuaModuleManager getLuaModuleManager() {
        return _luaModuleManager;
    }

    public void setPaused(boolean pause) {
        _paused = pause;
        notify(pause ? GameObserver::onGamePaused : GameObserver::onGameResume);
    }
}
