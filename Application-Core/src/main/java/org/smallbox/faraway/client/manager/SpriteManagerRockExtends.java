//package org.smallbox.faraway.client.manager;
//
//import com.badlogic.gdx.assets.AssetManager;
//import com.badlogic.gdx.files.FileHandle;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.Pixmap;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.TextureData;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.math.Rectangle;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.NotImplementedException;
//import org.smallbox.faraway.core.GameException;
//import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
//import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
//import org.smallbox.faraway.core.game.Data;
//import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
//import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
//import org.smallbox.faraway.core.module.world.model.ConsumableItem;
//import org.smallbox.faraway.core.module.world.model.NetworkItem;
//import org.smallbox.faraway.core.module.world.model.StructureItem;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.util.Constant;
//import org.smallbox.faraway.util.FileUtils;
//import org.smallbox.faraway.util.log.Log;
//
//import java.io.File;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@ApplicationObject
//public class SpriteManagerRockExtends {
//    private static final int                NB_SELECTOR_TILE = 4;
//
//    private static final int                TOP_LEFT = 0b10000000;
//    private static final int                TOP = 0b01000000;
//    private static final int                TOP_RIGHT = 0b00100000;
//    private static final int                LEFT = 0b00010000;
//    private static final int                RIGHT = 0b00001000;
//    private static final int                BOTTOM_LEFT = 0b00000100;
//    private static final int                BOTTOM = 0b00000010;
//    private static final int                BOTTOM_RIGHT = 0b00000001;
//
//    private int _spriteCount;
//
//    @Inject
//    private Data data;
//
//    public Sprite getRock(GraphicInfo graphicInfo, int tile) {
//        assert graphicInfo != null && graphicInfo.type == GraphicInfo.Type.TERRAIN;
//
//        if (graphicInfo.spriteId == -1) {
//            graphicInfo.spriteId = ++_spriteCount;
//        }
//
//        Sprite sprite = _sprites.get(getSum(graphicInfo.spriteId, tile, 0, 0));
//        if (sprite == null) {
//            Texture txTerrain = _manager.get(graphicInfo.path);
//            if (txTerrain != null) {
//                Pixmap pixmap = new Pixmap(Constant.TILE_SIZE, Constant.TILE_SIZE, Pixmap.Format.RGBA8888);
//
//                txTerrain.getTextureData().prepare();
//                Pixmap texturePixmap = txTerrain.getTextureData().consumePixmap();
//
//                // Top left
//                if ((tile & TOP_LEFT) > 0 && (tile & TOP) > 0 && (tile & LEFT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 0, 0, 16, 16, 16, 16);
//                } else if ((tile & TOP) > 0 && (tile & LEFT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 0, 0, 64, 32, 16, 16);
//                } else if ((tile & LEFT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 0, 0, 32, 0, 16, 16);
//                } else if ((tile & TOP) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 0, 0, 0, 32, 16, 16);
//                } else {
//                    pixmap.drawPixmap(texturePixmap, 0, 0, 0, 0, 16, 16);
//                }
//
//                // Top right
//                if ((tile & TOP_RIGHT) > 0 && (tile & TOP) > 0 && (tile & RIGHT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 16, 0, 16, 16, 16, 16);
//                } else if ((tile & TOP) > 0 && (tile & RIGHT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 16, 0, 80, 32, 16, 16);
//                } else if ((tile & RIGHT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 16, 0, 16, 0, 16, 16);
//                } else if ((tile & TOP) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 16, 0, 48, 32, 16, 16);
//                } else {
//                    pixmap.drawPixmap(texturePixmap, 16, 0, 48, 0, 16, 16);
//                }
//
//                // Bottom left
//                if ((tile & BOTTOM_LEFT) > 0 && (tile & BOTTOM) > 0 && (tile & LEFT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 0, 16, 16, 16, 16, 16);
//                } else if ((tile & BOTTOM) > 0 && (tile & LEFT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 0, 16, 64, 48, 16, 16);
//                } else if ((tile & LEFT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 0, 16, 32, 48, 16, 16);
//                } else if ((tile & BOTTOM) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 0, 16, 0, 16, 16, 16);
//                } else {
//                    pixmap.drawPixmap(texturePixmap, 0, 16, 0, 48, 16, 16);
//                }
//
//                // Bottom right
//                if ((tile & BOTTOM_RIGHT) > 0 && (tile & BOTTOM) > 0 && (tile & RIGHT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 16, 16, 16, 16, 16, 16);
//                } else if ((tile & BOTTOM) > 0 && (tile & RIGHT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 16, 16, 80, 48, 16, 16);
//                } else if ((tile & RIGHT) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 16, 16, 16, 48, 16, 16);
//                } else if ((tile & BOTTOM) > 0) {
//                    pixmap.drawPixmap(texturePixmap, 16, 16, 48, 16, 16, 16);
//                } else {
//                    pixmap.drawPixmap(texturePixmap, 16, 16, 48, 48, 16, 16);
//                }
//
//                txTerrain.getTextureData().disposePixmap();
//
//                sprite = new Sprite(new Texture(pixmap));
//                sprite.setFlip(false, true);
//                _sprites.put(getSum(graphicInfo.spriteId, tile, 0, 0), sprite);
//            }
//        }
//
//        return sprite;
//    }
//
//}