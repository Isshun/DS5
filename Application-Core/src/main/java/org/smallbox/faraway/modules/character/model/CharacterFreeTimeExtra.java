package org.smallbox.faraway.modules.character.model;

import org.smallbox.faraway.modules.character.model.base.CharacterExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CharacterFreeTimeExtra extends CharacterExtra {

    private Collection<Class<? extends JobModel>>   _types = new ConcurrentLinkedQueue<>();

    public CharacterFreeTimeExtra(CharacterModel character) {
        super(character);
    }

    public Collection<Class<? extends JobModel>> getTypes() {
        return _types;
    }

    public void addType(Class<? extends JobModel> cls) {
        _types.add(cls);
    }
}
