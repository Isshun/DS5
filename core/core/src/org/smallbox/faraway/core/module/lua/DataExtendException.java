package org.smallbox.faraway.core.module.lua;

/**
 * Created by Alex on 12/10/2015.
 */
public class DataExtendException extends Throwable {
    private final Type      _type;
    private final String    _item;

    public enum Type { MANDATORY }
    public DataExtendException(Type type, String item) {
        _type = type;
        _item = item;
    }

    @Override
    public String getMessage() {
        return _item + " is mandatory";
    }
}
