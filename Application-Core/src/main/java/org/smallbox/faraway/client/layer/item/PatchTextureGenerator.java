package org.smallbox.faraway.client.layer.item;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.smallbox.faraway.util.Constant.HALF_TILE_SIZE;

@ApplicationObject
public class PatchTextureGenerator {
    @Inject private AssetManager assetManager;

    private Map<String, PatchTextureGeneratorMapper> mappers;
    private Map<String, Texture> textures;

    @OnInit
    private void init() {
        textures = new ConcurrentHashMap<>();

        mappers = new ConcurrentHashMap<>();
        mappers.put("tl_oc", new PatchTextureGeneratorMapper((x, y) -> x, (x, y) -> y));
        mappers.put("tl_ic", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE + HALF_TILE_SIZE - x, (x, y) -> HALF_TILE_SIZE + HALF_TILE_SIZE - y));
        mappers.put("tl_hb", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE + x, (x, y) -> y));
        mappers.put("tl_vb", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE + y, (x, y) -> x));
        mappers.put("tr_oc", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE - x, (x, y) -> y));
        mappers.put("tr_ic", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE + x, (x, y) -> HALF_TILE_SIZE + HALF_TILE_SIZE - y));
        mappers.put("tr_hb", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE + x, (x, y) -> y));
        mappers.put("tr_vb", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE - x, (x, y) -> HALF_TILE_SIZE + HALF_TILE_SIZE - y));
        mappers.put("bl_oc", new PatchTextureGeneratorMapper((x, y) -> x, (x, y) -> HALF_TILE_SIZE - y));
        mappers.put("bl_ic", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE + HALF_TILE_SIZE - x, (x, y) -> HALF_TILE_SIZE + y));
        mappers.put("bl_hb", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE + x, (x, y) -> HALF_TILE_SIZE - y));
        mappers.put("bl_vb", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE + y, (x, y) -> x));
        mappers.put("br_oc", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE - x, (x, y) -> HALF_TILE_SIZE - y));
        mappers.put("br_ic", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE + x, (x, y) -> HALF_TILE_SIZE + y));
        mappers.put("br_hb", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE - y, (x, y) -> HALF_TILE_SIZE + x));
        mappers.put("br_vb", new PatchTextureGeneratorMapper((x, y) -> HALF_TILE_SIZE - x, (x, y) -> HALF_TILE_SIZE + y));
    }

    public Texture getOrCreateTexture(GraphicInfo graphic, int glue) {
        List<String> keys = buildKeys(glue);
        String masterKey = String.join("-", keys);

        if (!textures.containsKey(masterKey)) {
            createTexture(graphic, masterKey, keys);
        }

        return textures.get(masterKey);
    }

    private void createTexture(GraphicInfo graphic, String masterKey, List<String> keys) {
        assetManager.temporaryPixmapFromTexture(graphic.absolutePath, pixmapIn -> {
            assetManager.temporaryPixmap(Constant.TILE_SIZE, Constant.TILE_SIZE, Pixmap.Format.RGBA8888, pixmapOut -> {
                keys.forEach(key -> fillPixmap(key, pixmapIn, pixmapOut));

                Texture texture = new Texture(pixmapOut);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                textures.put(masterKey, texture);
            });
        });
    }

    private void fillPixmap(String key, Pixmap pixmapIn, Pixmap pixmap) {
        for (int x = 0; x < HALF_TILE_SIZE; x++) {
            for (int y = 0; y < HALF_TILE_SIZE; y++) {
                PatchTextureGeneratorMapper mapper = mappers.get(key);
                int pixel = pixmapIn.getPixel(mapper.getX(x, y), mapper.getY(x, y));
                pixmap.drawPixel(
                        x + (key.startsWith("tr") || key.startsWith("br") ? HALF_TILE_SIZE : 0),
                        Constant.TILE_SIZE - y - (key.startsWith("bl") || key.startsWith("br") ? HALF_TILE_SIZE : 0),
                        pixel);
            }
        }
    }

    private List<String> buildKeys(int glue) {
        List<String> keys = new ArrayList<>();

        if ((glue & 0b1001) == 0) keys.add("tl_oc");
        if ((glue & 0b1001) == 0b1001) keys.add("tl_ic");
        if ((glue & 0b0001) == 0 && (glue & 0b1000) == 0b1000) keys.add("tl_hb");
        if ((glue & 0b1000) == 0 && (glue & 0b0001) == 0b0001) keys.add("tl_vb");
        if ((glue & 0b0011) == 0) keys.add("tr_oc");
        if ((glue & 0b0011) == 0b0011) keys.add("tr_ic");
        if ((glue & 0b0001) == 0 && (glue & 0b0010) == 0b0010) keys.add("tr_hb");
        if ((glue & 0b0010) == 0 && (glue & 0b0001) == 0b0001) keys.add("tr_vb");
        if ((glue & 0b1100) == 0) keys.add("bl_oc");
        if ((glue & 0b1100) == 0b1100) keys.add("bl_ic");
        if ((glue & 0b0100) == 0 && (glue & 0b1000) == 0b1000) keys.add("bl_hb");
        if ((glue & 0b1000) == 0 && (glue & 0b0100) == 0b0100) keys.add("bl_vb");
        if ((glue & 0b0110) == 0) keys.add("br_oc");
        if ((glue & 0b0110) == 0b0110) keys.add("br_ic");
        if ((glue & 0b0100) == 0 && (glue & 0b0010) == 0b0010) keys.add("br_hb");
        if ((glue & 0b0010) == 0 && (glue & 0b0100) == 0b0100) keys.add("br_vb");

        return keys;
    }

}
