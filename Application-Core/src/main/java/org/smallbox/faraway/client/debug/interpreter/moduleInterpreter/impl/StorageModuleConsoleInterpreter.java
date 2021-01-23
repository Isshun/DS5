package org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.impl;

import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleCommand;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleInterpreterBase;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.storage.StorageModule;

import java.util.Collection;
import java.util.stream.Collectors;

@GameObject
public class StorageModuleConsoleInterpreter extends ConsoleInterpreterBase {
    @Inject private StorageModule storageModule;

    @ConsoleCommand("list")
    public Collection<String> getList() {
        return storageModule.getAreas().stream().map(c -> "#" + c.getId() + " " + c.getName()).collect(Collectors.toList());
    }

}
