package ru.spbau.mit.softwaredesign.cli.pipe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class OutputBufferTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        OutputBuffer.redirectToInput();
        InputBuffer.flush();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(System.out);
        OutputBuffer.redirectToInput();
        InputBuffer.flush();
    }

    @Test
    public void add_operation_appends_new_string_to_the_buffer() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            OutputBuffer.add(String.valueOf(i));
            sb.append(String.valueOf(i));
        }
        OutputBuffer.print();
        assertEquals(sb.toString(), outContent.toString());
        outContent.close();
    }

    @Test
    public void redirecting_to_input_moves_all_data() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            OutputBuffer.add(String.valueOf(i));
            sb.append(String.valueOf(i));
        }
        OutputBuffer.redirectToInput();
        OutputBuffer.print();
        assertEquals("", outContent.toString());
        assertEquals(sb.toString(), InputBuffer.get());
        outContent.close();
    }
}
