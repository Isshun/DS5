package org.smallbox.faraway;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.game.model.item.ParcelModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 29/06/2015.
 */
public class ParcelPathCache {
    private final Map<ParcelModel, PathCacheModel>  _paths;
    private final List<PathCacheModel>              _pathsBy;
    private final ParcelModel                       _fromParcel;

    public ParcelPathCache(ParcelModel fromParcel) {
        _fromParcel = fromParcel;
        _paths = new HashMap<>();
        _pathsBy = new ArrayList<>();
    }

    public PathCacheModel getPath(ParcelModel toParcel) {
        return _paths.get(toParcel);
    }

    public void addPath(ParcelModel toParcel, GraphPath<ParcelModel> path) {
        _paths.put(toParcel, new PathCacheModel(_fromParcel, toParcel, path));
    }

    public void addPathBy(PathCacheModel path) {
        _pathsBy.add(path);
    }

    public List<PathCacheModel> getPathsBy() {
        return _pathsBy;
    }
}
