package org.smallbox.faraway.core.engine.module.java;

import org.smallbox.faraway.core.game.module.character.CharacterModule;
import org.smallbox.faraway.core.game.module.job.JobModule;
import org.smallbox.faraway.core.game.module.world.WeatherModule;
import org.smallbox.faraway.core.game.module.world.WorldModule;

/**
 * Created by Alex on 01/09/2015.
 */
public class ModuleHelper {
    private static CharacterModule  _characterModule;
    private static WorldModule      _worldModule;
    private static JobModule        _jobModule;
    private static WeatherModule    _weatherModule;

    public static CharacterModule   getCharacterModule() { return _characterModule; }
    public static WorldModule       getWorldModule() { return _worldModule; }
    public static JobModule         getJobModule() { return _jobModule; }
    public static WeatherModule     getWeatherModule() { return _weatherModule; }

    public static void setCharacterModule(CharacterModule characterModule) { _characterModule = characterModule; }
    public static void setWorldModule(WorldModule worldModule) { _worldModule = worldModule; }
    public static void setJobModule(JobModule jobModule) { _jobModule = jobModule; }
    public static void getWeatherModule(WeatherModule weatherModule) { _weatherModule = weatherModule; }
}
