package org.smallbox.faraway.client.render.layer.ground.chunkGenerator;

import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.function.Predicate;

public class TileGeneratorRule {
    public final int position;
    public final String key;
    public final Predicate<ParcelModel> predicate;
    public final MovableModel.Direction[] directions;
    public final MovableModel.Direction directionEx;

    public TileGeneratorRule(int position, String key, Predicate<ParcelModel> predicate, MovableModel.Direction... directions) {
        this.position = position;
        this.key = key;
        this.predicate = predicate;
        this.directionEx = null;
        this.directions = directions;
    }

    public TileGeneratorRule(int position, String key, MovableModel.Direction directionEx, MovableModel.Direction... directions) {
        this.position = position;
        this.key = key;
        this.predicate = null;
        this.directionEx = directionEx;
        this.directions = directions;
    }

    public boolean check(WorldModule worldModule, ParcelModel parcel) {
        if (predicate != null) {
            return worldModule.check(parcel, predicate, directions);
        }
        return worldModule.check(parcel, ParcelModel::hasRock, directions) && worldModule.check(parcel, p -> !p.hasRock(), directionEx);
    }

}
