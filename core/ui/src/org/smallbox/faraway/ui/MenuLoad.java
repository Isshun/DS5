package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.OnLoadListener;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuLoad extends MenuBase {
    private static final int     FRAME_WIDTH = 640;
    private static final int     FRAME_HEIGHT = 480;
    private int                 _index;
    private List<UILabel>        _lbFiles;
    private int                 _nbFiles;

    public MenuLoad(final OnLoadListener onLoadListener) throws IOException {
        super(FRAME_WIDTH, FRAME_HEIGHT);

        setPosition(Constant.WINDOW_WIDTH / 2 - FRAME_WIDTH / 2, Constant.WINDOW_HEIGHT / 2 - FRAME_HEIGHT / 2);

        setBackgroundColor(new Color(200, 50, 140, 150));

        _lbFiles = new ArrayList<>();
        _nbFiles = 0;
        for (final File file: FileUtils.list("saves")) {
            UILabel lbFile = new UILabel(200, 32);
            lbFile.setTextSize(16);
            lbFile.setText(file.getName());
            lbFile.setTextColor(Color.WHITE);
            lbFile.setPosition(200, 32 * _nbFiles);
            lbFile.setOnClickListener(() -> {
                onLoadListener.onLoad(file.getAbsolutePath());
                setVisible(false);
            });
            _lbFiles.add(lbFile);
            addView(lbFile);
            _nbFiles++;
        }

        setVisible(true);
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport) {
        int i = 0;
        for (UILabel lbFile: _lbFiles) {
            lbFile.setTextColor(i++ == _index ? Color.YELLOW : Color.WHITE);
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
