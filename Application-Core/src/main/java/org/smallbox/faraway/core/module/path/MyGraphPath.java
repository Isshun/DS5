package org.smallbox.faraway.core.module.path;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.math.Vector2;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public class MyGraphPath extends DefaultGraphPath<ParcelModel> implements SmoothableGraphPath<ParcelModel, Vector2> {

    @Override
    public Vector2 getNodePosition(int index) {
        return new Vector2(get(index).x, get(index).y);
    }

    @Override
    public void swapNodes(int index1, int index2) {
        nodes.swap(index1, index2);
    }

    @Override
    public void truncatePath(int newLength) {
        nodes.truncate(newLength);
    }

}
