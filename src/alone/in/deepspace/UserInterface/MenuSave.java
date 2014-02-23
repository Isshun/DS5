package alone.in.deepspace.UserInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Game;
import alone.in.deepspace.UserInterface.Utils.OnClickListener;
import alone.in.deepspace.UserInterface.Utils.UIText;
import alone.in.deepspace.UserInterface.Utils.UIView;
import alone.in.deepspace.Utils.Constant;

public class MenuSave extends MenuBase {
	private static final int FRAME_WIDTH = 640;
	private static final int FRAME_HEIGHT = 480;
	private UIText _lbRenderTime;
	private UIText _lbMemoryUsed;
	private UIText _lbMemoryTotal;
	private int _used;
	private int _index;
	private List<UIText>	_lbFiles;
	private int _nbFiles;
	
	public MenuSave(final Game game) throws IOException {
		super(new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setPosition(new Vector2f(Constant.WINDOW_WIDTH / 2 - FRAME_WIDTH / 2, Constant.WINDOW_HEIGHT / 2 - FRAME_HEIGHT / 2));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		_lbRenderTime = new UIText(new Vector2f(10, 10));
		_lbRenderTime.setCharacterSize(14);
		_lbRenderTime.setColor(Color.WHITE);
		_lbRenderTime.setPosition(new Vector2f(10, 6));
		addView(_lbRenderTime);
		
		_lbMemoryUsed = new UIText(new Vector2f(10, 10));
		_lbMemoryUsed.setCharacterSize(14);
		_lbMemoryUsed.setColor(Color.WHITE);
		_lbMemoryUsed.setPosition(new Vector2f(100, 6));
		addView(_lbMemoryUsed);
		
		_lbFiles = new ArrayList<UIText>();
		File[] files = null;
		File directoryToScan = new File("saves");
		files = directoryToScan.listFiles();
		_nbFiles = files.length;
		int i = 0;
		for (final File file: files) {
			UIText lbFile = new UIText(new Vector2f(200, 32));
			lbFile.setCharacterSize(16);
			lbFile.setString(file.getName());
			lbFile.setColor(Color.WHITE);
			lbFile.setPosition(new Vector2f(200, 32 * i));
			lbFile.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(UIView view) {
					game.load(file.getAbsolutePath());
					setVisible(false);
				}
			});
			_lbFiles.add(lbFile);
			addView(lbFile);
			i++;
		}

		setVisible(true);
	}
	
	@Override
	public void onRefresh(RenderWindow app) {
		int i = 0;
		for (UIText lbFile: _lbFiles) {
			lbFile.setColor(i++ == _index ? Color.YELLOW : Color.WHITE);
		}
	}
	
	public void onKeyDown() {
		_index = (_index + 1) % _nbFiles;
	}

	public void onKeyUp() {
		_index = (_index - 1) % _nbFiles;
	}

	public void onKeyEnter() {
	}

}
