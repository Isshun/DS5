package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.common.ObjectModel;

import java.util.Collection;
import java.util.Random;

public abstract class GenericGameModule<T_MODEL extends ObjectModel, T extends ModuleObserver> extends GameModule<T> {
    protected Collection<T_MODEL> modelList;
    private int count;

    public int getCount() {
        return count;
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
        count++;
        modelList.add(model);
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

}