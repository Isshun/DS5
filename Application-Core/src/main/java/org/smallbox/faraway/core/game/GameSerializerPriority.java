package org.smallbox.faraway.core.game;

public enum GameSerializerPriority {
    NO_PRIORITY(999),
    MODULE_ITEM_PRIORITY(200),
    MODULE_JOB_PRIORITY(300),
    MODULE_CHARACTER_PRIORITY(100),
    MODULE_WORLD_PRIORITY(0);

    private final int priority;

    GameSerializerPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
