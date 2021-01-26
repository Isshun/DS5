package org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.impl;

import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleCommand;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleInterpreterBase;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.Collection;
import java.util.stream.Collectors;

@GameObject
public class JobModuleConsoleInterpreter extends ConsoleInterpreterBase {
    @Inject private JobModule jobModule;
    @Inject private WorldModule worldModule;
    @Inject private CharacterModule characterModule;

    @ConsoleCommand("list")
    public Collection<String> getList() {
        return jobModule.getAll().stream().map(c -> "#" + c.getId() + " " + c.getLabel()).collect(Collectors.toList());
    }

    @ConsoleCommand("add")
    public String add() {
        return null;
    }

    @ConsoleCommand("info")
    public Collection<String> getInfo(String id) {
        JobModel job = jobModule.getJob(Integer.parseInt(id));
        return list(job.getId(), job.getLabel());
    }

}
