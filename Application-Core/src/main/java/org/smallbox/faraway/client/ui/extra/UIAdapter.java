package org.smallbox.faraway.client.ui.extra;

import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.game.world.ObjectModel;

import java.util.Collection;

public class UIAdapter {
    private Collection<ObjectModel>         _data;
    private final OnCreateView              _onCreateView;
    private boolean                         _needRefresh;

    public UIAdapter(Collection<ObjectModel> data, OnCreateView onCreateView) {
        _data = data;
        _needRefresh = true;
        _onCreateView = onCreateView;
    }

    public boolean needRefresh() {
        return _needRefresh;
    }

    public Collection<ObjectModel> getData() {
        return _data;
    }

    public void setData(Collection<ObjectModel> data) {
        _data = data;
    }

    public OnCreateView getCallback() {
        return _onCreateView;
    }

    public void setRefresh() {
        _needRefresh = false;
    }

    public interface OnCreateView {
        View onCreateView();
        void onBindView(View subview, ObjectModel data);
    }
}
