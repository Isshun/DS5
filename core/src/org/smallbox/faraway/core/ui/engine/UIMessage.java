package org.smallbox.faraway.core.ui.engine;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.ui.engine.views.UIFrame;
import org.smallbox.faraway.core.ui.engine.views.UILabel;

public class UIMessage {
    public int                 posX;
    public int                 posY;
    public int                 frame;
    public UILabel text;
    public UIFrame shape;
    public UIFrame border;

    public UIMessage(String str, int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.frame = 100;

        this.text = ViewFactory.getInstance().createTextView();
        this.text.setTextSize(14);
        this.text.setTextColor(Color.WHITE);
        this.text.setText(str);
        this.text.setPosition(posX + 10, posY + 5);

        this.border = new UIFrame(this.text.getContentWidth() + 20, this.text.getContentHeight() + 20);
        this.border.setBackgroundColor(new Color(255, 0, 0, 180));
        this.border.setPosition(posX, posY);

        this.shape = new UIFrame(this.text.getContentWidth() + 18, this.text.getContentHeight() + 18);
        this.shape.setBackgroundColor(new Color(0, 0, 0, 180));
        this.shape.setPosition(posX + 1, posY + 1);
    }
}
