package ru.spbau.mit.softwaredesign.cli.commands;

import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.util.List;

/**
 * Redefined "exit" command.
 */
public class CommandExit implements AbstractCommand {

    public CommandExit() {}

    /**
     * Implements "exit" function without parameters.
     * Shutdowns the program.
     *
     * @return code that interprets result of command execution {@see AbstractCommand}
     */
    @Override
    public int execute() {
        OutputBuffer.add("bye. and don't write to me anymore...");
        OutputBuffer.add(System.getProperty("line.separator"));
        OutputBuffer.print();
        return -1;
    }

    /**
     * Implements "exit" function with parameters.
     * Ignores all arguments and just shutdowns the program
     * (as well as the same method without arguments).
     *
     * @param args Useless arguments of command
     *
     * @return code that interprets result of command execution {@see AbstractCommand}
     */
    @Override
    public int execute(List<String> args) {
        return execute();
    }
}
