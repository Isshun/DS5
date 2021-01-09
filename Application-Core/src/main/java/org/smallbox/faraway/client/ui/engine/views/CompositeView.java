package org.smallbox.faraway.client.ui.engine.views;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.engine.module.ModuleBase;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class CompositeView extends View {

    public CompositeView(ModuleBase module) {
        super(module);
    }

    public static Optional<CompositeView> instanceOf(View view) {
        return view instanceof CompositeView ? Optional.of((CompositeView)view) : Optional.empty();
    }

    public final void removeAllViews() {
        _views.forEach(view -> {
            uiManager.removeView(view);
            uiEventManager.removeListeners(view);
            if (view instanceof CompositeView) {
                ((CompositeView)view).removeAllViews();
            }
            onRemoveView(view);
        });
        _views.clear();
    }

    public boolean isLeaf() {
        return _views.isEmpty();
    }

    public void setSpecial(boolean special) {
        _special = special;
    }

    public void actionSpecial() {
        _parent.setSpecialTop(this);
    }

    public boolean hasSpecial() {
        return _special;
    }

    public void setSpecialTop(CompositeView specialTop) {
        _specialTop = specialTop;
    }

    public View createFromTemplate() {
        return _template != null ? _template.createFromTemplate() : null;
    }

    public void setSorted(boolean sorted) {
        if (sorted) {
            Collection<View> views = _views;
            _views = new PriorityBlockingQueue<>(10, View::compareTo);
            _views.addAll(views);
        }
    }

    //    protected Set<View>         _views = new ConcurrentSkipListSet<>((o1, o2) -> Integer.compare(o1.getIndex(), o2.getIndex()));
    protected Collection<View>  _views = new LinkedBlockingQueue<>();
    protected TemplateCallback  _template;
    protected Collection<View>  _nextViews = new ConcurrentLinkedQueue<>();
    protected boolean           _special = false;
    private CompositeView _specialTop;
    protected int               _level;

    public void         setLevel(int level) { _level = level; }
    public int          getLevel() { return _level; }

    public Collection<View> getViews() { return _views; }

    public int          compareLevel(CompositeView view) { return _deep != view.getDeep() ? _deep - view.getDeep() : hashCode() - view.hashCode(); }

    public void         setDeep(int deep) {
        super.setDeep(deep);
        _views.forEach(view -> view.setDeep(deep + 1));
    }

    public final void addNextView(View view) {
        _nextViews.add(view);
    }

    public final void switchViews() {
        removeAllViews();
        _nextViews.forEach(this::addView);
        _nextViews.clear();

        if (_parent != null) {
            _parent.updateSize();
        }
    }

    public final CompositeView addView(View view) {
        view.setParent(this);

        if (!CollectionUtils.containsAny(_views, view)) {
            _views.add(view);
        }

        uiManager.addView(view);

        onAddView(view);

        return this;
    }

    public final CompositeView setTemplate(TemplateCallback templateCallback) {
        _template = templateCallback;
        return this;
    }

    protected abstract void onAddView(View view);

    protected abstract void onRemoveView(View view);

    public View findByAction(String actionName) {
        for (View view: _views) {
            if (view._actionName != null && view._actionName.equals(actionName)) {
                return view;
            }
            if (view instanceof CompositeView) {
                View ret = ((CompositeView)view).findByAction(actionName);
                if (ret != null) {
                    return ret;
                }
            }
        }
        return null;
    }

    public UILabel findLabel(String resId) {
        return (UILabel)findById(resId);
    }

    public UIImage findImage(String resId) {
        return (UIImage)findById(resId);
    }

    public View findById(String resId) {
        for (View view: _views) {
            if (StringUtils.equals(view._id, resId)) {
                return view;
            }
            if (view instanceof CompositeView) {
                View ret = ((CompositeView)view).findById(resId);
                if (ret != null) {
                    return ret;
                }
            }
        }
        return null;
    }

}