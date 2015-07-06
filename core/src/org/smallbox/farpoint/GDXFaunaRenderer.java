package org.smallbox.farpoint;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.FaunaManager;
import org.smallbox.faraway.game.model.AnimalModel;
import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 26/06/2015.
 */
public class GDXFaunaRenderer extends BaseRenderer {
    private FaunaManager    _faunaManager;

    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        if (_faunaManager == null) {
            _faunaManager = (FaunaManager) Game.getInstance().getManager(FaunaManager.class);
        }

        for (AnimalModel animal: _faunaManager.getAnimals()) {
            int posX = (int) (animal.getX() * Constant.TILE_WIDTH * effect.getViewport().getScale());
            int posY = (int) (animal.getY() * Constant.TILE_HEIGHT * effect.getViewport().getScale());
            MovableModel.Direction direction = animal.getDirection();
            MovableModel.Direction move = animal.getMove();

            // Get offset based on current frame
            int offset = 0;
            int frame = 0;
            if (move != MovableModel.Direction.NONE) {
//				offset = (int) ((1-animProgress) * Constant.TILE_WIDTH);
                offset = (int) ((animal.getMoveProgress()) * Constant.TILE_WIDTH);
                frame = animal.getFrameIndex() / 20 % 4;
            }

            // Get exact position
            int dirIndex = 0;
            switch (direction) {
                case BOTTOM: posY += offset; dirIndex = 0; break;
                case LEFT: posX -= offset; dirIndex = 1; break;
                case RIGHT: posX += offset; dirIndex = 2; break;
                case TOP: posY -= offset; dirIndex = 3; break;
                case TOP_LEFT: posY -= offset; posX -= offset; dirIndex = 1; direction = MovableModel.Direction.LEFT; break;
                case TOP_RIGHT: posY -= offset; posX += offset; dirIndex = 2; direction = MovableModel.Direction.RIGHT; break;
                case BOTTOM_LEFT: posY += offset; posX -= offset; dirIndex = 1; direction = MovableModel.Direction.LEFT; break;
                case BOTTOM_RIGHT: posY += offset; posX += offset; dirIndex = 2; direction = MovableModel.Direction.RIGHT; break;
                default: break;
            }

            renderer.draw(SpriteManager.getInstance().getAnimal("data/res/animals/sand_worm.png"), posX - 2, posY - 2);
        }
    }

    @Override
    public void onRefresh(int frame) {
    }
}
