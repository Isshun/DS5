package org.smallbox.faraway.client;

import org.smallbox.faraway.core.Application;

import java.util.function.Consumer;

public class ApplicationClient {
    public static void notify(Consumer<GameClientObserver> action) {
        Application.getObservers().forEach(observer -> {
            if (observer instanceof GameClientObserver) {
                action.accept((GameClientObserver) observer);
            }
        });
    }
}