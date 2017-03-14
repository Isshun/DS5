package org.smallbox.faraway.modules.characterRelation;

import org.smallbox.faraway.GameConfig;

/**
 * Created by Alex on 13/03/2017.
 */
public class CharacterRelationConfig extends GameConfig {
    public double havePeopleOnProximity = byHour(0.1);
    public double regularChange = byHour(-0.05);
}
