package ru.spbau.mit.softwaredesign.cli.utils;

import org.junit.Test;

import java.util.List;
import java.util.StringJoiner;

import static org.junit.Assert.assertEquals;

public class TokenizerHelperTest {

    @Test
    public void tokenizing_must_return_all_tokens_including_delimiters() {
        String[] delimiters = new String[]{"\\.", "\\,", "\\-"};
        List<String> tokens = TokenizerHelper.tokenizeAndKeepDelimiters("a,b.c--d e", delimiters);
        assertEquals(8, tokens.size());
        assertEquals("a", tokens.get(0));
        assertEquals(",", tokens.get(1));
        assertEquals("b", tokens.get(2));
        assertEquals(".", tokens.get(3));
        assertEquals("c", tokens.get(4));
        assertEquals("-", tokens.get(5));
        assertEquals("-", tokens.get(6));
        assertEquals("d e", tokens.get(7));
    }

    @Test
    public void tokens_represent_a_partition_of_string() {
        String[] delimiters = new String[]{"\\.", "\\,", "\\-"};
        List<String> tokens = TokenizerHelper.tokenizeAndKeepDelimiters("a,b.c--d e", delimiters);
        StringJoiner joiner = new StringJoiner("");
        tokens.forEach(joiner::add);
        assertEquals("a,b.c--d e", joiner.toString());
    }
}
