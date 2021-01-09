package org.smallbox.faraway.client.manager;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.util.log.Log;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationObject
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

    private static final Collection<ShortcutStrategy> shortcutStrategies = new LinkedBlockingQueue<>();

    public void addBinding(String label, int key, Runnable runnable) {
        shortcutStrategies.removeIf(shortcutStrategy -> StringUtils.equals(shortcutStrategy.label, label));
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
