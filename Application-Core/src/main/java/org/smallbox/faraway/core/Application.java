package org.smallbox.faraway.core;

import com.badlogic.gdx.Gdx;

public class Application {

    public static boolean isLoaded = false;

    public static void addTask(Runnable runnable) {
        Gdx.app.postRunnable(runnable);
    }

    public static void setRunning(boolean isRunning) {
        if (!isRunning && Gdx.app != null) {
            Gdx.app.exit();
        }
    }

}