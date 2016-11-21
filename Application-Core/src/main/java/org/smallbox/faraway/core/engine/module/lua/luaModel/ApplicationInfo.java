package org.smallbox.faraway.core.engine.module.lua.luaModel;

import org.smallbox.faraway.core.Application;

/**
 * Created by Alex on 06/11/2015.
 */
public class ApplicationInfo {
    public int  screen_width = Application.configurationManager.screen.resolution[0];
    public int  screen_height = Application.configurationManager.screen.resolution[1];
}
