package org.smallbox.faraway.util;

public class UUIDUtils {
    private static int _uuid;

    public static int getUUID() {
        return ++_uuid;
    }

    public static int getUUID(int usedId) {
        if (_uuid < usedId + 1) {
            _uuid = usedId + 1;
        }
        return usedId;
    }
}
