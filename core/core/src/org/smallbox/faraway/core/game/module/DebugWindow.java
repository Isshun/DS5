package org.smallbox.faraway.core.game.module;

import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UILabel;

/**
 * Created by Alex on 31/08/2015.
 */
public abstract class DebugWindow extends UITitleWindow {
    private int             _debugIndex;
    protected UIFrame _frameContent;

    public void addDebugView(String text) {
        addDebugView(text, null);
    }

    public void addDebugView(String text, OnClickListener clickListener) {
        UILabel lbCommand = ViewFactory.getInstance().createTextView();
        lbCommand.setText(text);
        lbCommand.setTextSize(14);
        lbCommand.setPosition(6, 20 * _debugIndex++);
        lbCommand.setSize(230, 20);
        lbCommand.setTextAlign(Align.CENTER_VERTICAL);
        lbCommand.setOnClickListener(clickListener);
        _frameContent.addView(lbCommand);

        setSize(250, _debugIndex * 20 + 28);
    }
}
