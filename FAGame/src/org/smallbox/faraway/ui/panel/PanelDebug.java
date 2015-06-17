package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.engine.util.Settings;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.job.JobModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PanelDebug extends BaseRightPanel {

	private static class CommandModel {
		private final String 		cmd;
		private final OnCommandExec onCommandExec;

		public interface OnCommandExec {
			void onCommandExec();
		}

		public CommandModel(String cmd, OnCommandExec onCommandExec) {
			this.cmd = cmd;
			this.onCommandExec = onCommandExec;
		}
	}

	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	private TextView 			_lbSearch;
	private TextView[] 			_labels;
	private TextView[] 			_shortcuts;
	private int 				_nbEntries;
	private int 				_line;
	private String 				_search = "";
	private int 				_nbResults;
	private ItemInfo 			_currentItem;
    private FrameLayout         _entries;
    private int                 _index;
	private String 					_cmd;


	private CommandModel[] COMMANDS = new CommandModel[] {
			new CommandModel("jobs.list", () -> JobManager.getInstance().getJobs().forEach(job -> println(job.toString()))),
			new CommandModel("crew.add", () -> Game.getCharacterManager().addRandom(5, 5)),
//			new CommandModel("crew.remove", () -> Game.getCharacterManager().addRandom(5, 5)),
			new CommandModel("crew.removeAll", () -> Game.getCharacterManager().getCharacters().removeAll(Game.getCharacterManager().getCharacters())),
			new CommandModel("world.add seaweed", () -> {
				for (int i = 0; i < 10; i++) {
					Game.getWorldManager().putObject("base.seaweed1", (int)(Math.random() * Game.getWorldManager().getWidth()), (int)(Math.random() * Game.getWorldManager().getHeight()), 0, 10);
				}
			})
	};

    public PanelDebug(Mode mode, GameEventListener.Key shortcut) {
//		super(mode, shortcut, Constant.WINDOW_WIDTH - FRAME_WIDTH, 32, FRAME_WIDTH, FRAME_HEIGHT, null);
		super(mode, shortcut);
//
//		setBackgroundColor(new Color(160, 80, 140, 150));
//
//		// Re-launch jobs
//		TextView lbReLaunchJob = ViewFactory.getInstance().createTextView(200, 32);
//		lbReLaunchJob.setOnClickListener(view -> {
//            int width = ServiceManager.getWorldMap().getWidth();
//            int height = ServiceManager.getWorldMap().getHeight();
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    StructureItem structure = ServiceManager.getWorldMap().getStructure(x, y);
//                    if (structure != null && !structure.hasComponents()) {
//                        JobManager.getInstance().addBuild(structure);
//                    }
//                    UserItem item = ServiceManager.getWorldMap().getItem(x, y);
//                    if (item != null && !item.hasComponents()) {
//                        JobManager.getInstance().addBuild(item);
//                    }
//                }
//            }
//        });
//		lbReLaunchJob.setString("Re-launch jobs");
//		lbReLaunchJob.setCharacterSize(20);
//		lbReLaunchJob.setColor(Color.WHITE);
//		lbReLaunchJob.setPosition(20, 100);
//		addView(lbReLaunchJob);
//
//		// Clear jobs
//		TextView lbClearJob = ViewFactory.getInstance().createTextView(200, 32);
//		lbClearJob.setOnClickListener(view -> JobManager.getInstance().clear());
//		lbClearJob.setString("Clear jobs");
//		lbClearJob.setCharacterSize(20);
//		lbClearJob.setColor(Color.WHITE);
//		lbClearJob.setPosition(20, 140);
//		addView(lbClearJob);
//
//		// Add seed
//		TextView lbAddSeed = ViewFactory.getInstance().createTextView(200, 32);
//		lbAddSeed.setOnClickListener(view -> {
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//            ServiceManager.getWorldMap().addRandomSeed();
//        });
//		lbAddSeed.setString("Add seed");
//		lbAddSeed.setCharacterSize(20);
//		lbAddSeed.setColor(Color.WHITE);
//		lbAddSeed.setPosition(20, 180);
//		addView(lbAddSeed);
//
//		// Add water
//		TextView lbAddWater = ViewFactory.getInstance().createTextView(200, 32);
//		lbAddWater.setOnClickListener(view -> ResourceManager.getInstance().addWater(20));
//		lbAddWater.setString("Add water");
//		lbAddWater.setCharacterSize(20);
//		lbAddWater.setColor(Color.WHITE);
//		lbAddWater.setPosition(20, 220);
//		addView(lbAddWater);
//
//		// Reset light
//		TextView lbResetLight = ViewFactory.getInstance().createTextView(200, 32);
//		lbResetLight.setOnClickListener(view -> ((MainRenderer)MainRenderer.getInstance()).initLight());
//		lbResetLight.setString("Reset light");
//		lbResetLight.setCharacterSize(20);
//		lbResetLight.setColor(Color.WHITE);
//		lbResetLight.setPosition(20, 260);
//		addView(lbResetLight);
//
//		// Add dome
//		TextView lbDome = ViewFactory.getInstance().createTextView(200, 32);
//		lbDome.setOnClickListener(view -> {
//            for (double j = 0; j < 20; j++) {
//                for (double i = 0; i < Math.PI * 2; i += 0.01) {
//                    double offsetX = (int)Math.round(Math.cos(i) * j);
//                    double offsetY = (int)Math.round(Math.sin(i) * j);
//                    ServiceManager.getWorldMap().putObject("base.floor", (int)(80 + offsetX), (int)(80 + offsetY), 0, 500);
//                }
//            }
//            for (double i = 0; i < Math.PI * 2; i += 0.01) {
//                double offsetX = (int)Math.round(Math.cos(i) * 20);
//                double offsetY = (int)Math.round(Math.sin(i) * 20);
//                ServiceManager.getWorldMap().putObject("base.wall", (int)(80 + offsetX), (int)(80 + offsetY), 0, 500);
//            }
//        });
//		lbDome.setString("Dome");
//		lbDome.setCharacterSize(20);
//		lbDome.setColor(Color.WHITE);
//		lbDome.setPosition(20, 360);
//		addView(lbDome);
	}

	@Override
	public void onLayoutLoaded(LayoutModel layout) {
		findById("bt_add_character").setOnClickListener(view -> Game.getCharacterManager().addRandom(150, 150));
		findById("bt_add_matter").setOnClickListener(view -> ResourceManager.getInstance().addMatter(500));
		findById("bt_toggle_debug").setOnClickListener(view -> _ui.toggleMode(Mode.DEBUGITEMS));
		findById("bt_make_room").setOnClickListener(view -> Game.getRoomManager().makeRooms());
		findById("bt_kill_all").setOnClickListener(view -> {
			Game.getCharacterManager().clear();
			JobManager.getInstance().clear();
		});
	}

	@Override
	protected void onCreate(ViewFactory factory) {
		_lbSearch = factory.createTextView();
		_lbSearch.setPosition(20, 60);
//		_lbSearch.setString("search: ");
		_lbSearch.setCharacterSize(FONT_SIZE);
		_lbSearch.setColor(Colors.TEXT);
		addView(_lbSearch);
        
        _entries = factory.createFrameLayout();
        _entries.setPosition(20, 100);
        addView(_entries);

//		_nbEntries = Game.getData().items.size();
//		_labels = new TextView[_nbEntries];
//		_shortcuts = new TextView[_nbEntries];
//		for (int i = 0; i < _nbEntries; i++) {
//			_labels[i] = factory.createTextView();
//			_labels[i].setPosition(20, 600 + i * LINE_HEIGHT);
//			_labels[i].setCharacterSize(FONT_SIZE);
//			addView(_labels[i]);
//
//			_shortcuts[i] = factory.createTextView();
//			_shortcuts[i].setPosition(20, 600 + i * LINE_HEIGHT);
//			_shortcuts[i].setCharacterSize(FONT_SIZE);
//			_shortcuts[i].setColor(Colors.LINK_ACTIVE);
//			_shortcuts[i].setStyle(TextView.UNDERLINED);
//			addView(_shortcuts[i]);
//		}
	}

	@Override
	protected void onOpen() {
		Settings.getInstance().setDebug(true);
	}

	@Override
	protected void onClose() {
		Settings.getInstance().setDebug(false);
	}

	@Override
	public void onRefresh(int frame) {
//		int i = 0;
//		for (ItemInfo item: Game.getData().items) {
//			if (_search.length() == 0) {
//				if (i == _line) {
//					_currentItem = item;
//					_shortcuts[i].setVisible(true);
//					_shortcuts[i].setString(item.label);
//					_shortcuts[i].setPosition(20, _shortcuts[i].getPosY());
//					_shortcuts[i].setColor(Colors.LINK_ACTIVE);
//					_labels[i].setVisible(false);
//				}
//				else {
//					_shortcuts[i].setVisible(false);
//					_labels[i].setVisible(true);
//					_labels[i].setString(item.label);
//				}
//				i++;
//			}
//
//			else {
//				int pos = item.label.toLowerCase().indexOf(_search);
//				if (pos != -1) {
//					if (i == _line) {
//						_currentItem = item;
//						_shortcuts[i].setString(item.label);
//						_shortcuts[i].setColor(Colors.LINK_ACTIVE);
//						_shortcuts[i].setPosition(20, _shortcuts[i].getPosY());
//						_labels[i].setVisible(false);
//					}
//					else {
//						_shortcuts[i].setString(item.label.substring(pos, pos + _search.length()));
//						_shortcuts[i].setColor(Colors.LINK_INACTIVE);
//						_shortcuts[i].setPosition(20 + pos * 8, _shortcuts[i].getPosY());
//						_labels[i].setVisible(true);
//						_labels[i].setString(item.label);
//					}
//					_shortcuts[i].setVisible(true);
//					i++;
//				}
//			}
//		}
//		_nbResults = i - 1;
//		for (; i < _nbEntries; i++) {
//			_shortcuts[i].setVisible(false);
//			_labels[i].setVisible(false);
//		}
	}
	
	@Override
	public boolean	onKey(GameEventListener.Key key) {
		if (key == GameEventListener.Key.ENTER) {
//			int x = UserInterface.getInstance().getMouseX();
//			int y = UserInterface.getInstance().getMouseY();
//			Log.info("x: " + x + ", y: " + y);
//			int progress = 0;
//			if (_currentItem.cost != null) {
//				progress = _currentItem.cost.progress;
//			}
//			Game.getWorldManager().putObject(_currentItem, x, y, 0, progress);

			exec(_search);

			_search = "";
		}
		else if (key == GameEventListener.Key.UP) {
			_search = _cmd;
//			_line = _line - 1 < 0 ? _nbResults : _line - 1;
		}
		else if (key == GameEventListener.Key.DOWN) {
//			_line = _line + 1 > _nbResults ? 0 : _line + 1;
		}
		else if (key == GameEventListener.Key.BACKSPACE) {
			if (_search.length() > 0) {
				_search = _search.substring(0, _search.length() - 1);
			}
		} else {
			String str = StringUtils.getStringFromKey(key);
			if (str != null) {
				_search += str;
			} else {
				close();
				return false;
			}
		}
		_lbSearch.setString("" + _search);
		onRefresh(0);
		return true;
	}

	private void exec(String cmd) {
		cmd = cmd.trim();
        _cmd = cmd;
        clear();

		for (CommandModel command: COMMANDS) {
			if (command.cmd.toLowerCase().equals(cmd.toLowerCase())) {
				command.onCommandExec.onCommandExec();
				return;
			}
		}

		if (cmd.startsWith("job")) {
			Matcher m = Pattern.compile("job (\\d+)").matcher(cmd);
			if (m.matches()) {
				int jobId = Integer.valueOf(m.group(1));
				JobManager.getInstance().getJobs().stream().filter(job -> job.getId() == jobId).forEach(job -> dumpJob(job));
			}
		}
	}

    private void clear() {
        _index = 0;
        _entries.removeAllViews();
    }

    private void println(String text) {
        Log.debug(text);


        TextView lbEntry = ViewFactory.getInstance().createTextView();
        lbEntry.setColor(Color.WHITE);
        lbEntry.setCharacterSize(14);
        lbEntry.setString(text);
        lbEntry.setPosition(0, 20 * _index++);
        _entries.addView(lbEntry);
    }

    public void dumpJob(JobModel job) {
        println(job.getLabel());
        println("char: " + (job.getCharacter() != null ? job.getCharacter().toString() : "none"));
    }

//	void  addDebug(final String key, String value) {
//		int y = _index * 32;
//
//		Text text = ObjectPool.getText();
//		
//		text.setFont(SpriteManager.getInstance().getFont());
//		text.setCharacterSize(20);
//		text.setStyle(Text.REGULAR);
//		
//		text.setString(key);
//		text.setPosition(Constant.WINDOW_WIDTH - 320 + Constant.UI_PADDING, Constant.UI_PADDING + y);
//		_app.draw(text);
//
//		text.setString(value);
//		text.setPosition(Constant.WINDOW_WIDTH - 320 + Constant.UI_PADDING + 160, Constant.UI_PADDING + y);
//		_app.draw(text);
//
//		ObjectPool.release(text);
//
//		_index++;
//	}
//	
//	void  addDebug(final String str) {
//		int y = _index * 32;
//
//		Text text = ObjectPool.getText();
//		
//		text.setFont(SpriteManager.getInstance().getFont());
//		text.setCharacterSize(20);
//		text.setStyle(Text.REGULAR);
//		text.setColor(Color.WHITE);
//		
//		text.setString(str);
//		text.setPosition(0, 0);
//		_app.draw(text, _renderEffect);
//
//		ObjectPool.release(text);
//
//		_index++;
//	}

}
