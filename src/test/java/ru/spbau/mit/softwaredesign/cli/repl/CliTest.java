package ru.spbau.mit.softwaredesign.cli.repl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.softwaredesign.cli.errors.*;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class CliTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private String testData;
    private String expectedWcData;
    private String expectedWcTotal;

    @Before
    public void setUp() {
        testData = "hello world" + System.getProperty("line.separator") + "i'm alive" + System.getProperty("line.separator");
        expectedWcData = "\t2\t4\t22";
        expectedWcTotal = "\t4\t8\t44";
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(System.out);
        OutputBuffer.redirectToInput();
        InputBuffer.flush();
    }

    @Test
    public void assignment_x_and_the_following_reference_call_return_value_of_x() throws CliException, IOException {
        Cli.execute("x=1");
        Cli.execute("echo $x");
        assertEquals("1" + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }

    @Test
    public void echo_with_parameter_inside_single_quotes_prints_raw_parameter() throws CliException, IOException {
        Cli.execute("x=1");
        Cli.execute("echo 'abc$x'");
        assertEquals("abc$x" + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }

    @Test
    public void echo_with_parameter_inside_double_quotes_substitutes_variables() throws CliException, IOException {
        Cli.execute("x=1");
        Cli.execute("echo \"abc$x\"");
        assertEquals("abc1" + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }

    @Test(expected = UnknownExternalCommandException.class)
    public void command_with_parameters_inside_single_quotes_must_throw_an_exception() throws CliException {
        Cli.execute("'echo 1'");
    }

    @Test(expected = UnknownExternalCommandException.class)
    public void command_with_parameters_inside_double_quotes_must_throw_an_exception() throws CliException {
        Cli.execute("\"echo 1\"");
    }

    @Test
    public void assignment_command_inside_single_quotes_to_variable_gives_correct_command_call() throws CliException, IOException {
        String filename = "cattest.txt";
        PrintWriter out = new PrintWriter(filename);
        out.write(testData);
        out.close();

        Cli.execute("x='cat cattest.txt'");
        Cli.execute("$x");
        assertEquals(testData, outContent.toString());
        outContent.close();
    }

    @Test
    public void assignment_command_inside_double_quotes_to_variable_gives_correct_command_call() throws CliException, IOException {
        String filename = "cattest.txt";
        PrintWriter out = new PrintWriter(filename);
        out.write(testData);
        out.close();

        Cli.execute("x=\"cat cattest.txt\"");
        Cli.execute("$x");
        assertEquals(testData, outContent.toString());
        outContent.close();
    }

    @Test
    public void many_cats_in_a_row_makes_no_effects() throws IOException, CliException {
        String filename = "cattest.txt";
        PrintWriter out = new PrintWriter(filename);
        out.write(testData);
        out.close();

        String filename2 = "cattest2.txt";
        out = new PrintWriter(filename2);
        out.write(testData);
        out.close();

        Cli.execute("wc cattest.txt cattest2.txt | cat | cat | cat|cat|    cat  |  cat");
        assertEquals(expectedWcData + " " + filename + System.getProperty("line.separator")
                + expectedWcData + " " + filename2 + System.getProperty("line.separator")
                + expectedWcTotal + " total" + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }

    @Test
    public void pwd_as_the_last_block_prints_only_current_directory() throws CliException, IOException {
        String filename = "cattest.txt";
        PrintWriter out = new PrintWriter(filename);
        out.write(testData);
        out.close();

        String filename2 = "cattest2.txt";
        out = new PrintWriter(filename2);
        out.write(testData);
        out.close();

        Cli.execute("wc cattest.txt cattest2.txt | cat | cat | cat|cat|    cat  |  cat | pwd");
        assertEquals(System.getProperty("user.dir") + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }

    @Test
    public void quotes_inside_double_quotes_are_common_symbols() throws UncompletedLineException, UnknownExternalCommandException, PipelineException, IOException {
        Cli.execute("echo \"'blah-blah'\"");
        assertEquals("'blah-blah'" + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }

    @Test
    public void quotes_inside_single_quotes_are_common_symbols() throws UncompletedLineException, UnknownExternalCommandException, PipelineException, IOException {
        Cli.execute("echo '\"blah-blah\"'");
        assertEquals("\"blah-blah\"" + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }
}
