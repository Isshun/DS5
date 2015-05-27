package org.smallbox.faraway.ui.panel;

import org.jsfml.window.Keyboard.Key;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.manager.ResourceData;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.renderer.MiniMapRenderer;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PanelShortcut extends BaseRightPanel {
	private static final int NB_COLUMNS_STATS = NB_COLUMNS / 2 - 1;
	private static final int NB_RESOURCE_MAX = 10;

	private static class ResourceEntry {
		public ResourceData	data;
		public TextView		text;
	}
	
	private static class PanelEntry {
		Mode		mode;
		String		label;
		String 		shortcut;
		int 		shortcutPos;

		public PanelEntry(String label, String shortcut, int shortcutPos, Mode mode) {
			this.shortcut = shortcut;
			this.label = label;
			this.mode = mode;
			this.shortcutPos = shortcutPos;
		}
	}

	private PanelEntry	_entries[] = {
			new PanelEntry("[ UILD]", 		"B", 	1, Mode.BUILD),
			new PanelEntry("[ CCUPATION]", 	"O",	1, Mode.JOBS),
			new PanelEntry("[ REW]", 		"C", 	1, Mode.CREW),
			new PanelEntry("[ OOM]", 		"R", 	1, Mode.ROOM),
			new PanelEntry("[ EBUG]", 		"D", 	1, Mode.DEBUG),
			new PanelEntry("[ LAN]", 		"P",	1, Mode.PLAN),
			new PanelEntry("[ ANAGE]", 		"M",	1, Mode.MANAGER),
			new PanelEntry("[ TATS]", 		"S",	1, Mode.STATS)
	};

	private MiniMapRenderer		_miniMapRenderer;
	private TextView 			_lbTime;
	private ResourceEntry[] 	_resources;
	private ImageView _map;

	public PanelShortcut(Mode mode, Key shortcut) {
		super(mode, shortcut);
	}
	
	@Override
	protected void onCreate() {
		_lbTime = new TextView();
		_lbTime.setCharacterSize(FONT_SIZE);
		addView(_lbTime);

//		_miniMapRenderer = new MiniMapRenderer(_effect);
		int posX = 24;
		int posY = 244;

		_map = new ImageView();
		_map.setPosition(20, 20);
		addView(_map);

		TextView lbResources = new TextView();
		lbResources.setString("Resources");
		lbResources.setPosition(posX, posY);
		lbResources.setCharacterSize(FONT_SIZE_TITLE);
		addView(lbResources);
		posY += 38;

		_resources = new ResourceEntry[NB_RESOURCE_MAX];
		addResource(0, posX, posY, ResourceManager.getInstance().getFood());
		addResource(1, posX + 195, posY, ResourceManager.getInstance().getWater());
		posY += 32;

		addResource(2, posX, posY, ResourceManager.getInstance().getGasoline());
		addResource(3, posX + 195, posY, ResourceManager.getInstance().getMatter());
		posY += 32;

		addResource(4, posX, posY, ResourceManager.getInstance().getO2());
		addResource(5, posX + 195, posY, ResourceManager.getInstance().getPower());
		posY += 42;

		TextView lbActions = new TextView();
		lbActions.setString("Actions");
		lbActions.setPosition(posX, posY);
		lbActions.setCharacterSize(FONT_SIZE_TITLE);
		addView(lbActions);
		posY += 42;

		int i = 0;
		for (PanelEntry entry: _entries) {
			final PanelEntry e = entry;

			int paddingX = (int)(175 / 2 - (entry.label.length() / 2) * 11.8 - (entry.label.length() % 2 == 0 ? 0 : 5.5));

			TextView label = new TextView(175, 36);
			label.setColor(new Color(120, 255, 255));
			label.setPosition(posX, posY);
			label.setString(e.label);
			label.setCharacterSize(20);
			label.setPadding(3, paddingX);
			label.setBackgroundColor(new Color(29, 85, 96, 100));
			label.setOnFocusListener(new OnFocusListener() {
				@Override
				public void onEnter(View view) {
					view.setBackgroundColor(new Color(29, 85, 96, 180));
				}

				@Override
				public void onExit(View view) {
					view.setBackgroundColor(new Color(29, 85, 96, 100));
				}
			});
			label.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					_ui.toogleMode(e.mode);
				}
			});
			addView(label);

			TextView shortcut = new TextView(160, 36);
			shortcut.setColor(new Color(176, 205, 53));
			shortcut.setPosition(posX + (int)(e.shortcutPos * 11.8), posY);
			shortcut.setString(e.shortcut);
			shortcut.setCharacterSize(20);
			shortcut.setPadding(3, paddingX);
			shortcut.setStyle(TextView.UNDERLINED);
			addView(shortcut);

			if (i % 2 == 0) {
				posX += 197;
			} else {
				posX = 24;
				posY += 62;
			}
			i++;
		}
	}	

	private void addResource(int index, int posX, int posY, final ResourceData data) {
		_resources[index] = new ResourceEntry();
		_resources[index].data = data;
		
		_resources[index].text = new LinkView(180, 20);
		_resources[index].text.setCharacterSize(FONT_SIZE);
		_resources[index].text.setPosition(posX, posY);
		_resources[index].text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_ui.select(data.tooltip);
			}
		});
		addView(_resources[index].text);
	}

	@Override
	public void onDraw(Renderer renderer, RenderEffect effect) {
//		_miniMapRenderer.onDraw(renderer, effect, 0);
	}

	@Override
	protected void onRefresh(int update) {
        if (_miniMapRenderer != null) {
            _miniMapRenderer.onRefresh(update);
            _map.setSprite(_miniMapRenderer.getSprite());
        }

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
