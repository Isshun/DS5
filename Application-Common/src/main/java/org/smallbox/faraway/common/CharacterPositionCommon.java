package org.smallbox.faraway.common;

import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class CharacterPositionCommon {
    public int characterId;
    public Path<Vector2> myCatmull;
    public int pathLength;
    public List<Vector3> _curve;
    public double _moveProgress;
    public double _moveProgress2;
    public int parcelX;
    public int parcelY;
    public int parcelZ;
}
