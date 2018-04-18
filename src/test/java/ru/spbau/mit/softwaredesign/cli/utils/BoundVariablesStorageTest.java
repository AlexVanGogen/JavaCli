package ru.spbau.mit.softwaredesign.cli.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoundVariablesStorageTest {

    private String key;
    private String value;

    @Before
    public void setUp() {
        key = "Papa";
        value = "Jones";
    }

    @Test
    public void add_new_key_value_entry_and_get_value_by_key_must_return_value() {
        BoundVariablesStorage.putVariable(key, value);
        assertTrue(BoundVariablesStorage.tryFetch(key).isPresent());
        assertEquals(value, BoundVariablesStorage.tryFetch(key).get());
    }

    @Test
    public void get_value_by_not_existing_key_must_return_nothing() {
        assertFalse(BoundVariablesStorage.tryFetch(value).isPresent());
    }
}
