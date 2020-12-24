package org.smallbox.faraway.core.module.world;

public class ChunkCacheModel {
    public final int _index;
    public final int fromX;
    public final int fromY;
    public final int fromZ;
    public final int toX;
    public final int toY;
    public final int toZ;

    public ChunkCacheModel(int index, int fromX, int toX, int fromY, int toY, int fromZ, int toZ) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.fromZ = fromZ;
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
        _index = index;
    }
}
