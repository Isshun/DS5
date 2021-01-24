package org.smallbox.faraway.game.character.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.smallbox.faraway.core.path.spline.BezierPath;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PathModel {

//    public final double[] _spline;
//    public final double[] _c;
    public final List<Vector3> _curve;
    private final Parcel _lastParcelCharacter;
    public Path<Vector2> myCatmull;
    private long _startTime;
    public boolean minusOne;
    private float moveSpeed;

    public void setStartTime(long startTime) {
        _startTime = startTime;
    }

    public long getStartTime() {
        return _startTime;
    }

    public Parcel getLastParcelCharacter() {
        return _lastParcelCharacter;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public static class PathSection {
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

    private Parcel _currentParcel;
    private final Parcel _firstParcel;
    private final Parcel _lastParcel;
    private final int                         _length;
    private int                         _index;
    public GraphPath<Parcel>      _nodes;
    private final Queue<PathSection>    _smooth = new ConcurrentLinkedQueue<>();

    public static PathModel create(GraphPath<Parcel> nodes, boolean minusOne) {
        if (nodes != null) {
            return new PathModel(nodes, minusOne);
        }
        return null;
    }

    public Queue<PathSection> getSections() {
        return _smooth;
    }

    private PathModel(GraphPath<Parcel> nodes, boolean minusOne) {
        _nodes = nodes;
        this.minusOne = minusOne;

        List<Vector3> vector3List = new ArrayList<>();
        nodes.forEach(parcel -> vector3List.add(new Vector3(parcel.x, parcel.y, 0)));
        BezierPath bezierPath = new BezierPath();
        bezierPath.SetControlPoints(vector3List);
        _curve = bezierPath.GetDrawingPoints1();


//        Vector2 out = new Vector2();
//        Vector2 tmp = new Vector2();
//        Vector2[] dataSet = new Vector2[nodes.getCount()];
//
//        /* fill dataSet with path points */
//        CatmullRomSpline.calculate(out, t, dataSet, continuous, tmp);// stores in the vector out the point of the catmullRom path of the dataSet in the time t. Uses tmp as a temporary vector. if continuous is true, the path is a loop.
//        CatmullRomSpline.derivative(out, t, dataSet, continuous, tmp); // the same as above, but stores the derivative of the time t in the vector out

        int count = nodes.getCount();
        Vector2[] vector2List = new Vector2[count + 2];
        vector2List[0] = new Vector2(nodes.get(0).x, nodes.get(0).y);
        for (int i = 0; i < count; i++) {
            vector2List[i + 1] = new Vector2(nodes.get(i).x, nodes.get(i).y);
            vector2List[i + 2] = vector2List[i + 1];
        }

        myCatmull = new CatmullRomSpline<>(vector2List, false);
//        myCatmull = new BSpline<>(vector2List, 0, false);
//        myCatmull = new Bezier<>(vector2List, 5, 4);

//        int x1 = nodes.get(0).x;
//        int y1 = nodes.get(0).y;
//        double[] c = new double[Math.max(4, nodes.getCount()) * 3];
//        for (int i = 0; i < nodes.getCount(); i++) {
//            ParcelModel parcel = nodes.get(i);
//            c[i * 3]  =   parcel.x - x1;
//            c[i * 3 + 1]  =   parcel.y - y1;
//            c[i * 3 + 2]  =   0.0;
//        }
//        _c = c;
//        _spline = SplineFactory.createBezier (c,     32);

        _currentParcel = nodes.getCount() > 0 ? nodes.get(0) : null;
        _firstParcel = nodes.getCount() > 0 ? nodes.get(0) : null;
        _lastParcel = nodes.getCount() > 0 ? nodes.get(nodes.getCount() - 1) : null;
        _lastParcelCharacter = nodes.getCount() > 0 ? nodes.get(Math.max(nodes.getCount() - (minusOne ? 2 : 1), 0)) : null;
        _length = nodes.getCount();
        _index = 0;

        Parcel p1 = nodes.get(0);
//        int lastOffsetX = nodes.get(1).x - nodes.get(0).x;
//        int lastOffsetY = nodes.get(1).y - nodes.get(0).y;
//        int length = 1;
//        for (int i = 1; i < nodes.getCount() - 1; i++) {
//            int offsetX = nodes.get(i + 1).x - nodes.get(i).x;
//            int offsetY = nodes.get(i + 1).y - nodes.get(i).y;
//            if (offsetX != lastOffsetX || offsetY != lastOffsetY) {
//                _smooth.add(new PathSection(p1, nodes.get(i), length));
//                p1 = nodes.get(i + 1);
//                length = 1;
//                lastOffsetX = offsetX;
//                lastOffsetY = offsetY;
//            } else {
//                length++;
//            }
//        }

        _smooth.add(new PathSection(p1, _lastParcel, nodes.getCount()));
    }

    private int dirY(int dir) {
        switch (dir) {
            case 1:
            case 2:
            case 3:
                return 1;
            case 5:
            case 6:
            case 7:
                return -1;
        }
        return 0;
    }

    private int dirX(int dir) {
        switch (dir) {
            case 1:
            case 7:
            case 8:
                return 1;
            case 3:
            case 4:
            case 5:
                return -1;
        }
        return 0;
    }

    private int getDir(Parcel p1, Parcel p2) {
        if (p1.x < p2.x     &&  p1.y < p2.y)     return 1;
        if (p1.x == p2.x    &&  p1.y < p2.y)    return 2;
        if (p1.x > p2.x     &&  p1.y < p2.y)     return 3;
        if (p1.x > p2.x     &&  p1.y == p2.y)    return 4;
        if (p1.x > p2.x     &&  p1.y > p2.y)    return 5;
        if (p1.x == p2.x     &&  p1.y > p2.y)    return 6;
        if (p1.x < p2.x     &&  p1.y > p2.y)    return 7;
        if (p1.x < p2.x     &&  p1.y == p2.y)    return 8;
        return 0;
    }

    public int          getLength() { return _length; }
    public Parcel getLastParcel() { return _lastParcel; }
    public Parcel getFirstParcel() { return _firstParcel; }
    public Parcel getCurrentParcel() { return _currentParcel; }
    public int getIndex() { return _index; }
    public GraphPath<Parcel> getNodes() { return _nodes; }

    public boolean next() {
        if (++_index < _length - (minusOne ? 1 : 0)) {
            _currentParcel = _nodes.get(_index);
            return true;
        }
        return false;
    }

    public boolean isValid() {
        for (Parcel parcel: _nodes) {
            if (!parcel.isWalkable()) {
                return false;
            }
        }
        return true;
    }
}