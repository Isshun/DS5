//package org.smallbox.faraway.client.manager;
//
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import org.apache.commons.collections4.CollectionUtils;
//import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
//import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
//import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
//import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
//
//@ApplicationObject
//public class SpriteFactory {
//
//    @Inject
//    private SpriteManager spriteManager;
//
//    public Sprite getNewSprite(ItemInfo itemInfo) {
//        return itemInfo != null && CollectionUtils.isNotEmpty(itemInfo.graphics) ? getNewSprite(itemInfo.graphics.get(0), 0) : null;
//    }
//
//    public Sprite getNewSprite(GraphicInfo graphicInfo) {
//        return getNewSprite(graphicInfo, 0);
//    }
//
//    public Sprite getNewSprite(GraphicInfo graphicInfo, int tile) {
//        spriteManager.getAssetManager().finishLoading();
//
//        assert graphicInfo != null;
//
//        if (graphicInfo.spriteId == -1) {
//            graphicInfo.spriteId = ++spriteManager._spriteCount;
//        }
//
//        long sum = spriteManager.getSum(graphicInfo.spriteId, graphicInfo.x, graphicInfo.y, tile);
//
////        long sum = graphicInfo.type == GraphicInfo.Type.WALL || graphicInfo.type == GraphicInfo.Type.DOOR ?
////                getSum(graphicInfo.spriteId, 0, 0, 0) :
////                getSum(graphicInfo.spriteId, 0, 0, 0);
////
//        Sprite sprite = spriteManager._sprites.get(sum);
//        if (sprite == null) {
////            Texture texture = _textures.get(graphicInfo.packageName + graphicInfo.path);
//            Texture texture = spriteManager.getAssetManager().get("data" + graphicInfo.path, Texture.class);
//            if (texture != null) {
//                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//
//                sprite = new Sprite(texture,
//                        (graphicInfo.x + tile) * graphicInfo.tileWidth,
//                        graphicInfo.y * graphicInfo.tileHeight,
//                        graphicInfo.tileWidth,
//                        graphicInfo.tileHeight);
//                sprite.setFlip(false, true);
//                sprite.setColor(new Color(255, 255, 255, 1));
////                if (isIcon) {
////                    switch (Math.max(width/32, height/32)) {
////                        case 2: sprite.setScale(0.85f, 0.85f); break;
////                        case 3: sprite.setScale(0.55f, 0.55f); break;
////                        case 4: sprite.setScale(0.35f, 0.35f); break;
////                        case 5: sprite.setScale(0.32f, 0.32f); break;
////                        case 6: sprite.setScale(0.3f, 0.3f); break;
////                        case 7: sprite.setScale(0.25f, 0.25f); break;
////                        case 8: sprite.setScale(0.2f, 0.2f); break;
////                    }
////                }
//
//                spriteManager._sprites.put(sum, sprite);
//            }
//        }
//
//        return spriteManager._sprites.get(sum);
//    }
//}