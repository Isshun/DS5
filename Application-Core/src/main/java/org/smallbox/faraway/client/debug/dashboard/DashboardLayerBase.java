package org.smallbox.faraway.client.debug.dashboard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

import java.util.ArrayList;
import java.util.List;

public abstract class DashboardLayerBase {
    @Inject private AssetManager assetManager;

    private int index;
    private int max = 0;
    private long page = 0;
    private static final int ITEM_PER_PAGE = 50;

    public void draw(GDXRenderer renderer, int frame) {
        index = 0;

        onDraw(renderer, frame);

        if (index > 0) {
            renderer.drawTextUI(12, 112, 18, Color.BLACK, page + "/" + max / ITEM_PER_PAGE);
            renderer.drawTextUI(11, 111, 18, Color.BLACK, page + "/" + max / ITEM_PER_PAGE);
            renderer.drawTextUI(10, 110, 18, Color.WHITE, page + "/" + max / ITEM_PER_PAGE);
        }
    }

    protected abstract void onDraw(GDXRenderer renderer, int frame);

    protected void drawDebug(GDXRenderer renderer, String label, Object object) {
        if (index > page * ITEM_PER_PAGE && index < (page + 1) * ITEM_PER_PAGE) {
            renderer.drawTextUI(12, (index % ITEM_PER_PAGE * 20) + 132, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
            renderer.drawTextUI(11, (index % ITEM_PER_PAGE * 20) + 131, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
            renderer.drawTextUI(10, (index % ITEM_PER_PAGE * 20) + 130, 18, Color.WHITE, "[" + label.toUpperCase() + "] " + object);
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
