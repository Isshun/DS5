//package org.smallbox.faraway.ui.panel;
//
//import org.smallbox.faraway.core.engine.Color;
//import org.smallbox.faraway.core.engine.GameEventListener;
//import org.smallbox.faraway.ui.LinkFocusListener;
//import org.smallbox.faraway.ui.UserInteraction;
//import org.smallbox.faraway.ui.UserInterface;
//import org.smallbox.faraway.ui.UserInterface.Mode;
//import org.smallbox.faraway.ui.engine.Colors;
//import org.smallbox.faraway.ui.engine.LayoutFactory;
//import org.smallbox.faraway.ui.engine.OnClickListener;
//import org.smallbox.faraway.ui.engine.ViewFactory;
//import org.smallbox.faraway.ui.engine.views.UIFrame;
//import org.smallbox.faraway.ui.engine.views.UILabel;
//import org.smallbox.faraway.ui.engine.views.View;
//import org.smallbox.faraway.core.util.Constant;
//
//public abstract class BaseRightPanel extends BasePanel {
//    protected static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
//    protected static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
//
//    public BaseRightPanel(Mode mode, GameEventListener.Key shortcut) {
//        super(mode, shortcut, Constant.WINDOW_WIDTH - FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT, null);
//        setSize(FRAME_WIDTH, FRAME_HEIGHT);
//    }
//
//    public BaseRightPanel(Mode mode, GameEventListener.Key shortcut, String layoutPath) {
//        super(mode, shortcut, layoutPath);
//        setSize(FRAME_WIDTH, FRAME_HEIGHT);
//    }
//
//    @Override
//    public void init(ViewFactory viewFactory, LayoutFactory factory, UserInterface ui, UserInteraction interaction) {
//        super.init(viewFactory, factory, ui, interaction);
//
//        setBackgroundColor(Colors.BACKGROUND);
//        View border = new UIFrame(4, FRAME_HEIGHT);
//        border.setBackgroundColor(Colors.BORDER);
//        addView(border);
//
//        if (_mode != Mode.NONE) {
//            UILabel lbBack = ViewFactory.getInstance().createTextView();
//            lbBack.setText("[Back]");
//            lbBack.setTextSize(FONT_SIZE_TITLE);
//            lbBack.setTextColor(Colors.LINK_INACTIVE);
//            lbBack.setPosition(22, -22);
//            lbBack.setSize(120, 32);
//            lbBack.setTextAlign(Align.CENTER);
//            lbBack.setBackgroundColor(new Color(0x1d5560));
////            lbBack.setOnClickListener(view -> _ui.back());
//            lbBack.setOnFocusListener(new LinkFocusListener());
//            addView(lbBack);
//        }
//    }
//
//    protected void addDebugView(String text, int x, int y, OnClickListener clickListener) {
//        UILabel lbCommand = ViewFactory.getInstance().createTextView();
//        lbCommand.setText("[DEV] " + text);
//        lbCommand.setTextSize(14);
//        lbCommand.setPosition(x, y);
//        lbCommand.setOnClickListener(clickListener);
//        addView(lbCommand);
//    }
//
//}
