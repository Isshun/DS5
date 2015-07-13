package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.*;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.MovableModel.Direction;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.ui.engine.ColorView;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.Constant;

import java.util.Collection;

public class CharacterRenderer extends BaseRenderer {
	private Collection<CharacterModel> 		_characters;
	private SpriteManager 					_spriteManager;
	private int 							_frame;
	private ColorView 						_redBackground;

	public CharacterRenderer() {
		_characters = Game.getInstance().getCharacterManager().getCharacters();
		_spriteManager = SpriteManager.getInstance();
		_redBackground = ViewFactory.getInstance().createColorView(32, 48);
		_redBackground.setBackgroundColor(new Color(200, 50, 0, 150));
	}

	public void	onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        int viewPortX = effect.getViewport().getPosX();
        int viewPortY = effect.getViewport().getPosY();
		double viewPortScale = effect.getViewport().getScale();

		for (CharacterModel c : _characters) {
			// Get game position and direction
			int posX = c.getX() * Constant.TILE_WIDTH + viewPortX;
			int posY = c.getY() * Constant.TILE_HEIGHT + viewPortY;
			Direction direction = c.getDirection();
			Direction move = c.getMove();
			int frame = 0;
			int dirIndex = 0;

			// Get offset based on current frame
			if (c.isAlive()) {
				int offset = 0;
				if (move != Direction.NONE) {
//					offset = (int) ((c.getMoveProgress() + (c.getMoveStep() * animProgress)) * Constant.TILE_WIDTH);
//                    if ("rhea".equals(c.getInfo().getFirstName().toLowerCase().trim())) {
//                        Log.notice("offset: " + offset);
//                    }
//				offset = (int) ((c.getMoveProgress()) * Constant.TILE_WIDTH);
					frame = c.getFrameIndex() / 20 % 4;
				}

				// Get exact position
				switch (direction) {
					case BOTTOM:
						posY += offset;
						dirIndex = 0;
						break;
					case LEFT:
						posX -= offset;
						dirIndex = 1;
						break;
					case RIGHT:
						posX += offset;
						dirIndex = 2;
						break;
					case TOP:
						posY -= offset;
						dirIndex = 3;
						break;
					case TOP_LEFT:
						posY -= offset;
						posX -= offset;
						dirIndex = 1;
						direction = Direction.LEFT;
						break;
					case TOP_RIGHT:
						posY -= offset;
						posX += offset;
						dirIndex = 2;
						direction = Direction.RIGHT;
						break;
					case BOTTOM_LEFT:
						posY += offset;
						posX -= offset;
						dirIndex = 1;
						direction = Direction.LEFT;
						break;
					case BOTTOM_RIGHT:
						posY += offset;
						posX += offset;
						dirIndex = 2;
						direction = Direction.RIGHT;
						break;
					default:
						break;
				}
			}

			// Bad status
			if (c.getNeeds().happiness < 20) {
				_redBackground.setPosition(posX, posY);
				renderer.draw(_redBackground, effect);
			}

			// Draw characters
			{
				SpriteModel sprite = _spriteManager.getCharacter(c, dirIndex, frame);
				renderer.draw(sprite, posX, posY);
			}

            // Is dead
            if (!c.isAlive()) {
                SpriteModel sprite = SpriteManager.getInstance().getIcon("data/res/ic_dead.png");
				renderer.draw(sprite, posX, posY);
                continue;
            }

            // Selection
			if (c.isSelected()) {
				SpriteModel sprite = _spriteManager.getSelector(_frame / 10);
				renderer.draw(sprite, posX, posY);
			}

			if (c.getInventory() != null) {
				SpriteModel sprite = SpriteManager.getInstance().getItem(c.getInventory());
				renderer.draw(sprite, posX, posY);
			}

			if (c.isSleeping()) {
				SpriteModel sprite = SpriteManager.getInstance().getIcon("data/res/ic_sleep.png");
				renderer.draw(sprite, posX, posY);
			}

			if (c.getJob() != null && c.getJob().getActionIcon() != null && c.getJob().getX() == c.getX() && c.getJob().getY() == c.getY()) {
				SpriteModel sprite = SpriteManager.getInstance().getIcon(c.getJob().getActionIcon());
				renderer.draw(sprite, posX, posY);
			}
		}
	}

	public void onRefresh(int frame) {
		_frame = frame;
	}

}