package org.smallbox.faraway.client.debug.dashboard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

import java.util.ArrayList;
import java.util.List;

public abstract class DashboardLayerBase {
    @Inject private AssetManager assetManager;

    private int index;
    private int max = 0;
    private long page = 0;
    private static final int ITEM_PER_PAGE = 50;

    public void draw(BaseRenderer renderer, int frame) {
        index = 0;

        onDraw(renderer, frame);

        if (index > 0) {
            renderer.drawText(10, 110, page + "/" + max / ITEM_PER_PAGE, Color.WHITE, 18, false, "sui", 1);
        }
    }

    protected abstract void onDraw(BaseRenderer renderer, int frame);

    protected void drawDebug(BaseRenderer renderer, String label, Object object) {
        if (index > page * ITEM_PER_PAGE && index < (page + 1) * ITEM_PER_PAGE) {
            renderer.drawText(10, (index % ITEM_PER_PAGE * 20) + 130, "[" + label.toUpperCase() + "] " + object, Color.WHITE, 18, false, "sui", 1);
        }
        max = Math.max(max, index++);
    }

    protected <T> List<T> assetToList(Class<T> cls) {
        Array<T> arrayOut = new Array<>();
        assetManager.getAll(cls, arrayOut);
        List<T> list = new ArrayList<>();
        for (T entry : arrayOut) {
            list.add(entry);
        }
        return list;
    }

    public void pageUp() {
        page = Math.max(page - 1, 0);
    }

    public void pageDown() {
        page++;
    }

}
