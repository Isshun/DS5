package org.smallbox.faraway.ui;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.manager.ServiceManager;

public class UserInterfaceCursor {

	void	draw(Renderer renderer, RenderEffect effect, int startX, int startY, int toX, int toY) {
		startX = Math.max(startX, 0);
		startY = Math.max(startY, 0);
		toX = Math.min(toX, ServiceManager.getWorldMap().getWidth());
		toY = Math.min(toY, ServiceManager.getWorldMap().getHeight());

		int border = 3;

		View viewItem = new View(32, 32);
		viewItem.setBackgroundColor(new Color(200, 255, 100, 120));

		View rectangle1 = new View(32, 32);
		rectangle1.setBackgroundColor(new Color(100, 255, 100, 20));

		View rectangle2 = new View(32, 32);
		rectangle2.setBackgroundColor(new Color(100, 255, 100, 40));

		View rectangleTop = new View(32, 32);
		rectangleTop.setBackgroundColor(new Color(100, 255, 100, 100));
		rectangleTop.setPosition(startX * 32 + border, startY * 32);
		renderer.draw(rectangleTop, effect);
		rectangleTop.setPosition(startX * 32 + border, (toY + 1) * 32 - border);
		renderer.draw(rectangleTop, effect);

		View rectangleLeft = new View(border, (toY - startY + 1) * 32);
		rectangleLeft.setBackgroundColor(new Color(100, 255, 100, 100));
		rectangleLeft.setPosition(startX * 32, startY * 32);
		renderer.draw(rectangleLeft, effect);
		rectangleLeft.setPosition((toX + 1) * 32 - border, startY * 32);
		renderer.draw(rectangleLeft, effect);
		
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				if ((x + y) % 2 == 0) {
					rectangle1.setPosition(x * 32, y * 32);
					renderer.draw(rectangle1, effect);
				} else {
					rectangle2.setPosition(x * 32, y * 32);
					renderer.draw(rectangle2, effect);
				}
				
				if (ServiceManager.getWorldMap().getRessource(x, y) != null) {
					viewItem.setPosition(x * 32, y * 32);
					renderer.draw(viewItem, effect);
				}
			}
		}
	}

}
