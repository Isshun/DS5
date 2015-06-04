package org.smallbox.faraway.ui;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.manager.ServiceManager;

public class UserInterfaceCursor {

	void	draw(GFXRenderer renderer, RenderEffect effect, int startX, int startY, int toX, int toY) {
		startX = Math.max(startX, 0);
		startY = Math.max(startY, 0);
		toX = Math.min(toX, ServiceManager.getWorldMap().getWidth());
		toY = Math.min(toY, ServiceManager.getWorldMap().getHeight());

		int border = 3;

		View viewItem = ViewFactory.getInstance().createColorView(32, 32);
		viewItem.setBackgroundColor(new Color(200, 255, 100, 120));

		View rectangle1 = ViewFactory.getInstance().createColorView(32, 32);
		rectangle1.setBackgroundColor(new Color(100, 255, 100, 20));

		View rectangle2 = ViewFactory.getInstance().createColorView(32, 32);
		rectangle2.setBackgroundColor(new Color(100, 255, 100, 40));

		View rectangleTop = ViewFactory.getInstance().createColorView(32, 32);
		rectangleTop.setBackgroundColor(new Color(100, 255, 100, 100));
		rectangleTop.setPosition(startX * 32 + border, startY * 32);
		rectangleTop.draw(renderer, effect);
		rectangleTop.setPosition(startX * 32 + border, (toY + 1) * 32 - border);
		rectangleTop.draw(renderer, effect);

		View rectangleLeft = ViewFactory.getInstance().createColorView(border, (toY - startY + 1) * 32);
		rectangleLeft.setBackgroundColor(new Color(100, 255, 100, 100));
		rectangleLeft.setPosition(startX * 32, startY * 32);
		rectangleLeft.draw(renderer, effect);
		rectangleLeft.setPosition((toX + 1) * 32 - border, startY * 32);
		rectangleLeft.draw(renderer, effect);
		
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				if ((x + y) % 2 == 0) {
					rectangle1.setPosition(x * 32, y * 32);
					rectangle1.draw(renderer, effect);
				} else {
					rectangle2.setPosition(x * 32, y * 32);
					rectangle2.draw(renderer, effect);
				}
				
				if (ServiceManager.getWorldMap().getResource(x, y) != null) {
					viewItem.setPosition(x * 32, y * 32);
					viewItem.draw(renderer, effect);
				}
			}
		}
	}

}