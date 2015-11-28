package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 15/06/2015.
 */
public abstract class GameModule extends ModuleBase implements GameObserver {
    private int                 _nbUpdate;
    private long                _totalTime;
    protected int               _updateInterval = 1;
    private List<Long>          _updateTimeHistory = new ArrayList<>();
    private long                _updateTime;

    @Override
    public boolean loadOnStart() {
        return true;
    }

    @Override
    public void update(int tick) {
        if (_isStarted) {
            if (hasOwnThread()) {
                _needUpdate = true;
                _needUpdateTick = tick;
            } else {
                innerUpdate(tick);
            }
        }
    }

    private void innerUpdate(int tick) {
        if (tick % _updateInterval == 0) {
            long time = System.currentTimeMillis();
            onUpdate(tick);
            _updateTimeHistory.add((System.currentTimeMillis() - time));
            if (_updateTimeHistory.size() > 10) {
                _updateTimeHistory.remove(0);
            }
            _updateTime = 0;
            for (long t: _updateTimeHistory) {
                _updateTime += t;
            }
            _updateTime = _updateTime / _updateTimeHistory.size();
            _totalTime += (System.currentTimeMillis() - time);
            _nbUpdate++;
        }
    }

    public SerializerInterface getSerializer() {
        return null;
    }

    public long         getModuleUpdateTime() { return _updateTime; }
}