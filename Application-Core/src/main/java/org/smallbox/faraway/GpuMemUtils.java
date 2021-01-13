package org.smallbox.faraway;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

import java.lang.reflect.Field;
import java.util.Map;

@ApplicationObject
public class GpuMemUtils {

    public static class GpuMemInfo {
        public int totalTextures;
        public int gpuMemSize;
    }

    private GpuMemUtils.GpuMemInfo gpuMemInfo;

    public GpuMemInfo getGpuMemInfo() {
        return gpuMemInfo;
    }

    public void getTextureGpuSize() {

        try {
            Field managedTexturesField = Texture.class.getDeclaredField("managedTextures");
            managedTexturesField.setAccessible(true);
            Map<Application, Array<Texture>> managedTexturesPerApp = (Map<Application, Array<Texture>>) managedTexturesField.get(null);
            Array<Texture> managedTextures = managedTexturesPerApp.get(Gdx.app);

            int totalTextureSize = 0;
            for (Texture texture : managedTextures) {
                int width = texture.getWidth();
                int height = texture.getHeight();
                Pixmap.Format format = texture.getTextureData().getFormat();
                boolean useMipMaps = texture.getTextureData().useMipMaps();

                int bytesPerPixel = getBytesPerPixel(format);

                int textureSize = (int) (width * height * bytesPerPixel * (useMipMaps ? 1.33333f : 1));

                totalTextureSize += textureSize;
            }
            gpuMemInfo = new GpuMemInfo();
            gpuMemInfo.totalTextures = managedTextures.size;
            gpuMemInfo.gpuMemSize = totalTextureSize;
        } catch (Exception e) {
            throw new RuntimeException("Error while getting textures gpu memory use", e);
        }
    }

    static public int getBytesPerPixel(Pixmap.Format format) {
        switch (format) {
            case Alpha:
            case Intensity:
            case LuminanceAlpha:
                return 1;
            case RGB565:
            case RGBA4444:
                return 2;
            case RGB888:
                return 3;
            case RGBA8888:
                return 4;
            default:
                return 4;
        }
    }
}
