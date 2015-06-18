package org.smallbox.faraway.ui.engine;

import org.smallbox.faraway.engine.Color;

public class UIMessage {
	public int 				posX;
	public int 				posY;
	public int 				frame;
	public TextView 		text;
	public ColorView		shape;
	public ColorView 		border;

	public UIMessage(String str, int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
		this.frame = 100;
		
		this.text = ViewFactory.getInstance().createTextView();
		this.text.setCharacterSize(14);
		this.text.setColor(Color.WHITE);
		this.text.setString(str);
		this.text.setPosition(posX + 10, posY + 5);
		
		this.border = ViewFactory.getInstance().createColorView(this.text.getContentWidth() + 20, this.text.getContentHeight() + 20);
		this.border.setBackgroundColor(new Color(255, 0, 0, 180));
		this.border.setPosition(posX, posY);

		this.shape = ViewFactory.getInstance().createColorView(this.text.getContentWidth() + 18, this.text.getContentHeight() + 18);
		this.shape.setBackgroundColor(new Color(0, 0, 0, 180));
		this.shape.setPosition(posX + 1, posY + 1);
	}
}
