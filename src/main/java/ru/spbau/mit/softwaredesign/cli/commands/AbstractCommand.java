package ru.spbau.mit.softwaredesign.cli.commands;

import java.util.List;

/**
 * Common contract of command that behavior is defined by the code.
 */
public interface AbstractCommand {

    /**
     * Executes command that takes no parameters.
     * @return code that interprets result of command execution:
     *      0 -- command has been successful executed, continue session
     *     -1 -- command execution claims for termination (e.g. result of {@link CommandExit} execution)
     */
    int execute();

    /**
     * Executes command with given args.
     * @return code that interprets result of command execution:
     *      0 -- command has been successful executed, continue session
     *     -1 -- command execution claims for termination (e.g. result of {@link CommandExit} execution)
     */
    int execute(List<String> args);
}
