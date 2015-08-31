package org.smallbox.faraway.game.module;

import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 31/08/2015.
 */
public abstract class DebugWindow extends UITitleWindow {
    private int             _debugIndex;
    protected FrameLayout   _frameContent;

    public void addDebugView(String text) {
        addDebugView(text, null);
    }

    public void addDebugView(String text, OnClickListener clickListener) {
        UILabel lbCommand = ViewFactory.getInstance().createTextView();
        lbCommand.setString(text);
        lbCommand.setCharacterSize(14);
        lbCommand.setPosition(6, 20 * _debugIndex++);
        lbCommand.setSize(230, 20);
        lbCommand.setAlign(Align.CENTER_VERTICAL);
        lbCommand.setOnClickListener(clickListener);
        lbCommand.resetSize();
        _frameContent.addView(lbCommand);

        setSize(250, _debugIndex * 20 + 28);
    }
}
