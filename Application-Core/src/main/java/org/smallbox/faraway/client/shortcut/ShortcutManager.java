package org.smallbox.faraway.client.shortcut;

import com.badlogic.gdx.Input;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.util.log.Log;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationObject
public class ShortcutManager {
    private static final Collection<ShortcutStrategy> shortcutStrategies = new LinkedBlockingQueue<>();

    public void addBinding(String label, GameShortcut gameShortcut, Runnable runnable) {
        shortcutStrategies.removeIf(shortcutStrategy -> StringUtils.equals(shortcutStrategy.label, label));

        int key = getKey(gameShortcut);
        if (key != -1) {
            shortcutStrategies.add(new ShortcutStrategy(label, key, runnable));
        }
    }

    private int getKey(GameShortcut gameShortcut) {
        return DependencyManager.getInstance().getDependency(ApplicationConfig.class).shortcuts.stream()
                .filter(shortcutConfig -> StringUtils.equals(shortcutConfig.name, gameShortcut.value()))
                .mapToInt(shortcutConfig -> Input.Keys.valueOf(shortcutConfig.key))
                .findFirst()
                .orElse(-1);
    }

    public Collection<ShortcutStrategy> getBindings() {
        return shortcutStrategies;
    }

    public void action(int key) {
        shortcutStrategies.stream()
                .filter(strategy -> strategy.key == key)
                .forEach(strategy -> {
                    Log.debug("Press shortcut (key: %s, label: %s)", strategy.key, strategy.label);
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
