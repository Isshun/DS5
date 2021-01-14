package org.smallbox.faraway.client.render.layer.ground.chunkGenerator;

import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;

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

    public boolean check(WorldModule worldModule, ParcelModel parcel) {
        return Stream.of(boxedDirections).allMatch(box -> box.hasRequired
                ? worldModule.checkOrNull(parcel, ParcelModel::hasRock, box.direction)
                : worldModule.check(parcel, p -> !p.hasRock(), box.direction)
        );
    }

    public static BoxTileGeneratorRule not(MovableModel.Direction direction) {
        return new BoxTileGeneratorRule(direction, false);
    }

    public static BoxTileGeneratorRule has(MovableModel.Direction direction) {
        return new BoxTileGeneratorRule(direction, true);
    }

}
