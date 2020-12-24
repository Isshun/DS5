package org.smallbox.faraway.client.debug.interpreter.moduleInterpreter;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ConsoleInterpreterBase {

    protected String safe(Object value) {
        if (Objects.isNull(value)) {
            return "null";
        }
        return String.valueOf(value);
    }

    protected Collection<String> list(Object... value) {
        return Stream.of(value).map(this::safe).collect(Collectors.toList());
    }

}
