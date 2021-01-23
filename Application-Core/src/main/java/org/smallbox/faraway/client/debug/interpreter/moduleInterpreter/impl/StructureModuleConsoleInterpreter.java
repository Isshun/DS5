package org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.impl;

import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleCommand;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleInterpreterBase;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;

import java.util.Collection;
import java.util.stream.Collectors;

@GameObject
public class StructureModuleConsoleInterpreter extends ConsoleInterpreterBase {
    @Inject private JobModule jobModule;

    @ConsoleCommand("list")
    public Collection<String> getList() {
        return jobModule.getAll().stream().map(c -> "#" + c.getId() + " " + c.getLabel()).collect(Collectors.toList());
    }

    @ConsoleCommand("info")
    public Collection<String> getInfo(String id) {
        JobModel job = jobModule.getJob(Integer.parseInt(id));
        return list(job.getId(), job.getLabel());
    }

}
