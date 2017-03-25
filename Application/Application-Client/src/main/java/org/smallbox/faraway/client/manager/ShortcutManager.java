package org.smallbox.faraway.client.manager;

import org.smallbox.faraway.util.Log;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 23/02/2017.
 */
public class ShortcutManager {

    public static final class ShortcutStrategy {
        public final String label;
        public final int key;
        final Runnable runnable;

        ShortcutStrategy(String label, int key, Runnable runnable) {
            this.label = label;
            this.key = key;
            this.runnable = runnable;
        }
    }

    private static Collection<ShortcutStrategy> shortcutStrategies = new LinkedBlockingQueue<>();

    public void addBinding(String label, int key, Runnable runnable) {
        shortcutStrategies.add(new ShortcutStrategy(label, key, runnable));
    }

    public Collection<ShortcutStrategy> getBindings() {
        return shortcutStrategies;
    }

    public void action(int key) {
        shortcutStrategies.stream()
                .filter(strategy -> strategy.key == key)
                .forEach(strategy -> {
                    Log.info("Press shortcut (key: %s, label: %s)", strategy.key, strategy.label);
                    strategy.runnable.run();
                });

    }

    //            dependencyInjector.setClientInterface((id, key, runnable) -> {
//
////            // Le raccourci existe avec un identifiant différent
////            if (shortcutStrategies.stream().anyMatch(strategy -> strategy.key == key && !strategy.id.equals(id))) {
////                throw new GameException("Add already existing shortcut");
////            }
////
////            // Le raccourci existe avec le même identifiant
////            if (shortcutStrategies.stream().anyMatch(strategy -> strategy.key == key)) {
////                Log.warning(ApplicationClient.class, "Add already existing shortcut");
////            }
//    });

}
