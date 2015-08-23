package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.renderer.GDXRenderer;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.ColorView;

public abstract class UICursor {
	protected ColorView RES_ODD;
	protected ColorView RES_EDEN;

	public UICursor() {
		RES_ODD = ViewFactory.getInstance().createColorView(32, 32);
		RES_ODD.setBackgroundColor(new Color(100, 255, 100, 20));
		RES_EDEN = ViewFactory.getInstance().createColorView(32, 32);
		RES_EDEN.setBackgroundColor(new Color(100, 255, 100, 40));
	}


	void	draw(GDXRenderer renderer, Viewport viewport, int startX, int startY, int toX, int toY, boolean isPressed) {
		startX = Math.max(startX, 0);
		startY = Math.max(startY, 0);
		toX = Math.min(toX, Game.getWorldManager().getWidth());
		toY = Math.min(toY, Game.getWorldManager().getHeight());

		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				onDraw(renderer, Game.getWorldManager().getParcel(x, y), x * 32 + viewport.getPosX(), y * 32 + viewport.getPosY(), (x + y) % 2 == 0, isPressed);
			}
		}
	}

	protected abstract void onDraw(GDXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed);

}
