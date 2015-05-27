package org.smallbox.faraway.engine.ui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.smallbox.faraway.manager.SpriteManager;

public class UIMessage {
	public int 				posX;
	public int 				posY;
	public int 				frame;
	public Text 			text;
	public RectangleShape	shape;
	public RectangleShape 	border;

	public UIMessage(String str, int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
		this.frame = 100;
		
		this.text = new Text();
		this.text.setCharacterSize(14);
		this.text.setFont(SpriteManager.getInstance().getFont());
		this.text.setColor(Color.WHITE);
		this.text.setString(str);
		this.text.setPosition(posX + 10, posY + 5);
		
		this.border = new RectangleShape();
		this.border.setSize(new Vector2f(this.text.getLocalBounds().width + 20, this.text.getLocalBounds().height + 20));
		this.border.setFillColor(new Color(255, 0, 0, 180));
		this.border.setPosition(posX, posY);

		this.shape = new RectangleShape();
		this.shape.setSize(new Vector2f(this.text.getLocalBounds().width + 18, this.text.getLocalBounds().height + 18));
		this.shape.setFillColor(new Color(0, 0, 0, 180));
		this.shape.setPosition(posX + 1, posY + 1);
	}
}
