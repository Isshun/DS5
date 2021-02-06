package org.smallbox.faraway.core.config;

import com.google.gson.Gson;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnSettingsUpdate;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@ApplicationObject
public class ApplicationConfigService {
    @Inject private DependencyManager dependencyManager;

    private ApplicationConfig applicationConfig;

    @OnInit
    public void onInit() {
        Log.info("Load application applicationConfig");

        FileUtils.createRoamingDirectory();

        applicationConfig = dependencyManager.getDependency(ApplicationConfig.class);

        if (applicationConfig == null) {
            File configFile = FileUtils.getUserDataFile("settings.json");
            if (configFile.exists()) {
                try (FileReader fileReader = new FileReader(configFile)) {
                    dependencyManager.register(applicationConfig = new Gson().fromJson(fileReader, ApplicationConfig.class));
                } catch (IOException e) {
                    throw new GameException(ApplicationConfigService.class, e, "Unable to read config file");
                }
            }
        }

        initScreenResolution();
    }

    @OnSettingsUpdate
    private void onSettingsUpdate() {
        File configFile = FileUtils.getUserDataFile("settings.json");
        try (FileWriter fileWriter = new FileWriter(configFile)) {
            fileWriter.write(new Gson().toJson(applicationConfig));
        } catch (IOException e) {
            throw new GameException(ApplicationConfigService.class, e, "Unable to write config file");
        }
    }

    private void initScreenResolution() {
        if (applicationConfig.screen.resolution == null) {

            // Get native screen resolution
            java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int width = gd.getDisplayMode().getWidth();
            int height = gd.getDisplayMode().getHeight();
            double ratio = (double)width / height;
            Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

            applicationConfig.screen.resolution = new int[2];
            applicationConfig.screen.resolution[0] = width - 60;
            applicationConfig.screen.resolution[1] = height - 140;
        }

    }

}
