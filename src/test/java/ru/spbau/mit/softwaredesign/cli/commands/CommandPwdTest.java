package ru.spbau.mit.softwaredesign.cli.commands;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommandPwdTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    public CommandPwd commandPwd;
    private String expectedData;
    private List<String> pwdParameters;

    @Before
    public void setUp() {
        expectedData = System.getProperty("user.dir") + System.getProperty("line.separator");
        pwdParameters = Arrays.asList("so", "many", "useless", "arguments", "...");
        commandPwd = new CommandPwd();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(System.out);
        OutputBuffer.redirectToInput();
        InputBuffer.flush();
    }

    @Test
    public void pwd_without_parameters_returns_current_directory() throws IOException {
        commandPwd.execute();
        OutputBuffer.print();
        assertEquals(expectedData, outContent.toString());
        outContent.close();
    }

    @Test
    public void echo_with_string_parameters_returns_string_without_extra_whitespaces() throws IOException {
        commandPwd.execute(pwdParameters);
        OutputBuffer.print();
        assertEquals(expectedData, outContent.toString());
        outContent.close();
    }
}
