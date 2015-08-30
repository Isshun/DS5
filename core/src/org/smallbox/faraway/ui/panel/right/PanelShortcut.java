package org.smallbox.faraway.ui.panel.right;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.extra.ResourceModule;
import org.smallbox.faraway.game.module.extra.ResourceModule.ResourceData;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.LinkFocusListener;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UIImage;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;
import org.smallbox.faraway.ui.panel.BaseRightPanel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PanelShortcut extends BaseRightPanel {
    private static final int NB_COLUMNS_STATS = 21;

    private ResourceModule _resourceModule;

    private static class ResourceEntry {
        public ResourceData data;
        public UILabel text;
    }

    private static class PanelEntry {
        final String    buttonId;
        final Mode      mode;
        final String	label;
        final String 	shortcut;
        final int 		shortcutPos;

        public PanelEntry(String buttonId, String label, String shortcut, int shortcutPos, Mode mode) {
            this.buttonId = buttonId;
            this.shortcut = shortcut;
            this.label = label;
            this.mode = mode;
            this.shortcutPos = shortcutPos;
        }
    }

    private PanelEntry	_entries[] = {
            new PanelEntry("bt_build",      "[ UILD]", 	    "B", 	1, Mode.BUILD),
            new PanelEntry("bt_occupation", "[ CCUPATION]", "O",	1, Mode.JOBS),
            new PanelEntry("bt_crew",       "[ REW]", 		"C", 	1, Mode.CREW),
            new PanelEntry("bt_room",       "[ OOM]", 		"R", 	1, Mode.ROOM),
            new PanelEntry("bt_plan",       "[ LAN]", 		"P",	1, Mode.PLAN),
            new PanelEntry("bt_manage",     "[ ANAGE]", 	"M",	1, Mode.MANAGER),
            new PanelEntry("bt_stats",      "[ TATS]", 		"S",	1, Mode.STATS),
            new PanelEntry("bt_area",       "[ AREAS]",     "A",	1, Mode.AREA)
    };

    // TODO
//	private MiniMapRenderer		_miniMapRenderer;
    private UILabel _lbTime;
    private List<ResourceEntry> _resources;
    private UIImage _map;

    public PanelShortcut(Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/shortcut.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
        _lbTime = (UILabel) findById("lb_time");

        _resourceModule = (ResourceModule)Game.getInstance().getModule(ResourceModule.class);

        _resources = new ArrayList<>();
        addResource((UILabel) findById("lb_food"), _resourceModule.getFood());
        addResource((UILabel) findById("lb_water"), _resourceModule.getWater());
        addResource((UILabel) findById("lb_gas"), _resourceModule.getGasoline());
        addResource((UILabel) findById("lb_science"), _resourceModule.getScience());
        addResource((UILabel) findById("lb_o2"), _resourceModule.getO2());
        addResource((UILabel) findById("lb_power"), _resourceModule.getPower());

        for (PanelEntry entry : _entries) {
            View button = findById(entry.buttonId);
            if (button != null) {
                findById(entry.buttonId).setOnClickListener(view -> _ui.toggleMode(entry.mode));
                findById(entry.buttonId).setBackgroundColor(new Color(29, 85, 96, 100));
                findById(entry.buttonId).setOnFocusListener(new LinkFocusListener());
            }
        }
    }

    private void addResource(final UILabel text, final ResourceData data) {
        ResourceEntry res = new ResourceEntry();
        res.data = data;
        res.text = text;
        res.text.setOnClickListener(view -> _ui.getSelector().select(data.tooltip));
        _resources.add(res);
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
		Calendar cal = Calendar.getInstance();
		cal.set(2175, Calendar.JANUARY, 1, 0, 0);
		cal.add(Calendar.HOUR, update);

		Date date = cal.getTime();
		DateFormat formater = new SimpleDateFormat("dd MMMM y");
		_lbTime.setString(formater.format(date));
		_lbTime.setVisible(false);

        for (ResourceEntry res: _resources) {
            if (res != null) {
                res.text.setDashedString(res.data.label, String.valueOf(res.data.value), NB_COLUMNS_STATS);
            }
        }
    }
}
