package org.smallbox.faraway.core.game.service.applicationConfig;

import com.google.gson.Gson;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.OnInit;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@ApplicationObject
public class ApplicationConfigService {

    private ApplicationConfig applicationConfig;

    @OnInit
    public void onInit() {
        Log.info("Load application applicationConfig");

        File configFile = FileUtils.getUserDataFile("settings.json");
        if (configFile.exists()) {
            try (FileReader fileReader = new FileReader(configFile)) {
                applicationConfig = new Gson().fromJson(fileReader, ApplicationConfig.class);
            } catch (IOException e) {
                throw new GameException(ApplicationConfigService.class, e, "Unable to read config file");
            }
        }

    }

    public ApplicationConfig getConfig() {
        return applicationConfig;
    }

    public ApplicationConfig.ApplicationConfigScreenInfo getScreenInfo() {
        return applicationConfig.screen;
    }

    public int getResolutionWidth() {
        return applicationConfig.screen.resolution[0];
    }

    public int getResolutionHeight() {
        return applicationConfig.screen.resolution[1];
    }

    public ApplicationConfig.ApplicationConfigGameInfo getGameInfo() {
        return applicationConfig.game;
    }

    public ApplicationConfig.ApplicationConfigDebug getDebugInfo() {
        return applicationConfig.debug;
    }

}
