package org.smallbox.faraway.renderer;

import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.Movable.Direction;
import org.smallbox.faraway.model.character.Character;
import org.smallbox.faraway.model.character.CharacterStatus.Level;
import org.smallbox.faraway.engine.util.Constant;

public class CharacterRenderer implements IRenderer {
	private List<Character> _characters;
	private SpriteManager 	_spriteManager;
	private int 			_update;
	private RectangleShape _redBackground;

	public CharacterRenderer(List<Character> characters) {
		_characters = characters;
		_spriteManager = SpriteManager.getInstance();
		_redBackground = new RectangleShape(new Vector2f(32, 48));
		_redBackground.setFillColor(new Color(200, 50, 0, 150));
	}

	public void	onDraw(Renderer renderer, RenderEffect effect, double animProgress) {
		for (Character c: _characters) {
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
			
			// Draw sprite
			SpriteModel sprite = _spriteManager.getCharacter(c, dirIndex, frame);
			sprite.setPosition(posX, posY + (c.isSleeping() ? 20 : 0));
			renderer.draw(sprite, effect);
						
			// Selection
			if (c.isSelected()) {
				sprite = _spriteManager.getSelector(_update);
				sprite.setPosition(posX - 2, posY + (c.isSleeping() ? 20 : 0) - 2);
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