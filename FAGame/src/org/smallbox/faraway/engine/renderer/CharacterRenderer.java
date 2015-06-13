package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.ui.ColorView;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.Movable.Direction;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.character.CharacterStatus.Level;

import java.util.List;

public class CharacterRenderer implements IRenderer {
	private List<CharacterModel> _characters;
	private SpriteManager _spriteManager;
	private int 			_update;
	private ColorView _redBackground;

	public CharacterRenderer(List<CharacterModel> characters) {
		_characters = characters;
		_spriteManager = SpriteManager.getInstance();
		_redBackground = ViewFactory.getInstance().createColorView(32, 48);
		_redBackground.setBackgroundColor(new Color(200, 50, 0, 150));
	}

	public void	onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		for (CharacterModel c: _characters) {
			// Get game position and direction
			int posX = c.getX() * Constant.TILE_WIDTH - (Constant.CHAR_WIDTH - Constant.TILE_WIDTH) + 2;
			int posY = c.getY() * Constant.TILE_HEIGHT - (Constant.CHAR_HEIGHT - Constant.TILE_HEIGHT) + 0;
			Direction direction = c.getDirection();
			Direction move = c.getMove();

			// Get offset based on current frame
			int offset = 0;
			int frame = 0;
			if (move != Direction.NONE) {
				offset = (int) ((1-animProgress) * Constant.TILE_WIDTH);
				frame = c.getFrameIndex() / 20 % 4;
			}

			// Get exact position
			int dirIndex = 0;
			switch (direction) {
			case BOTTOM: posY -= offset; dirIndex = 0; break;
			case LEFT: posX += offset; dirIndex = 1; break;
			case RIGHT: posX -= offset; dirIndex = 2; break;
			case TOP: posY += offset; dirIndex = 3; break;
			case TOP_LEFT: posY += offset; posX += offset; dirIndex = 1; direction = Direction.LEFT; break;
			case TOP_RIGHT: posY += offset; posX -= offset; dirIndex = 2; direction = Direction.RIGHT; break;
			case BOTTOM_LEFT: posY -= offset; posX += offset; dirIndex = 1; direction = Direction.LEFT; break;
			case BOTTOM_RIGHT: posY -= offset; posX -= offset; dirIndex = 2; direction = Direction.RIGHT; break;
			default: break;
			}

			// Bad status
			if (c.getStatus().getLevel() == Level.BAD || c.getStatus().getLevel() == Level.REALLY_BAD) {
				_redBackground.setPosition(posX, posY);
				renderer.draw(_redBackground, effect);
			}
			
			// Draw character
			{
				SpriteModel sprite = _spriteManager.getCharacter(c, dirIndex, frame);
				sprite.setPosition(posX, posY + (c.isSleeping() ? 20 : 0));
				renderer.draw(sprite, effect);
			}
						
			// Selection
			if (c.isSelected()) {
				SpriteModel sprite = _spriteManager.getSelector(_update);
				sprite.setPosition(posX - 2, posY + (c.isSleeping() ? 20 : 0) - 2);
				renderer.draw(sprite, effect);
			}

			if (c.getInventory() != null) {
				SpriteModel sprite = SpriteManager.getInstance().getItem(c.getInventory());
				sprite.setPosition(posX - 2, posY - 2);
				renderer.draw(sprite, effect);
			}

			if (c.isSleeping()) {
				SpriteModel sprite = SpriteManager.getInstance().getIcon("data/res/ic_sleep.png");
				sprite.setPosition(posX - 2, posY - 2);
				renderer.draw(sprite, effect);
			}
		}
	}

	public void onRefresh(int update) {
		_update = update;
	}

	@Override
	public void invalidate(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		
	}

}