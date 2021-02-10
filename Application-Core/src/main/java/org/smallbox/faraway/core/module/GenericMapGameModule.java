package org.smallbox.faraway.core.module;

import org.smallbox.faraway.game.world.ObjectModel;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class GenericMapGameModule<T_KEY, T_MODEL extends ObjectModel> extends AbsGameModule {
    protected Map<T_KEY, T_MODEL> modelMap = new ConcurrentHashMap<>();

    public int getCount() {
        return modelMap.size();
    }

    public Collection<T_MODEL> getAll() {
        return modelMap.values();
    }

    public T_MODEL get(T_KEY key) {
        return modelMap.get(key);
    }

    public void add(T_KEY key, T_MODEL model) {
        modelMap.put(key, model);
    }

    public <T> void replaceAll(Collection<T> objects, Function<T, T_KEY> funcKey, Function<T, T_MODEL> funcValue) {
        modelMap.clear();
        objects.forEach(object -> modelMap.put(funcKey.apply(object), funcValue.apply(object)));
    }

}