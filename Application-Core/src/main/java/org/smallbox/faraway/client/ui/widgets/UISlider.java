package org.smallbox.faraway.client.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.core.module.ModuleBase;
import org.smallbox.faraway.util.Utils;

public class UISlider extends CompositeView {
    public Color handleBackground;
    public int handleWidth;
    public int handleHeight;
    private int positionX;
    public float value;

    public UISlider(ModuleBase module) {
        super(module);
    }

    @Override
    protected void onAddView(View view) {
    }

    @Override
    protected void onRemoveView(View view) {
    }

    @Override
    public void draw(BaseRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {

            if (positionX == 0) {
                positionX = geometry.getFinalX();
            }

//            int position = (int) (geometry.getFinalX() + value * (getWidth() - handleWidth));
            renderer.drawRectangle(positionX, geometry.getFinalY() + getHeight() / 2 - handleHeight / 2, handleWidth, handleHeight, handleBackground);

            if (_views != null) {
                UILabel label = (UILabel) _views.iterator().next();
                label.setText(String.valueOf(Math.round(value * 100)));
                label.draw(renderer, getAlignedX() + x, getAlignedY() + y);
            }

        }

    }

    @Override
    public int getContentWidth() {
        return 0;
    }

    @Override
    public int getContentHeight() {
        return 0;
    }

    public void update(int dragX) {
        positionX = Utils.bound(geometry.getFinalX(), geometry.getFinalX() + geometry.getWidth() - handleWidth, dragX - handleWidth / 2);
        value = ((float) positionX - geometry.getFinalX()) / (geometry.getWidth() - handleWidth);
    }

    public void setValue(float value) {
        this.value = value;
        positionX = (int) (geometry.getFinalX() + value * (geometry.getWidth() - handleWidth));
    }
}
