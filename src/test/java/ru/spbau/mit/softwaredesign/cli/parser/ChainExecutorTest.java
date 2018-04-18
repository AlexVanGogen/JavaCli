package ru.spbau.mit.softwaredesign.cli.parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.softwaredesign.cli.errors.PipelineException;
import ru.spbau.mit.softwaredesign.cli.errors.UnknownExternalCommandException;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ChainExecutorTest {

    private ChainExecutor executor;
    private String testData;
    private String expectedWcData;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        testData = "hello world" + System.getProperty("line.separator") + "i'm alive" + System.getProperty("line.separator");
        expectedWcData = "\t2\t4\t22";
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(System.out);
        OutputBuffer.redirectToInput();
        InputBuffer.flush();
    }

    @Test(expected = PipelineException.class)
    public void if_two_pipes_in_a_row_then_there_must_be_thrown_an_exception() throws UnknownExternalCommandException, PipelineException {
        List<String> tokens = Arrays.asList("pwd", " ", " ", "|", "|", "|", " ", "3", "|", " ", "4");
        executor = new ChainExecutor(tokens);
        executor.execute();
    }

    @Test
    public void cat_that_is_not_in_the_first_place_in_the_pipeline_redirects_input_to_output() throws UnknownExternalCommandException, PipelineException {
        String filename = "cattest.txt";
        List<String> tokens = Arrays.asList("wc", " ", filename, " ", "|", " ", "cat");
        executor = new ChainExecutor(tokens);
        executor.execute();
        OutputBuffer.print();
        assertEquals(expectedWcData + " " + filename + System.getProperty("line.separator"), outContent.toString());
    }

    @Test
    public void wc_that_is_not_in_the_first_place_in_the_pipeline_counts_from_input_buffer() throws UnknownExternalCommandException, PipelineException {
        String filename = "cattest.txt";
        List<String> tokens = Arrays.asList("cat", " ", filename, " ", "|", " ", "wc");
        executor = new ChainExecutor(tokens);
        executor.execute();
        OutputBuffer.print();
        assertEquals(expectedWcData + System.getProperty("line.separator"), outContent.toString());
    }

    @Test
    public void echo_that_is_not_in_the_first_place_in_the_pipeline_returns_its_argument() throws UnknownExternalCommandException, PipelineException {
        String filename = "cattest.txt";
        List<String> tokens = Arrays.asList("wc", " ", filename, " ", "|", " ", "echo", "x");
        executor = new ChainExecutor(tokens);
        executor.execute();
        OutputBuffer.print();
        assertEquals("x" + System.getProperty("line.separator"), outContent.toString());
    }
}
