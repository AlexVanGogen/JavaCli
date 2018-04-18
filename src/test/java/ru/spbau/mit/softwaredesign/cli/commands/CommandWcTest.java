package ru.spbau.mit.softwaredesign.cli.commands;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class CommandWcTest {

    private CommandWc commandWc;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private String testData;
    private String expectedData;
    private String expectedTotal;

    @Before
    public void setUp() {
        testData = "hello world" + System.getProperty("line.separator") + "i'm alive" + System.getProperty("line.separator");
        expectedData = "\t2\t4\t22";
        expectedTotal = "\t4\t8\t44";
        commandWc = new CommandWc();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(System.out);
        OutputBuffer.redirectToInput();
        InputBuffer.flush();
    }

    @Test
    public void single_wc_without_arguments_counts_user_input() throws IOException {
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(testData.getBytes()));
        commandWc.calculateUserInput();
        OutputBuffer.print();
        assertEquals(expectedData + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
        System.setIn(stdin);
    }

    @Test
    public void single_wc_with_arguments_counts_from_file_and_prints_results_to_output() throws IOException {
        String filename = "cattest.txt";
        PrintWriter out = new PrintWriter(filename);
        out.write(testData);
        out.close();

        commandWc.execute(Collections.singletonList(filename));
        OutputBuffer.print();
        assertEquals(expectedData + " " + filename + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }

    @Test
    public void single_wc_with_arguments_counts_many_files_and_prints_results_to_output_with_total() throws IOException {
        String filename = "cattest.txt";
        PrintWriter out = new PrintWriter(filename);
        out.write(testData);
        out.close();

        String filename2 = "cattest2.txt";
        out = new PrintWriter(filename2);
        out.write(testData);
        out.close();

        commandWc.execute(Arrays.asList(filename, filename2));
        OutputBuffer.print();
        assertEquals(expectedData + " " + filename + System.getProperty("line.separator")
                + expectedData + " " + filename2 + System.getProperty("line.separator")
                + expectedTotal + " total" + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }

    @Test
    public void single_wc_with_arguments_notifies_about_absence_of_file() throws IOException {
        String filename = "cattest.txt";
        PrintWriter out = new PrintWriter(filename);
        out.write(testData);
        out.close();

        String filename2 = "cattest3.txt";
        commandWc.execute(Arrays.asList(filename, filename2));
        OutputBuffer.print();
        assertEquals("wc: " + filename2 + ": error - file not found" + System.getProperty("line.separator")
                + expectedData + " " + filename + System.getProperty("line.separator")
                + expectedData + " total" + System.getProperty("line.separator"), outContent.toString());
        outContent.close();
    }
}
