package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.modules.characterDisease.CharacterDisease;
import org.smallbox.faraway.modules.characterDisease.DiseaseInfo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alex on 24/06/2015.
 */
public class CharacterDiseasesExtra {

    private Map<DiseaseInfo, CharacterDisease> _diseases = new ConcurrentHashMap<>();

    public void addDisease(CharacterDisease disease) {
        _diseases.put(disease.info, disease);
    }

    public Collection<CharacterDisease> getAll() {
        return _diseases.values();
    }

    public CharacterDisease get(DiseaseInfo diseaseInfo) {
        return _diseases.get(diseaseInfo);
    }

}
