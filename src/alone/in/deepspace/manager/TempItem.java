package alone.in.deepspace.manager;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;

import alone.in.deepspace.util.Constant;

public class TempItem {
	Sprite	sprite;
	int 	posX;
	int 	posY;
	double 	offsetX;
	double 	offsetY;
	int 	lifespan;
	
	public TempItem(int posX, int posY, int lifespan) {
		this.sprite = SpriteManager.getInstance().getBullet(0);
		this.sprite.setPosition(this.posX * Constant.TILE_WIDTH, this.posY * Constant.TILE_HEIGHT);
		this.posX = posX;
		this.posY = posY;
		this.offsetX = 3;
		this.offsetY = 3;
		this.lifespan = lifespan;
	}

	public void refresh(RenderWindow app, RenderStates render, double animProgress) {
		double oX = (1-animProgress) * Constant.TILE_WIDTH * -offsetX;
		double oY = (1-animProgress) * Constant.TILE_HEIGHT * -offsetY;
		int x = this.posX * Constant.TILE_WIDTH;
		int y = this.posY * Constant.TILE_HEIGHT;
//		if (offsetX > 0) x -= offset;
//		else x += offset;
//		if (offsetY > 0) y -= offset;
//		else y -= offset;
		
		this.sprite.setPosition((int)(x + oX), (int)(y + oY));
		app.draw(this.sprite, render);
	}
	
	public void update() {
		this.lifespan--;
		this.posX += this.offsetX;
		this.posY += this.offsetY;
		this.sprite.setPosition(this.posX * Constant.TILE_WIDTH, this.posY * Constant.TILE_HEIGHT);
	}
}
