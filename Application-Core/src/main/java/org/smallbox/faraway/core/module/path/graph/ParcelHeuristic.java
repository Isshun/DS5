package org.smallbox.faraway.core.module.path.graph;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;
import org.smallbox.faraway.core.module.world.model.Parcel;

public class ParcelHeuristic implements Heuristic<Parcel> {

    @Override
    public float estimate(Parcel currentParcel, Parcel goalParcel) {
        return Vector2.dst(currentParcel.x, currentParcel.y, goalParcel.x, goalParcel.y);
    }

}
