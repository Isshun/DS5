package org.smallbox.faraway.core.game.service.applicationConfig;

import com.google.gson.Gson;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.log.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@ApplicationObject
public class ApplicationConfigService {

    @OnInit
    public void onInit() {
        Log.info("Load application applicationConfig");

        File configFile = FileUtils.getUserDataFile("settings.json");
        if (configFile.exists()) {
            try (FileReader fileReader = new FileReader(configFile)) {
                DependencyInjector.getInstance().register(new Gson().fromJson(fileReader, ApplicationConfig.class));
            } catch (IOException e) {
                throw new GameException(ApplicationConfigService.class, e, "Unable to read config file");
            }
        }

    }

}
