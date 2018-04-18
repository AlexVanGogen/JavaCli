package ru.spbau.mit.softwaredesign.cli.pipe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockCounterTest {

    @Before
    public void setUp() {
        BlockCounter.reset();
    }

    @After
    public void tearDown() {
        BlockCounter.reset();
    }

    @Test
    public void increase_counter_value_really_increases_it_by_one() {
        for (int i = 0; i < 100; i++) {
            int previousValue = BlockCounter.get();
            BlockCounter.increase();
            assertEquals(previousValue + 1, BlockCounter.get());
        }
    }
}
