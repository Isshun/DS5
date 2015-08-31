package org.smallbox.faraway.game.module.dev;

import org.smallbox.faraway.game.module.GameModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Alex on 30/08/2015.
 */
public class LogModule extends GameModule {
    private static List<String> TAGS = Arrays.asList(
//            "System",
//            "WeatherModule",
//            "TemperatureModule",
//            "RoomModule",
            "CharacterModule"
//            "JobModule",
    );

    @Override
    protected void onUpdate(int tick) {

    }

    @Override
    public void onLog(String tag, String message) {
        if (TAGS.contains(tag)) {
            System.out.print("[");
            System.out.print(tag);
            System.out.print("] ");
            System.out.println(message);
        }
    }
}
