package org.smallbox.faraway.modules.character.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Utils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 30/10/2015.
 */
public class PathModel {

    private long _startTime;

    public void setStartTime(long startTime) {
        _startTime = startTime;
    }

    public long getStartTime() {
        return _startTime;
    }

    public static class PathSection {
        public final int length;
        public final int dirX;
        public final int dirY;
        public final ParcelModel p1;
        public final ParcelModel p2;
        public long startTime;
        public long lastTime;

        public PathSection(ParcelModel p1, ParcelModel p2, int length) {
            this.dirX = Utils.bound(-1, 1, p2.x - p1.x);
            this.dirY = Utils.bound(-1, 1, p2.y - p1.y);
            this.length = length;
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    private ParcelModel                 _currentParcel;
    private ParcelModel                 _firstParcel;
    private ParcelModel                 _lastParcel;
    private int                         _length;
    private int                         _index;
    private GraphPath<ParcelModel>      _nodes;
    private final Queue<PathSection>    _smooth = new ConcurrentLinkedQueue<>();

    public static PathModel create(GraphPath<ParcelModel> nodes) {
        if (nodes != null) {
            return new PathModel(nodes);
        }
        return null;
    }

    public Queue<PathSection> getSections() {
        return _smooth;
    }

    private PathModel(GraphPath<ParcelModel> nodes) {
        _nodes = nodes;
        _currentParcel = nodes.getCount() > 0 ? nodes.get(0) : null;
        _firstParcel = nodes.getCount() > 0 ? nodes.get(0) : null;
        _lastParcel = nodes.getCount() > 0 ? nodes.get(nodes.getCount()-1) : null;
        _length = nodes.getCount();
        _index = 0;

        ParcelModel p1 = nodes.get(0);
        int lastOffsetX = nodes.get(1).x - nodes.get(0).x;
        int lastOffsetY = nodes.get(1).y - nodes.get(0).y;
        int length = 1;
        for (int i = 1; i < nodes.getCount() - 1; i++) {
            int offsetX = nodes.get(i + 1).x - nodes.get(i).x;
            int offsetY = nodes.get(i + 1).y - nodes.get(i).y;
            if (offsetX != lastOffsetX || offsetY != lastOffsetY) {
                _smooth.add(new PathSection(p1, nodes.get(i), length));
                p1 = nodes.get(i + 1);
                length = 1;
                lastOffsetX = offsetX;
                lastOffsetY = offsetY;
            } else {
                length++;
            }
        }
        _smooth.add(new PathSection(p1, _lastParcel, length));

        System.out.println("ok");
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

    private int getDir(ParcelModel p1, ParcelModel p2) {
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
    public ParcelModel  getLastParcel() { return _lastParcel; }
    public ParcelModel  getFirstParcel() { return _firstParcel; }
    public ParcelModel  getCurrentParcel() { return _currentParcel; }
    public int getIndex() { return _index; }
    public GraphPath<ParcelModel> getNodes() { return _nodes; }

    public boolean next() {
        if (++_index < _length) {
            _currentParcel = _nodes.get(_index);
            return true;
        }
        return false;
    }

    public boolean isValid() {
        for (ParcelModel parcel: _nodes) {
            if (!parcel.isWalkable()) {
                return false;
            }
        }
        return true;
    }
}