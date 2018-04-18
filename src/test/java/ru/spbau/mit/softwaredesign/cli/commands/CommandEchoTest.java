package ru.spbau.mit.softwaredesign.cli.commands;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;
import ru.spbau.mit.softwaredesign.cli.utils.TokenizerHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommandEchoTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    public CommandEcho commandEcho;
    private List<String> testData;
    private String expectedData;

    @Before
    public void setUp() {
        testData = new ArrayList<>();
        testData.add("1");
        testData.add(TokenizerHelper.whitespace);
        testData.add(TokenizerHelper.whitespace);
        testData.add(TokenizerHelper.whitespace);
        testData.add("2");
        testData.add(TokenizerHelper.whitespace);
        testData.add(TokenizerHelper.whitespace);
        testData.add("3");
        testData.add(TokenizerHelper.whitespace);
        testData.add(TokenizerHelper.whitespace);
        testData.add(TokenizerHelper.whitespace);
        expectedData = "1 2 3" + System.getProperty("line.separator");
        commandEcho = new CommandEcho();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(System.out);
        OutputBuffer.redirectToInput();
        InputBuffer.flush();
    }

    @Test
    public void echo_without_parameters_returns_empty_line() throws IOException {
        commandEcho.execute();
        OutputBuffer.print();
        assertEquals(System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }

    @Test
    public void echo_with_string_parameters_returns_string_without_extra_whitespaces() throws IOException {
        commandEcho.execute(testData);
        OutputBuffer.print();
        assertEquals(expectedData, outContent.toString());
        outContent.close();
    }
}
