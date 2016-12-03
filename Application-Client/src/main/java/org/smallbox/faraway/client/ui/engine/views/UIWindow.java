//package org.smallbox.faraway.ui.engine.views;
//
//import org.smallbox.faraway.core.engine.GameEventListener;
//import OnClickListener;
//import UIFrame;
//import UILabel;
//
///**
// * Created by Alex on 31/08/2015.
// */
//public abstract class UIWindow extends UIFrame {
//    protected UIFrame _frameMain;
//    protected UIFrame _frameContent;
//    private boolean             _isLoaded;
//    private int                 _debugIndex;
//
//    public UIWindow() {
//        _frameContent = this;
//        _isLoaded = true;
//    }
//
//    protected abstract void onGameInit(UIWindow window, UIFrame content);
//    protected abstract void onRefresh(int updateGame);
//    protected abstract String getContentLayout();
//
//    public void createModules() {
//        onGameInit(this, _frameContent);
//    }
//
//    public void onDisplayMultiple(int updateGame) {
//        if (_isVisible && _isLoaded) {
//            onRefresh(updateGame);
//        }
//    }
//
//    @Override
//    public int getContentWidth() {
//        return 400;
//    }
//
//    @Override
//    public int getContentHeight() {
//        return 400;
//    }
//
//    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
//        return false;
//    }
//
//    protected void addDebugView(UIFrame frame, String text) {
//        addDebugView(frame, text, null);
//    }
//
//    protected void addDebugView(UIFrame frame, String text, OnClickListener clickListener) {
//        UILabel lbCommand = new UILabel();
//        lbCommand.setText(text);
//        lbCommand.setTextSize(14);
//        lbCommand.setPosition(6, 38 + 20 * _debugIndex++);
//        lbCommand.setSize(230, 20);
//        lbCommand.setOnClickListener(clickListener);
//        frame.addView(lbCommand);
//    }
//
//}
