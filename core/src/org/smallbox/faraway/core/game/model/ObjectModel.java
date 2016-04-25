package org.smallbox.faraway.core.game.model;

import java.util.UUID;

/**
 * Created by Alex on 08/10/2015.
 */
// TODO concurrent modification exception
public class ObjectModel {
    final public int id = UUID.randomUUID().toString().hashCode();
}
