package ru.spbau.mit.softwaredesign.cli.pipe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InputBufferTest {

    @Before
    public void setUp() {
        InputBuffer.flush();
    }

    @After
    public void tearDown() {
        InputBuffer.flush();
    }

    @Test
    public void add_operation_appends_new_string_to_the_buffer() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            InputBuffer.add(String.valueOf(i));
            sb.append(String.valueOf(i));
        }
        assertEquals(192, InputBuffer.get().length());
        assertEquals(sb.toString(), InputBuffer.get());
    }
}
