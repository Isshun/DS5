package alone.in.deepspace.UserInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.OnLoadListener;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;

public class MenuLoad extends MenuBase {
	private static final int 	FRAME_WIDTH = 640;
	private static final int 	FRAME_HEIGHT = 480;
	private int 				_index;
	private List<TextView>		_lbFiles;
	private int 				_nbFiles;
	
	public MenuLoad(final OnLoadListener onLoadListener) throws IOException {
		super(new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setPosition(new Vector2f(Constant.WINDOW_WIDTH / 2 - FRAME_WIDTH / 2, Constant.WINDOW_HEIGHT / 2 - FRAME_HEIGHT / 2));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		_lbFiles = new ArrayList<TextView>();
		File[] files = null;
		File directoryToScan = new File("saves");
		files = directoryToScan.listFiles();
		_nbFiles = files.length;
		int i = 0;
		for (final File file: files) {
			TextView lbFile = new TextView(new Vector2f(200, 32));
			lbFile.setCharacterSize(16);
			lbFile.setString(file.getName());
			lbFile.setColor(Color.WHITE);
			lbFile.setPosition(new Vector2f(200, 32 * i));
			lbFile.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					onLoadListener.onLoad(file.getAbsolutePath());
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
		for (TextView lbFile: _lbFiles) {
			lbFile.setColor(i++ == _index ? Color.YELLOW : Color.WHITE);
		}
	}
	
	@Override
	public void onKeyDown() {
		_index = (_index + 1) % _nbFiles;
	}

	@Override
	public void onKeyUp() {
		_index = _index == 0 ? _nbFiles - 1 : _index - 1;
	}

	@Override
	public void onKeyEnter() {
		_lbFiles.get(_index).onClick();
	}

}
