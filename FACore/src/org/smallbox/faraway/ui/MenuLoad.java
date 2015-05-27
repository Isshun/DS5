package org.smallbox.faraway.ui;

import org.jsfml.system.Vector2f;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.serializer.GameLoadListener;
import org.smallbox.faraway.engine.ui.OnClickListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.util.Constant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuLoad extends MenuBase {
	private static final int 	FRAME_WIDTH = 640;
	private static final int 	FRAME_HEIGHT = 480;
	private int 				_index;
	private List<TextView>		_lbFiles;
	private int 				_nbFiles;
	
	public MenuLoad(final GameLoadListener onLoadListener) throws IOException {
		super(FRAME_WIDTH, FRAME_HEIGHT);
		
		setPosition(new Vector2f(Constant.WINDOW_WIDTH / 2 - FRAME_WIDTH / 2, Constant.WINDOW_HEIGHT / 2 - FRAME_HEIGHT / 2));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		_lbFiles = new ArrayList<TextView>();
		File[] files = null;
		File directoryToScan = new File("saves");
		files = directoryToScan.listFiles();
		_nbFiles = files.length;
		int i = 0;
		for (final File file: files) {
			TextView lbFile = new TextView(200, 32);
			lbFile.setCharacterSize(16);
			lbFile.setString(file.getName());
			lbFile.setColor(Color.WHITE);
			lbFile.setPosition(200, 32 * i);
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
	public void onDraw(Renderer renderer, RenderEffect effect) {
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
