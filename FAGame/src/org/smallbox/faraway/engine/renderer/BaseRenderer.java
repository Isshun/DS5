package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.util.Log;

public abstract class BaseRenderer {
	private long 	_totalTime;
	private int 	_nbDraw;

	public abstract void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress);
	public abstract void onRefresh(int frame);
	public void draw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		long time = System.currentTimeMillis();
		onDraw(renderer, effect, animProgress);
		_totalTime += (System.currentTimeMillis() - time);
		_nbDraw++;
	}

	public void dump() {
		if (_nbDraw != 0) {
			Log.notice("Renderer: " + this.getClass().getSimpleName() + ",\tdraw: " + _nbDraw + ",\tavg time: " + _totalTime / _nbDraw);
		}
	}
}
