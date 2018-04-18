package ru.spbau.mit.softwaredesign.cli.commands;

import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.util.List;

/**
 * Redefined "pwd" command.
 */
public class CommandPwd implements AbstractCommand {

    public CommandPwd() {}

    /**
     * Implements "pwd" function without parameters.
     * Produces the path to current directory
     *
     * @return code that interprets result of command execution {@see AbstractCommand}
     */
    @Override
    public int execute() {
        OutputBuffer.add(System.getProperty("user.dir"));
        OutputBuffer.add(System.getProperty("line.separator"));
        return 0;
    }

    /**
     * Implements "pwd" function with parameters.
     * Ignores all arguments and just produces the path to current directory
     * (as well as the same method without arguments).
     *
     * @param args Useless arguments of command
     * @return code that interprets result of command execution {@see AbstractCommand}
     */
    @Override
    public int execute(List<String> args) {
        return execute();
    }
}
