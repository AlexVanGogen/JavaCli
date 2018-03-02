package ru.spbau.mit.softwaredesign.cli.parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.softwaredesign.cli.errors.ExpectedExitException;
import ru.spbau.mit.softwaredesign.cli.errors.UnknownExternalCommandException;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;
import ru.spbau.mit.softwaredesign.cli.utils.BoundVariablesStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockExecutorTest {

    private BlockExecutor executor;
    private String testData;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        testData = "hello world" + System.getProperty("line.separator") + "i'm alive" + System.getProperty("line.separator");
        executor = new BlockExecutor();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(System.out);
        OutputBuffer.redirectToInput();
        InputBuffer.flush();
    }

    @Test
    public void assignment_makes_new_record_in_storage() throws ExpectedExitException, UnknownExternalCommandException {
        List<String> tokens = Arrays.asList("x", "=", "y");
        executor.execute(tokens);
        assertTrue(BoundVariablesStorage.tryFetch("x").isPresent());
        assertEquals(BoundVariablesStorage.tryFetch("x").get(), "y");
    }

    @Test(expected = UnknownExternalCommandException.class)
    public void incorrect_assignment_is_treated_as_external_command() throws ExpectedExitException, UnknownExternalCommandException {
        List<String> tokens = Arrays.asList("zzz", " ", "=", "y");
        executor.execute(tokens);
        assertFalse(BoundVariablesStorage.tryFetch("zzz").isPresent());
    }

    @Test
    public void check_correct_variable_names() {
        assertTrue(executor.isCorrectVariableName("papa"));
        assertTrue(executor.isCorrectVariableName("papa_jones"));
        assertTrue(executor.isCorrectVariableName("papa__johannes"));
        assertTrue(executor.isCorrectVariableName("papa123j_ones"));
        assertTrue(executor.isCorrectVariableName("___papa_jones___"));
    }

    @Test
    public void trimming_removes_leading_and_trailing_whitespaces() {
        List<String> tokens = Arrays.asList(" ", " ", " ", "a", " ");
        List<String> trimmedTokens = executor.trimList(tokens);
        assertEquals(trimmedTokens.size(), 1);
        assertEquals(trimmedTokens.get(0), "a");
    }

    @Test
    public void redefined_command_is_recognized_and_arguments_are_calculated() throws ExpectedExitException, UnknownExternalCommandException {
        List<String> tokens = Arrays.asList("cat", " ", " ", "cattest.txt", " ", "cattest2.txt");
        executor.execute(tokens);
        OutputBuffer.print();
        assertEquals(outContent.toString(), testData + testData);
        try {
            outContent.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
