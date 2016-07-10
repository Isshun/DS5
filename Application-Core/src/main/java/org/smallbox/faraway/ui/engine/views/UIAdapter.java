package org.smallbox.faraway.ui.engine.views;

import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.Collection;

/**
 * Created by Alex on 08/10/2015.
 */
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