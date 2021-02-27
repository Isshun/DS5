package org.smallbox.faraway.client;

public interface ProgressCallback {
    float getProgress();
    int getCurrent();
    int getTotal();
}
