package org.smallbox.faraway.ui.panel.right;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.manager.RoomManager;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.UILabel;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.panel.BaseRightPanel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PanelDebug extends BaseRightPanel {

	public PanelDebug(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut);
	}

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

	private UILabel 			_lbSearch;
	private String 				_search = "";
    private FrameLayout         _entries;
    private int                 _index;
	private String 				_cmd;


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

	@Override
	public void onLayoutLoaded(LayoutModel layout) {
		findById("bt_add_character").setOnClickListener(view -> Game.getCharacterManager().addRandom(150, 150));
//		findById("bt_add_matter").setOnClickListener(view -> ResourceManager.getInstance().addScience(500));
		findById("bt_toggle_debug").setOnClickListener(view -> _ui.toggleMode(Mode.DEBUGITEMS));
		findById("bt_make_room").setOnClickListener(view -> ((RoomManager)Game.getInstance().getManager(RoomManager.class)).makeRooms());
		findById("bt_kill_all").setOnClickListener(view -> {
			Game.getCharacterManager().getCharacters().forEach(CharacterModel::setIsDead);
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
	}

	@Override
	protected void onOpen() {
	}

	@Override
	protected void onClose() {
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


        UILabel lbEntry = ViewFactory.getInstance().createTextView();
        lbEntry.setColor(Color.WHITE);
        lbEntry.setCharacterSize(14);
        lbEntry.setString(text);
        lbEntry.setPosition(0, 20 * _index++);
        _entries.addView(lbEntry);
    }

    public void dumpJob(BaseJobModel job) {
        println(job.getLabel());
        println("char: " + (job.getCharacter() != null ? job.getCharacter().toString() : "none"));
    }

}
