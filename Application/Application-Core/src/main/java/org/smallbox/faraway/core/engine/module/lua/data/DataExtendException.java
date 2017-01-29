package org.smallbox.faraway.core.engine.module.lua.data;

/**
 * Created by Alex on 12/10/2015.
 */
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