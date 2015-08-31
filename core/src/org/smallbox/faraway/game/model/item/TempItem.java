//package org.smallbox.faraway.game.model.item;
//
//import org.smallbox.faraway.engine.renderer.GDXRenderer;
//import org.smallbox.faraway.core.Viewport;
//import org.smallbox.faraway.core.SpriteManager;
//import org.smallbox.faraway.engine.SpriteModel;
//import org.smallbox.faraway.util.Constant;
//
//public class TempItem {
//	SpriteModel 	sprite;
//	int 			posX;
//	int 			posY;
//	public double 	offsetX;
//	public double 	offsetY;
//	public int 		lifespan;
//
//	public TempItem(int posX, int posY, int lifespan) {
//		this.sprite = SpriteManager.getInstance().getBullet(0);
//		this.sprite.setPosition(this.posX * Constant.TILE_WIDTH, this.posY * Constant.TILE_HEIGHT);
//		this.posX = posX;
//		this.posY = posY;
//		this.offsetX = 3;
//		this.offsetY = 3;
//		this.lifespan = lifespan;
//	}
//
//	public void refresh(GDXRenderer renderer, Viewport viewport, double animProgress) {
//		double oX = (1-animProgress) * Constant.TILE_WIDTH * -offsetX;
//		double oY = (1-animProgress) * Constant.TILE_HEIGHT * -offsetY;
//		int x = this.posX * Constant.TILE_WIDTH;
//		int y = this.posY * Constant.TILE_HEIGHT;
////		if (offsetX > 0) x -= offset;
////		else x += offset;
////		if (offsetY > 0) y -= offset;
////		else y -= offset;
//
//		renderer.draw(this.sprite, (int)(x + oX), (int)(y + oY));
//	}
//
//	public void update() {
//		this.lifespan--;
//		this.posX += this.offsetX;
//		this.posY += this.offsetY;
//		this.sprite.setPosition(this.posX * Constant.TILE_WIDTH, this.posY * Constant.TILE_HEIGHT);
//	}
//}
