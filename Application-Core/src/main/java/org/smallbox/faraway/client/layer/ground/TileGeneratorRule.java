package org.smallbox.faraway.client.layer.ground;

import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.stream.Stream;

public class TileGeneratorRule {
    private final BoxTileGeneratorRule[] boxedDirections;
    public final int position;
    public final String key;

    public static class BoxTileGeneratorRule {
        public final MovableModel.Direction direction;
        public final boolean hasRequired;

        public BoxTileGeneratorRule(MovableModel.Direction direction, boolean hasRequired) {
            this.direction = direction;
            this.hasRequired = hasRequired;
        }
    }

    public TileGeneratorRule(int position, String key, BoxTileGeneratorRule... boxedDirections) {
        this.position = position;
        this.key = key;
        this.boxedDirections = boxedDirections;
    }

    public boolean check(WorldModule worldModule, Parcel parcel) {
        return Stream.of(boxedDirections).allMatch(box -> box.hasRequired
                ? worldModule.checkOrNull(parcel, Parcel::hasRock, box.direction)
                : worldModule.check(parcel, p -> !p.hasRock(), box.direction)
        );
    }

    public static BoxTileGeneratorRule hasNot(MovableModel.Direction direction) {
        return new BoxTileGeneratorRule(direction, false);
    }

    public static BoxTileGeneratorRule has(MovableModel.Direction direction) {
        return new BoxTileGeneratorRule(direction, true);
    }

}
