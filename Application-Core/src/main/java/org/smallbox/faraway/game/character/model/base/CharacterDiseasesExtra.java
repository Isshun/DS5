package org.smallbox.faraway.game.character.model.base;

import org.smallbox.faraway.game.characterDisease.CharacterDisease;
import org.smallbox.faraway.game.characterDisease.DiseaseInfo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CharacterDiseasesExtra extends CharacterExtra {

    private final Map<DiseaseInfo, CharacterDisease> _diseases = new ConcurrentHashMap<>();

    public CharacterDiseasesExtra(CharacterModel character) {
        super(character);
    }

    public void addDisease(CharacterDisease disease) {
        _diseases.put(disease.info, disease);
    }

    public void addDisease(DiseaseInfo diseaseInfo) {
        addDisease(new CharacterDisease(diseaseInfo, _character));
    }

    public Collection<CharacterDisease> getAll() {
        return _diseases.values();
    }

    public CharacterDisease get(DiseaseInfo diseaseInfo) {
        return _diseases.get(diseaseInfo);
    }

}
