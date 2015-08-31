package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.util.Log;

public abstract class BaseRenderer {
	private long 	_totalTime;
	private int 	_nbDraw;
    private boolean _isLoaded;

    public abstract void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress);
	public abstract void onRefresh(int frame);
	public abstract boolean isActive(GameConfig config);
	public int getLevel() {
		return 0;
	}
	public void draw(GDXRenderer renderer, Viewport viewport, double animProgress) {
		long time = System.currentTimeMillis();
		onDraw(renderer, viewport, animProgress);
		_totalTime += (System.currentTimeMillis() - time);
		_nbDraw++;
	}

	public void dump() {
		if (_nbDraw != 0) {
			Log.notice("Renderer: " + this.getClass().getSimpleName() + ",\tdraw: " + _nbDraw + ",\tavg time: " + _totalTime / _nbDraw);
		}
	}

	public void init() {
	}

    public boolean isLoaded() {
        return _isLoaded;
    }

    public void load() {
        System.out.println("[BaseRender] load " + getClass().getSimpleName());
        _isLoaded = true;
    }

    public void unload() {
        System.out.println("[BaseRender] unload " + getClass().getSimpleName());
        _isLoaded = false;
    }
}
