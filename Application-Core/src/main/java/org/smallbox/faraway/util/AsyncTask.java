package org.smallbox.faraway.util;

public abstract class AsyncTask<T_RETURN> {
    private T_RETURN _data;

    public abstract void onStart();
    public abstract T_RETURN onBackground();
    public abstract void onComplete(T_RETURN data);

    public void start() {
        onStart();
        new Thread(() -> _data = onBackground()).start();
    }

    public void complete() {
        onComplete(_data);
    }

    public boolean isComplete() {
        return _data != null;
    }
}
