package org.smallbox.faraway.module.dev;

import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Alex on 30/08/2015.
 */
public class LogModule extends ModuleBase {
    private String      _lastMessage;

    private static List<String> TAGS = Arrays.asList(
            "System",
//            "WeatherModule",
            "TemperatureModule",
//            "RoomModule",
            "CharacterModule"
//            "TestModule"
//            "JobModule",
    );

    @Override
    protected void onLoaded(Game game) {

    }

    @Override
    protected void onUpdate(int tick) {

    }

    @Override
    public void onLog(String tag, String message) {
        _lastMessage = message;

        if (TAGS.contains(tag)) {
            System.out.print("[");
            System.out.print(tag);
            System.out.print("] ");
            System.out.println(message);
        }
    }
}
