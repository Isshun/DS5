package org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.impl;

import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleCommand;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleInterpreterBase;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@GameObject
public class ConsumableModuleConsoleInterpreter extends ConsoleInterpreterBase {
    @Inject private CharacterModule characterModule;

    @ConsoleCommand("list")
    public Collection<String> getList() {
        return characterModule.getAll().stream().map(c -> "#" + c.getId() + " " + c.getName()).collect(Collectors.toList());
    }

    @ConsoleCommand("add")
    public Collection<String> actionAdd() {
        characterModule.addRandom();
        return List.of("done");
    }

    @ConsoleCommand("info")
    public Collection<String> getInfo(String id) {
        CharacterModel character = characterModule.get(Integer.parseInt(id));
        return List.of(
                character.getName(),
                character.getJob() != null ? character.getJob().getLabel() : ""
        );
    }

}
