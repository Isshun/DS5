package org.smallbox.faraway.client.debug.interpreter;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleCommand;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleInterpreterBase;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.impl.*;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationObject
public class DebugCommandInterpreterService {
    private final static Map<String, ConsoleInterpreterBase> EXTRA_COMMANDS = new HashMap<>();

    @Inject
    private CharacterModuleConsoleInterpreter characterModuleConsoleInterpreter;

    @Inject
    private ConsumableModuleConsoleInterpreter consumableModuleConsoleInterpreter;

    @Inject
    private ItemModuleConsoleInterpreter itemModuleConsoleInterpreter;

    @Inject
    private JobModuleConsoleInterpreter jobModuleConsoleInterpreter;

    @Inject
    private ModuleConsoleInterpreter moduleConsoleInterpreter;

    @Inject
    private RoomModuleConsoleInterpreter roomModuleConsoleInterpreter;

    @Inject
    private StructureModuleConsoleInterpreter structureModuleConsoleInterpreter;

    @Inject
    private WeatherModuleConsoleInterpreter weatherModuleConsoleInterpreter;

    @Inject
    private WorldModuleConsoleInterpreter worldModuleConsoleInterpreter;

    private String commandInput;

    @OnInit
    private void onInit() {
//        EXTRA_COMMANDS.put("area", characterModuleConsoleInterpreter);
//        EXTRA_COMMANDS.put("building", characterModuleConsoleInterpreter);
        EXTRA_COMMANDS.put("character", characterModuleConsoleInterpreter);
        EXTRA_COMMANDS.put("consumable", consumableModuleConsoleInterpreter);
        EXTRA_COMMANDS.put("item", itemModuleConsoleInterpreter);
        EXTRA_COMMANDS.put("job", jobModuleConsoleInterpreter);
//        EXTRA_COMMANDS.put("plan", characterModuleConsoleInterpreter);
        EXTRA_COMMANDS.put("room", roomModuleConsoleInterpreter);
//        EXTRA_COMMANDS.put("storing", characterModuleConsoleInterpreter);
        EXTRA_COMMANDS.put("structure", structureModuleConsoleInterpreter);
        EXTRA_COMMANDS.put("weather", weatherModuleConsoleInterpreter);
        EXTRA_COMMANDS.put("world", worldModuleConsoleInterpreter);
        EXTRA_COMMANDS.put("module", moduleConsoleInterpreter);
    }

    public void execute(String command) {
        Log.command("> " + command);

        if (!command.contains("\\s")) {
            execute(command, command, "help", null);
        }

        Matcher matcher2 = Pattern.compile("(.*?)\\s(.*?)").matcher(command);
        if (matcher2.matches()) {
            execute(command, matcher2.group(1), matcher2.group(2), null);
        }

        Matcher matcher3 = Pattern.compile("(.*?)\\s(.*?)\\s(.*?)").matcher(command);
        if (matcher3.matches()) {
            execute(command, matcher3.group(1), matcher3.group(2), matcher3.group(3));
        }
    }

    private void execute(String command, String interpreterName, String actionName, String param) {
        if (EXTRA_COMMANDS.containsKey(interpreterName)) {
            ConsoleInterpreterBase interpreter = EXTRA_COMMANDS.get(interpreterName);

            if (StringUtils.equals("help", actionName)) {
                Log.command("Command available for " + interpreterName + ": " + Stream.of(interpreter.getClass().getMethods())
                        .filter(method -> method.isAnnotationPresent(ConsoleCommand.class))
                        .map(method -> method.getAnnotation(ConsoleCommand.class).value())
                        .collect(Collectors.joining(", ")));
            } else {
                Stream.of(interpreter.getClass().getMethods())
                        .filter(method -> method.isAnnotationPresent(ConsoleCommand.class))
                        .filter(method -> StringUtils.equals(method.getAnnotation(ConsoleCommand.class).value(), actionName))
                        .forEach(method -> executeAction(interpreter, method, param));
                Log.command(command + " done");
            }
        }
    }

    private void executeAction(ConsoleInterpreterBase interpreter, Method method, String param) {
        try {
            Object results = Objects.nonNull(param) ? method.invoke(interpreter, param) : method.invoke(interpreter);
            if (results instanceof Collection) {
                ((Collection<String>)results).forEach(Log::command);
            }
            if (results instanceof String) {
                Log.command((String)results);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public String autoComplete(String command) {
        return EXTRA_COMMANDS.keySet().stream()
                .sorted(Comparator.comparingInt(String::length))
                .filter(s -> s.startsWith(command))
                .findFirst()
                .orElse(null);
    }

    public void setCommandInput(String commandInput) {
        this.commandInput = commandInput;
    }

    public String getCommandInput() {
        return commandInput;
    }

}
