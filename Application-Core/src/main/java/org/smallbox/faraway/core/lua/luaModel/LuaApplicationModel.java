package org.smallbox.faraway.core.lua.luaModel;

import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.lua.LuaModule;
import org.smallbox.faraway.core.lua.ServerLuaModuleManager;
import org.smallbox.faraway.core.module.ModuleBase;

import java.util.Collection;

@ApplicationObject
public class LuaApplicationModel {
    @Inject private ServerLuaModuleManager serverLuaModuleManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private GameManager gameManager;
    @Inject private GameTime gameTime;
    @Inject public Game game;

    public int screen_width;
    public int screen_height;
    public Collection<LuaModule> luaModules;
    public Collection<ModuleBase> modules;
    public Collection<ModuleBase> moduleThirds;

    @OnInit
    public void init() {
        luaModules = serverLuaModuleManager.getModules();
        screen_width = applicationConfig.getResolutionWidth();
        screen_height = applicationConfig.getResolutionHeight();
    }

}
