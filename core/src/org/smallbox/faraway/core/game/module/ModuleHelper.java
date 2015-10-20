package org.smallbox.faraway.core.game.module;

import org.smallbox.faraway.core.game.module.base.CharacterModule;
import org.smallbox.faraway.core.game.module.base.JobModule;
import org.smallbox.faraway.core.game.module.base.WorldModule;

/**
 * Created by Alex on 01/09/2015.
 */
public class ModuleHelper {
    private static CharacterModule  _characterModule;
    private static WorldModule      _worldModule;
    private static JobModule        _jobModule;
    private static ResourcesModule  _resourcesModules;

    public static CharacterModule   getCharacterModule() { return _characterModule; }
    public static WorldModule       getWorldModule() { return _worldModule; }
    public static JobModule         getJobModule() { return _jobModule; }
    public static ResourcesModule   getResourcesModule() { return _resourcesModules; }

    public static void setCharacterModule(CharacterModule characterModule) { _characterModule = characterModule; }
    public static void setWorldModule(WorldModule worldModule) { _worldModule = worldModule; }
    public static void setJobModule(JobModule jobModule) { _jobModule = jobModule; }
}
