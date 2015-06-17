package org.smallbox.faraway.model;

/**
 * Created by Alex on 17/06/2015.
 */
public class PlanetAreaModel {
    public int type;

    public PlanetAreaModel() {
        this.type = (int)(Math.random() * 2);
    }
}
