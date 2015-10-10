package org.smallbox.faraway.module.panels;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.module.extra.ResourceModule;
import org.smallbox.faraway.ui.LinkFocusListener;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UIImage;
import org.smallbox.faraway.ui.engine.views.UILabel;
import org.smallbox.faraway.ui.engine.views.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 01/09/2015.
 */
public class PanelModule extends GameUIModule {
    private static final int NB_COLUMNS_STATS = 21;

    private static class ResourceEntry {
        public ResourceModule.ResourceData data;
        public UILabel text;
    }

    private static class PanelEntry {
        final String                buttonId;
        final UserInterface.Mode    mode;
        final String	            label;
        final String 	            shortcut;
        final int 		            shortcutPos;

        public PanelEntry(String buttonId, String label, String shortcut, int shortcutPos, UserInterface.Mode mode) {
            this.buttonId = buttonId;
            this.shortcut = shortcut;
            this.label = label;
            this.mode = mode;
            this.shortcutPos = shortcutPos;
        }
    }

    private PanelModuleWindow _window;

    private class PanelModuleWindow extends UIWindow {
        private int         _nbShortcut;
        private UILabel     _lbClock;
        private int         _hour;
        private int         _day;
        private int         _year;

        // TODO
        //	private MiniMapRenderer		_miniMapRenderer;
        private List<ResourceEntry> _resources;
        private UIImage             _map;
        private ResourceModule      _resourceModule;

        private PanelEntry	_entries[] = {
                new PanelEntry("bt_build",      "[ UILD]", 	    "B", 	1, UserInterface.Mode.BUILD),
                new PanelEntry("bt_occupation", "[ CCUPATION]", "O",	1, UserInterface.Mode.JOBS),
                new PanelEntry("bt_crew",       "[ REW]", 		"C", 	1, UserInterface.Mode.CREW),
                new PanelEntry("bt_room",       "[ OOM]", 		"R", 	1, UserInterface.Mode.ROOM),
                new PanelEntry("bt_plan",       "[ LAN]", 		"P",	1, UserInterface.Mode.PLAN),
                new PanelEntry("bt_manage",     "[ ANAGE]", 	"M",	1, UserInterface.Mode.MANAGER),
                new PanelEntry("bt_stats",      "[ TATS]", 		"S",	1, UserInterface.Mode.STATS),
                new PanelEntry("bt_area",       "[ AREAS]",     "A",	1, UserInterface.Mode.AREA)
        };
        private UIFrame _content;

        @Override
        protected void onCreate(UIWindow window, UIFrame content) {
            _content = content;
            _lbClock = (UILabel) content.findById("lb_clock");

            _resourceModule = (ResourceModule) ModuleManager.getInstance().getModule(ResourceModule.class);

            _resources = new ArrayList<>();
            addResource((UILabel) findById("lb_food"),      _resourceModule.getFood());
            addResource((UILabel) findById("lb_water"),     _resourceModule.getWater());
            addResource((UILabel) findById("lb_gas"),       _resourceModule.getGasoline());
            addResource((UILabel) findById("lb_science"),   _resourceModule.getScience());
            addResource((UILabel) findById("lb_o2"),        _resourceModule.getO2());
            addResource((UILabel) findById("lb_power"),     _resourceModule.getPower());

            for (PanelEntry entry : _entries) {
                View button = findById(entry.buttonId);
                if (button != null) {
//                    findById(entry.buttonId).setOnClickListener(view -> UserInterface.getInstance().toggleMode(entry.mode));
                    findById(entry.buttonId).setBackgroundColor(new Color(29, 85, 96, 100));
                    findById(entry.buttonId).setOnFocusListener(new LinkFocusListener());
                }
            }
        }

        @Override
        protected void onRefresh(int update) {
//// TODO
////        if (_miniMapRenderer != null) {
////            _miniMapRenderer.onDraw(update);
////            _map.setSprite(_miniMapRenderer.getSprite());
////        }
//
            //update * 1000000

//            Calendar cal = Calendar.getInstance();
//            cal.set(2175, Calendar.JANUARY, 1, 0, 0);
//            cal.add(Calendar.HOUR, update);
//
//            Date date = cal.getTime();
//            DateFormat formater = new SimpleDateFormat("dd MMMM y");
//            _lbTime.setText(formater.format(date));
//            _lbTime.setVisible(false);

            for (ResourceEntry res: _resources) {
                if (res != null) {
                    res.text.setDashedString(res.data.label, String.valueOf(res.data.value), NB_COLUMNS_STATS);
                }
            }

            _lbClock.setText("Day: " + _day + ", year: " + _year);
        }

        @Override
        protected String getContentLayout() {
            return "panels/shortcut";
        }

        private void addResource(final UILabel text, final ResourceModule.ResourceData data) {
            ResourceEntry res = new ResourceEntry();
            res.data = data;
            res.text = text;
            res.text.setOnClickListener(view -> UserInterface.getInstance().getSelector().select(data.tooltip));
            _resources.add(res);
        }

        public void setHour(int hour) {
            _hour = hour;
        }

        public void setDay(int day) {
            _day = day;
        }

        public void setYear(int year) {
            _year = year;
        }

        public void addShortcut(String label, UIWindow window) {
            UILabel btShortcut = new UILabel();
            btShortcut.setSize(165, 45);
            btShortcut.setText(label);
            btShortcut.setTextSize(18);
            btShortcut.setTextColor(new Color(0x78ffff));
            btShortcut.setBackgroundColor(new Color(0x1d5560));
            btShortcut.setTextAlign(Align.CENTER);
            btShortcut.setPosition(_nbShortcut % 2 == 0 ? 24 : 215, (_nbShortcut / 2) * 70);
            btShortcut.setOnClickListener(view -> {
                _windows.forEach(w -> w.setVisible(false));
                window.setVisible(true);
            });
            ((UIFrame) _content.findById("shortcut_entries")).addView(btShortcut);
            _nbShortcut++;
        }
    }

    @Override
    protected void onLoaded() {
        _window = new PanelModuleWindow();
        addWindow(_window);
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public void onHourChange(int hour) {
        _window.setHour(hour);
    }

    @Override
    public void onDayChange(int day) {
        _window.setDay(day);
    }

    @Override
    public void onYearChange(int year) {
        _window.setYear(year);
    }

    @Override
    public void onSelectCharacter(CharacterModel character) {
        _window.setVisible(false);
    }

    @Override
    public void onSelectParcel(ParcelModel parcel) {
        _window.setVisible(false);
    }

    @Override
    public void onSelectItem(ItemModel item) {
        _window.setVisible(false);
    }

    @Override
    public void onSelectResource(ResourceModel resource) {
        _window.setVisible(false);
    }

    @Override
    public void onSelectConsumable(ConsumableModel consumable) {
        _window.setVisible(false);
    }

    @Override
    public void onSelectStructure(StructureModel structure) {
        _window.setVisible(false);
    }

    @Override
    public void onDeselect() {
        _window.setVisible(true);
    }

    public void addShortcut(String label, UIWindow window) {
        _window.addShortcut(label, window);
        window.setVisible(false);
        super.addWindow(window);
    }

    @Override
    public boolean onKey(GameEventListener.Key key) {
        if (key == GameEventListener.Key.ESCAPE) {
            _windows.forEach(window -> window.setVisible(false));
            _window.setVisible(true);
            return true;
        }
        return false;
    }

}
