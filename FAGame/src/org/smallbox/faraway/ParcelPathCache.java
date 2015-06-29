package org.smallbox.faraway;

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

    public ParcelPathCache() {
        _paths = new HashMap<>();
        _pathsBy = new ArrayList<>();
    }

    public PathCacheModel getPath(ParcelModel toParcel) {
        return _paths.get(toParcel);
    }

    public void addPath(ParcelModel toParcel, PathCacheModel path) {
        _paths.put(toParcel, path);
    }

    public void addPathBy(PathCacheModel path) {
        _pathsBy.add(path);
    }

    public List<PathCacheModel> getPathsBy() {
        return _pathsBy;
    }
}
