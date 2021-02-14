package org.smallbox.faraway.core.module;

import org.smallbox.faraway.game.world.ObjectModel;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class GenericGameModule<T_MODEL extends ObjectModel> extends ModuleBase {
    protected Collection<T_MODEL> modelList = new LinkedBlockingQueue<>();

    public int getCount() {
        return modelList.size();
    }

    public Collection<T_MODEL> getAll() {
        return modelList;
    }

    public T_MODEL get(int id) {
        for (T_MODEL model: modelList) {
            if (model.getId() == id) {
                return model;
            }
        }
        return null;
    }

    public void add(T_MODEL model) {
        modelList.add(model);
    }

    public void remove(T_MODEL model) {
        modelList.remove(model);
    }

    public void removeIf(Predicate<T_MODEL> predicate) {
        modelList.removeIf(predicate);
    }

    public boolean contains(T_MODEL model) {
        return modelList.contains(model);
    }

    public T_MODEL getRandom() {
        if (modelList.size() == 0) {
            return null;
        }
        if (modelList.size() == 1) {
            return modelList.stream().findFirst().orElse(null);
        }
        return modelList.stream().skip(new Random().nextInt(modelList.size() - 1)).findFirst().orElse(null);
    }

    public <T> void replaceAll(Collection<T> objects, Function<T, T_MODEL> funcValue) {
        modelList.clear();
        objects.forEach(object -> modelList.add(funcValue.apply(object)));
    }

}