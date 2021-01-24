package org.smallbox.faraway.core.lua.data;

public class DataExtendException extends Throwable {
    private final Type      _type;
    private final String    _item;

    public enum Type { MANDATORY, MISSING_PARENT }
    public DataExtendException(Type type, String item) {
        _type = type;
        _item = item;
    }

    @Override
    public String getMessage() {
        return _item + " is mandatory";
    }
}
