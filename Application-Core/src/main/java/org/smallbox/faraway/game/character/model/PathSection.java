package org.smallbox.faraway.game.character.model;

import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.util.Utils;

public class PathSection {
    public final int length;
    public final int dirX;
    public final int dirY;
    public final Parcel p1;
    public final Parcel p2;
    public long startTime;
    public long lastTime;

    public PathSection(Parcel p1, Parcel p2, int length) {
        this.dirX = Utils.bound(-1, 1, p2.x - p1.x);
        this.dirY = Utils.bound(-1, 1, p2.y - p1.y);
        this.length = length;
        this.p1 = p1;
        this.p2 = p2;
    }
}
