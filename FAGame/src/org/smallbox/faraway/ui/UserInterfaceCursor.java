package org.smallbox.faraway.ui;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.Viewport;
import org.smallbox.faraway.engine.ui.ColorView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.manager.ServiceManager;

public class UserInterfaceCursor {

	void	draw(GFXRenderer renderer, Viewport viewport, int startX, int startY, int toX, int toY) {
		startX = Math.max(startX, 0);
		startY = Math.max(startY, 0);
		toX = Math.min(toX, ServiceManager.getWorldMap().getWidth());
		toY = Math.min(toY, ServiceManager.getWorldMap().getHeight());

		int border = 3;

		View viewItem = ViewFactory.getInstance().createColorView(32, 32);
		viewItem.setBackgroundColor(new Color(200, 255, 100, 120));

		ColorView rectangle1 = ViewFactory.getInstance().createColorView(32, 32);
		rectangle1.setBackgroundColor(new Color(100, 255, 100, 20));

		ColorView rectangle2 = ViewFactory.getInstance().createColorView(32, 32);
		rectangle2.setBackgroundColor(new Color(100, 255, 100, 40));

//		View rectangleTop = ViewFactory.getInstance().createColorView(32, 32);
//		rectangleTop.setBackgroundColor(new Color(100, 255, 100, 100));
//		rectangleTop.setPosition(startX * 32 + border, startY * 32);
//		rectangleTop.draw(renderer, viewport);
//		rectangleTop.setPosition(startX * 32 + border, (toY + 1) * 32 - border);
//		rectangleTop.draw(renderer, viewport);
//
//		View rectangleLeft = ViewFactory.getInstance().createColorView(border, (toY - startY + 1) * 32);
//		rectangleLeft.setBackgroundColor(new Color(100, 255, 100, 100));
//		rectangleLeft.setPosition(startX * 32, startY * 32);
//		rectangleLeft.draw(renderer, viewport);
//		rectangleLeft.setPosition((toX + 1) * 32 - border, startY * 32);
//		rectangleLeft.draw(renderer, viewport);
		
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				if ((x + y) % 2 == 0) {
                    rectangle1.setPosition(x * 32, y * 32);
					renderer.draw(rectangle1, viewport.getPosX(), viewport.getPosY());
//					rectangle1.draw(renderer, effect);
				} else {
                    rectangle2.setPosition(x * 32, y * 32);
                    renderer.draw(rectangle2, viewport.getPosX(), viewport.getPosY());
//					rectangle2.draw(renderer, effect);
				}
				
				if (ServiceManager.getWorldMap().getResource(x, y) != null) {
					viewItem.setPosition(x * 32, y * 32);
					viewItem.draw(renderer, viewport.getPosX(), viewport.getPosY());
				}
			}
		}
	}

}
