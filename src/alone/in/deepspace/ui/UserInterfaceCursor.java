package alone.in.deepspace.ui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.manager.ServiceManager;

public class UserInterfaceCursor {

	void	draw(RenderWindow app, RenderStates render, int startX, int startY, int toX, int toY) {
		startX = Math.max(startX, 0);
		startY = Math.max(startY, 0);
		toX = Math.min(toX, ServiceManager.getWorldMap().getWidth());
		toY = Math.min(toY, ServiceManager.getWorldMap().getHeight());

		int border = 3;
		
		RectangleShape rectangleItem = new RectangleShape(new Vector2f(32, 32));
		rectangleItem.setFillColor(new Color(200, 255, 100, 120));
		
		RectangleShape rectangle1 = new RectangleShape(new Vector2f(32, 32));
		rectangle1.setFillColor(new Color(100, 255, 100, 20));
		
		RectangleShape rectangle2 = new RectangleShape(new Vector2f(32, 32));
		rectangle2.setFillColor(new Color(100, 255, 100, 40));

		RectangleShape rectangleTop = new RectangleShape(new Vector2f((toX - startX + 1) * 32 - border * 2, border));
		rectangleTop.setFillColor(new Color(100, 255, 100, 100));
		rectangleTop.setPosition(new Vector2f(startX * 32 + border, startY * 32));
		app.draw(rectangleTop, render);
		rectangleTop.setPosition(new Vector2f(startX * 32 + border, (toY + 1) * 32 - border));
		app.draw(rectangleTop, render);

		RectangleShape rectangleLeft = new RectangleShape(new Vector2f(border, (toY - startY + 1) * 32));
		rectangleLeft.setFillColor(new Color(100, 255, 100, 100));
		rectangleLeft.setPosition(new Vector2f(startX * 32, startY * 32));
		app.draw(rectangleLeft, render);
		rectangleLeft.setPosition(new Vector2f((toX + 1) * 32 - border, startY * 32));
		app.draw(rectangleLeft, render);
		
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				if ((x + y) % 2 == 0) {
					rectangle1.setPosition(new Vector2f(x * 32, y * 32));
					app.draw(rectangle1, render);
				} else {
					rectangle2.setPosition(new Vector2f(x * 32, y * 32));
					app.draw(rectangle2, render);
				}
				
				if (ServiceManager.getWorldMap().getRessource(x, y) != null) {
					rectangleItem.setPosition(new Vector2f(x * 32, y * 32));
					app.draw(rectangleItem, render);
				}
			}
		}
	}

}
