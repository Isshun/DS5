package org.smallbox.faraway.module.dev;

import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.game.Game;

import java.util.Arrays;
import java.util.List;

public class LogModule extends GameModule {
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
    protected void onGameUpdate(Game game, int tick) {

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
