//package org.smallbox.faraway.ui.panel;
//
//import org.smallbox.faraway.core.engine.GameEventListener;
//import org.smallbox.faraway.core.game.model.GameData;
//import org.smallbox.faraway.core.game.module.world.model.ItemInfo;
//import org.smallbox.faraway.ui.UserInterface.Mode;
//import org.smallbox.faraway.ui.engine.Colors;
//import org.smallbox.faraway.ui.engine.ViewFactory;
//import org.smallbox.faraway.ui.engine.views.UIFrame;
//import org.smallbox.faraway.ui.engine.views.UILabel;
//import org.smallbox.faraway.core.util.Log;
//import org.smallbox.faraway.core.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PanelManager extends BaseRightPanel {
//    public class PanelEntry {
//        public String label;
//        public PanelEntry(String label) {
//            this.label = label;
//        }
//    }
//
//    private UILabel[]             _labels;
//    private UILabel[]             _shortcuts;
//    private String                 _search = "";
//
//    private List<PanelEntry> _entries;
//    private UILabel _lbSearch;
//
//    private UIFrame _cursor;
//    private int                _line;
//
//    private int             _nbResults;
//    private int             _nbEntries;
//
//    public PanelManager(Mode mode, GameEventListener.Key shortcut) {
//        super(mode, shortcut);
//    }
//
//    @Override
//    protected void onCreate(ViewFactory factory) {
//        _entries = new ArrayList<>();
//        List<ItemInfo> items = GameData.getData().items;
//        for (ItemInfo item: items) {
//            if (item.isFood && item.receipts != null) {
//                _entries.add(new PanelEntry("Cooking " + item.label));
//            }
//        }
//        _entries.add(new PanelEntry("Preparing medicinal"));
//        _entries.add(new PanelEntry("Making beverages"));
//        _entries.add(new PanelEntry("Making alcoholic beverages"));
//        _entries.add(new PanelEntry("Making fertilizer"));
//        _nbEntries = _entries.size();
//
//        createView(factory);
//    }
//
//    private void createView(ViewFactory factory) {
//        _cursor = new UIFrame(8, 16);
//        _cursor.setBackgroundColor(Colors.LINK_INACTIVE);
//        _cursor.setPosition(86, 60);
//        addView(_cursor);
//
//        UILabel lbOrder = ViewFactory.getInstance().createTextView();
//        lbOrder.setText("Select order");
//        lbOrder.setTextSize(FONT_SIZE_TITLE);
//        lbOrder.setPosition(20, 20);
//        addView(lbOrder);
//
//        _lbSearch = ViewFactory.getInstance().createTextView();
//        _lbSearch.setPosition(20, 60);
//        _lbSearch.setText("search: ");
//        _lbSearch.setTextSize(FONT_SIZE);
//        _lbSearch.setTextColor(Colors.LINK_INACTIVE);
//        addView(_lbSearch);
//
//        _labels = new UILabel[_nbEntries];
//        _shortcuts = new UILabel[_nbEntries];
//        for (int i = 0; i < _nbEntries; i++) {
//            _labels[i] = ViewFactory.getInstance().createTextView();
//            _labels[i].setPosition(20, 100 + i * LINE_HEIGHT);
//            _labels[i].setTextSize(FONT_SIZE);
//            addView(_labels[i]);
//
//            _shortcuts[i] = ViewFactory.getInstance().createTextView();
//            _shortcuts[i].setPosition(20, 100 + i * LINE_HEIGHT);
//            _shortcuts[i].setTextSize(FONT_SIZE);
//            _shortcuts[i].setTextColor(Colors.LINK_ACTIVE);
//            // TODO
//            //_shortcuts[i].setStyle(TextView.UNDERLINED);
//            addView(_shortcuts[i]);
//        }
//    }
//
//    @Override
//    public void onRefresh(int frame) {
//        if (frame != 0 && frame % 2 == 0) {
//            _cursor.setVisible(!_cursor.isVisible());
//        }
//
//        int i = 0;
//        Log.info(String.valueOf(_search.length()));
//        for (PanelEntry entry: _entries) {
//            if (_search.length() == 0) {
//                if (i == _line) {
//                    _shortcuts[i].setVisible(true);
//                    _shortcuts[i].setText(entry.label);
//                    _shortcuts[i].setPosition(20, _shortcuts[i].getPosY());
//                    _shortcuts[i].setTextColor(Colors.LINK_ACTIVE);
//                    _labels[i].setVisible(false);
//                }
//                else {
//                    _shortcuts[i].setVisible(false);
//                    _labels[i].setVisible(true);
//                    _labels[i].setText(entry.label);
//                }
//                i++;
//            }
//
//            else {
//                int pos = entry.label.toLowerCase().indexOf(_search);
//                if (pos != -1) {
//                    if (i == _line) {
//                        _shortcuts[i].setText(entry.label);
//                        _shortcuts[i].setTextColor(Colors.LINK_ACTIVE);
//                        _shortcuts[i].setPosition(20, _shortcuts[i].getPosY());
//                        _labels[i].setVisible(false);
//                    }
//                    else {
//                        _shortcuts[i].setText(entry.label.substring(pos, pos + _search.length()));
//                        _shortcuts[i].setTextColor(Colors.LINK_INACTIVE);
//                        _shortcuts[i].setPosition(20 + pos * 8, _shortcuts[i].getPosY());
//                        _labels[i].setVisible(true);
//                        _labels[i].setText(entry.label);
//                    }
//                    _shortcuts[i].setVisible(true);
//                    i++;
//                }
//            }
//        }
//        _nbResults = i - 1;
//        for (; i < _nbEntries; i++) {
//            _shortcuts[i].setVisible(false);
//            _labels[i].setVisible(false);
//        }
//    }
//
//    @Override
//    public boolean    onKey(GameEventListener.Key key) {
//        if (key == GameEventListener.Key.UP) {
//            _line = _line - 1 < 0 ? _nbResults : _line - 1;
//        }
//        else if (key == GameEventListener.Key.DOWN) {
//            _line = _line + 1 > _nbResults ? 0 : _line + 1;
//        }
//        else if (key == GameEventListener.Key.BACKSPACE) {
//            if (_search.length() > 0) {
//                _search = _search.substring(0, _search.length() - 1);
//            }
//        } else {
//            String str = StringUtils.getStringFromKey(key);
//            if (str != null) {
//                _search += str;
//            }
//        }
//        _lbSearch.setText("search: " + _search);
//        _cursor.setPosition(86 + (_search.length() * 8), _cursor.getPosY());
//        onRefresh(0);
//        return true;
//    }
//}
