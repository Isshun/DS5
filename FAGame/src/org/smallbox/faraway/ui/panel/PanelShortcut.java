package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.ResourceData;
import org.smallbox.faraway.game.manager.ResourceManager;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PanelShortcut extends BaseRightPanel {
    private static final int NB_COLUMNS_STATS = NB_COLUMNS / 2 - 1;
    private static final int NB_RESOURCE_MAX = 10;
    private ResourceManager _resourceManager;

    private static class ResourceEntry {
        public ResourceData	data;
        public TextView		text;
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
            new PanelEntry("bt_debug",      "[ EBUG]", 		"D", 	1, Mode.DEBUG),
            new PanelEntry("bt_plan",       "[ LAN]", 		"P",	1, Mode.PLAN),
            new PanelEntry("bt_manage",     "[ ANAGE]", 	"M",	1, Mode.MANAGER),
            new PanelEntry("bt_stats",      "[ TATS]", 		"S",	1, Mode.STATS),
            new PanelEntry("bt_area",       "[ AREAS]",     "A",	1, Mode.AREA)
    };

    // TODO
//	private MiniMapRenderer		_miniMapRenderer;
    private TextView 			_lbTime;
    private List<ResourceEntry> _resources;
    private ImageView _map;

    public PanelShortcut(Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/shortcut.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        _lbTime = (TextView) findById("lb_time");

        _resourceManager = (ResourceManager)Game.getInstance().getManager(ResourceManager.class);
        _resources = new ArrayList<>();
        addResource((TextView) findById("lb_food"), _resourceManager.getFood());
        addResource((TextView) findById("lb_water"), _resourceManager.getWater());
        addResource((TextView) findById("lb_gas"), _resourceManager.getGasoline());
        addResource((TextView) findById("lb_science"), _resourceManager.getScience());
        addResource((TextView) findById("lb_o2"), _resourceManager.getO2());
        addResource((TextView) findById("lb_power"), _resourceManager.getPower());

        for (PanelEntry entry : _entries) {
            findById(entry.buttonId).setOnClickListener(view -> {
                _ui.toggleMode(entry.mode);
            });
            findById(entry.buttonId).setBackgroundColor(new Color(29, 85, 96, 100));
            findById(entry.buttonId).setOnFocusListener(new OnFocusListener() {
                @Override
                public void onEnter(View view) {
                    view.setBackgroundColor(new Color(29, 85, 96, 180));
                }

                @Override
                public void onExit(View view) {
                    view.setBackgroundColor(new Color(29, 85, 96, 100));
                }
            });
        }
    }

    @Override
    protected void onCreate(ViewFactory factory) {
////		_miniMapRenderer = new MiniMapRenderer(_effect);
//		int posX = 24;
//		int posY = 244;
//
//		_map = ViewFactory.getInstance().createImageView();
//		_map.setPosition(20, 20);
//		addView(_map);
    }

    private void addResource(final TextView text, final ResourceData data) {
        ResourceEntry res = new ResourceEntry();
        res.data = data;
        res.text = text;
        res.text.setOnClickListener(view -> _ui.select(data.tooltip));
        _resources.add(res);
    }

    // TODO
//	@Override
//	public void onDraw(GFXRenderer renderer, RenderEffect effect) {
////		_miniMapRenderer.onDraw(renderer, effect, 0);
//	}

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
