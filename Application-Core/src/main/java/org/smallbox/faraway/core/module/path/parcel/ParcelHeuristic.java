package org.smallbox.faraway.core.module.path.parcel;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public class ParcelHeuristic implements Heuristic<ParcelModel> {

    @Override
    public float estimate(ParcelModel currentParcel, ParcelModel goalParcel) {
        return Vector2.dst(currentParcel.x, currentParcel.y, goalParcel.x, goalParcel.y);
    }

}
