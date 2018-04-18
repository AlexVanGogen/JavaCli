package ru.spbau.mit.softwaredesign.cli.commands;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommandExitTest {

    private CommandExit commandExit;
    private List<String> exitParameters;

    @Before
    public void setUp() {
        commandExit = new CommandExit();
        exitParameters = Arrays.asList("so", "many", "useless", "arguments", "...");
    }

    @Test
    public void exit_without_parameters_must_return_special_code() {
        assertEquals(-1, commandExit.execute());
    }

    @Test
    public void exit_with_parameters_must_throw_expected_exit_exception() {
        assertEquals(-1, commandExit.execute(exitParameters));
    }
}
