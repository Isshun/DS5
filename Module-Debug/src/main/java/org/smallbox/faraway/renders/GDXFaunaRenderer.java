//package org.smallbox.faraway.renders;
//
//import org.smallbox.faraway.client.renderer.SpriteManager;
//import org.smallbox.faraway.client.renderer.Viewport;
//import org.smallbox.faraway.engine.renderer.BaseRenderer;
//import org.smallbox.faraway.engine.renderer.GDXRenderer;
//import org.smallbox.faraway.game.model.AnimalModel;
//import org.smallbox.faraway.game.model.GameConfig;
//import org.smallbox.faraway.game.model.MovableModel;
//import org.smallbox.faraway.game.module.ModuleManager;
//import org.smallbox.faraway.module.extra.FaunaModule;
//import org.smallbox.faraway.util.Constant;
//
///**
// * Created by Alex
// */
//public class GDXFaunaRenderer extends BaseRenderer {
//    private FaunaModule _faunaModule;
//
//    @Override
//    public void draw(GDXRenderer renderer, Viewport viewport, double animProgress) {
//        if (_faunaModule == null) {
//            _faunaModule = (FaunaModule) Application.moduleManager.getModule(FaunaModule.class);
//        }
//
//        for (AnimalModel animal: _faunaModule.getAnimals()) {
//            int posX = (int) (animal.getX() * Constant.TILE_WIDTH * viewport.getScale());
//            int posY = (int) (animal.getY() * Constant.TILE_HEIGHT * viewport.getScale());
//            MovableModel.Direction direction = animal.getDirection();
//            MovableModel.Direction move = animal.getMove();
//
//            // Get offset based on current frame
//            int offset = 0;
//            int frame = 0;
//            if (move != MovableModel.Direction.NONE) {
////                offset = (int) ((1-animProgress) * Constant.TILE_WIDTH);
//                offset = (int) ((animal.getMoveProgress()) * Constant.TILE_WIDTH);
//                frame = animal.getFrameIndex() / 20 % 4;
//            }
//
//            // Get exact position
//            int dirIndex = 0;
//            switch (direction) {
//                case BOTTOM: posY += offset; dirIndex = 0; break;
//                case LEFT: posX -= offset; dirIndex = 1; break;
//                case RIGHT: posX += offset; dirIndex = 2; break;
//                case TOP: posY -= offset; dirIndex = 3; break;
//                case TOP_LEFT: posY -= offset; posX -= offset; dirIndex = 1; direction = MovableModel.Direction.LEFT; break;
//                case TOP_RIGHT: posY -= offset; posX += offset; dirIndex = 2; direction = MovableModel.Direction.RIGHT; break;
//                case BOTTOM_LEFT: posY += offset; posX -= offset; dirIndex = 1; direction = MovableModel.Direction.LEFT; break;
//                case BOTTOM_RIGHT: posY += offset; posX += offset; dirIndex = 2; direction = MovableModel.Direction.RIGHT; break;
//                default: break;
//            }
//
//            renderer.draw(ApplicationClient.spriteManager.getAnimal("data/res/animals/sand_worm.png"), posX - 2, posY - 2);
//        }
//    }
//
//    @Override
//    public void onRefresh(int frame) {
//    }
//
//    @Override
//    public boolean isActive(GameConfig config) {
//        return config.render.fauna;
//    }
//}
