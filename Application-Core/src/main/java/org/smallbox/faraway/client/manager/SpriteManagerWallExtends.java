package org.smallbox.faraway.client.manager;//package org.smallbox.faraway.client.manager;
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
//
//                else if (graphicInfo.type == GraphicInfo.Type.WALL) {
//                        Pixmap pixmap = new Pixmap(Constant.TILE_SIZE, Constant.TILE_SIZE, Pixmap.Format.RGBA8888);
//
//                        texture.getTextureData().prepare();
//                        Pixmap texturePixmap = texture.getTextureData().consumePixmap();
//
//                        // Top left
//                        if ((parcelTile & TOP_LEFT) > 0 && (parcelTile & TOP) > 0 && (parcelTile & LEFT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 0, 0, 32, 32, 16, 16);
//                        } else if ((parcelTile & TOP) > 0 && (parcelTile & LEFT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 0, 0, 64, 64, 16, 16);
//                        } else if ((parcelTile & LEFT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 0, 0, 64, 0, 16, 16);
//                        } else if ((parcelTile & TOP) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 0, 0, 64, 48, 16, 16);
//                        } else {
//                        pixmap.drawPixmap(texturePixmap, 0, 0, 0, 0, 16, 16);
//                        }
//
//                        // Top right
//                        if ((parcelTile & TOP_RIGHT) > 0 && (parcelTile & TOP) > 0 && (parcelTile & RIGHT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 16, 0, 32, 32, 16, 16);
//                        } else if ((parcelTile & TOP) > 0 && (parcelTile & RIGHT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 16, 0, 16, 64, 16, 16);
//                        } else if ((parcelTile & RIGHT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 16, 0, 16, 0, 16, 16);
//                        } else if ((parcelTile & TOP) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 16, 0, 80, 64, 16, 16);
//                        } else {
//                        pixmap.drawPixmap(texturePixmap, 16, 0, 80, 0, 16, 16);
//                        }
//
//                        // Bottom left
//                        if ((parcelTile & BOTTOM_LEFT) > 0 && (parcelTile & BOTTOM) > 0 && (parcelTile & LEFT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 0, 16, 32, 32, 16, 16);
//                        } else if ((parcelTile & BOTTOM) > 0 && (parcelTile & LEFT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 0, 16, 64, 16, 16, 16);
//                        } else if ((parcelTile & LEFT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 0, 16, 64, 80, 16, 16);
//                        } else if ((parcelTile & BOTTOM) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 0, 16, 64, 32, 16, 16);
//                        } else {
//                        pixmap.drawPixmap(texturePixmap, 0, 16, 0, 80, 16, 16);
//                        }
//
//                        // Bottom right
//                        if ((parcelTile & BOTTOM_RIGHT) > 0 && (parcelTile & BOTTOM) > 0 && (parcelTile & RIGHT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 16, 16, 32, 32, 16, 16);
//                        } else if ((parcelTile & BOTTOM) > 0 && (parcelTile & RIGHT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 16, 16, 16, 16, 16, 16);
//                        } else if ((parcelTile & RIGHT) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 16, 16, 16, 80, 16, 16);
//                        } else if ((parcelTile & BOTTOM) > 0) {
//                        pixmap.drawPixmap(texturePixmap, 16, 16, 80, 16, 16, 16);
//                        } else {
//                        pixmap.drawPixmap(texturePixmap, 16, 16, 80, 80, 16, 16);
//                        }
//
//                        texture.getTextureData().disposePixmap();
//
//                        sprite = new Sprite(new Texture(pixmap), 0, 0, 32, 32);
//                        sprite.setColor(new Color(255, 255, 255, alpha));
//                        sprite.setFlip(false, true);
//                        _sprites.put(sum, sprite);
//
////                    sprite = new Sprite(texture, (tile % 3) * 32, (tile / 3) * 32, 32, 32);
////                    sprite.setColor(new Color(255, 255, 255, alpha));
////                    _sprites.put(sum, sprite);
//                        }
//
//}