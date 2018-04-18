package ru.spbau.mit.softwaredesign.cli.parser;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class InputLineTokenizerTest {

    @Test
    public void tokenizer_separates_whitespaces() {
        String testData = "1 2  34";
        List<String> tokens = InputLineTokenizer.tokenize(testData);
        assertEquals(6, tokens.size());
        assertEquals("1", tokens.get(0));
        assertEquals(" ", tokens.get(1));
        assertEquals("2", tokens.get(2));
        assertEquals(" ", tokens.get(3));
        assertEquals(" ", tokens.get(4));
        assertEquals("34", tokens.get(5));
    }

    @Test
    public void tokenizer_separates_single_quotes() {
        String testData = "' aba caba'";
        List<String> tokens = InputLineTokenizer.tokenize(testData);
        assertEquals(6, tokens.size());
        assertEquals("'", tokens.get(0));
        assertEquals(" ", tokens.get(1));
        assertEquals("aba", tokens.get(2));
        assertEquals(" ", tokens.get(3));
        assertEquals("caba", tokens.get(4));
        assertEquals("'", tokens.get(5));
    }

    @Test
    public void tokenizer_separates_double_quotes() {
        String testData = "\" aba caba\"";
        List<String> tokens = InputLineTokenizer.tokenize(testData);
        assertEquals(tokens.size(), 6);
        assertEquals(6, tokens.size());
        assertEquals("\"", tokens.get(0));
        assertEquals(" ", tokens.get(1));
        assertEquals("aba", tokens.get(2));
        assertEquals(" ", tokens.get(3));
        assertEquals("caba", tokens.get(4));
        assertEquals("\"", tokens.get(5));
    }

    @Test
    public void tokenizer_separates_dollar_sign() {
        String testData = "$x $ $z";
        List<String> tokens = InputLineTokenizer.tokenize(testData);
        assertEquals(7, tokens.size());
        assertEquals("$", tokens.get(0));
        assertEquals("x", tokens.get(1));
        assertEquals(" ", tokens.get(2));
        assertEquals("$", tokens.get(3));
        assertEquals(" ", tokens.get(4));
        assertEquals("$", tokens.get(5));
        assertEquals("z", tokens.get(6));
    }

    @Test
    public void tokenizer_separates_pipe_sign() {
        String testData = "$x | $z";
        List<String> tokens = InputLineTokenizer.tokenize(testData);
        assertEquals(7, tokens.size());
        assertEquals("$", tokens.get(0));
        assertEquals("x", tokens.get(1));
        assertEquals(" ", tokens.get(2));
        assertEquals("|", tokens.get(3));
        assertEquals(" ", tokens.get(4));
        assertEquals("$", tokens.get(5));
        assertEquals("z", tokens.get(6));
    }

    @Test
    public void tokenizer_separates_assignment_operator() {
        String testData = "$x= $z";
        List<String> tokens = InputLineTokenizer.tokenize(testData);
        assertEquals(6, tokens.size());
        assertEquals("$", tokens.get(0));
        assertEquals("x", tokens.get(1));
        assertEquals("=", tokens.get(2));
        assertEquals(" ", tokens.get(3));
        assertEquals("$", tokens.get(4));
        assertEquals("z", tokens.get(5));
    }
}
