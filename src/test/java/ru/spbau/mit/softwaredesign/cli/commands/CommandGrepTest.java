package ru.spbau.mit.softwaredesign.cli.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.spbau.mit.softwaredesign.cli.pipe.BlockInfo;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandGrepTest {
    private CommandGrep commandGrep;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private String testData;
    private String testDataLine1 = "hello world" + System.getProperty("line.separator");
    private String testDataLine2 = "i'm alive" + System.getProperty("line.separator");
    private String testDataLine3 = "BYE WORLD" + System.getProperty("line.separator");

    @BeforeEach
    public void setUp() {
        testData = testDataLine1 + testDataLine2;
        commandGrep = new CommandGrep();
        commandGrep.passInfo(new BlockInfo(Collections.emptyList(), 0, 1));
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(System.out);
        OutputBuffer.redirectToInput();
        InputBuffer.flush();
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "hell", "h", "rld"})
    @DisplayName("test `grep pattern` call")
    public void testSingleGrepWithoutArgumentsWithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "world"})
    @DisplayName("test `grep -w pattern` call")
    public void testSingleGrepWithWordRegexpShortWithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, true, "-w");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "world"})
    @DisplayName("test `grep --word-regexp pattern` call")
    public void testSingleGrepWithWordRegexpLongWithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, true, "--word-regexp");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "hell", "h", "rld", "HeLlO", "RLD"})
    @DisplayName("test `grep -i pattern` call")
    public void testSingleGrepWithIgnoreCaseShortWithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, false, "-i");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "hell", "h", "rld", "HeLlO", "RLD"})
    @DisplayName("test `grep --ignore-case pattern` call")
    public void testSingleGrepWithIgnoreCaseLongWithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, false, "--ignore-case");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -i -w pattern` call")
    public void testSingleGrepWithIW1WithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, true, "-i", "-w");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -w -i pattern` call")
    public void testSingleGrepWithIW2WithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, true, "-w", "-i");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -w --ignore-case pattern` call")
    public void testSingleGrepWithIW3WithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, true, "-w", "--ignore-case");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep --word-regexp --ignore-case pattern` call")
    public void testSingleGrepWithIW4WithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, true, "--word-regexp", "--ignore-case");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep --ignore-case --word-regexp pattern` call")
    public void testSingleGrepWithIW5WithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, true, "--ignore-case", "--word-regexp");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -iw pattern` call")
    public void testSingleGrepWithIW6WithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, true, "-iw");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -wi pattern` call")
    public void testSingleGrepWithIW7WithUserInput(String substring) throws IOException {
        checkGrepWorkWithUserInput(substring, true, "-wi");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "hell", "h", "rld"})
    @DisplayName("test `grep pattern file.txt` call")
    public void testSingleGrepWithoutArgumentsWithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, false, "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "world"})
    @DisplayName("test `grep -w pattern file.txt` call")
    public void testSingleGrepWithWordRegexpShortWithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "-w", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "world"})
    @DisplayName("test `grep --word-regexp pattern file.txt` call")
    public void testSingleGrepWithWordRegexpLongWithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "--word-regexp", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "hell", "h", "rld", "HeLlO", "RLD"})
    @DisplayName("test `grep -i pattern file.txt` call")
    public void testSingleGrepWithIgnoreCaseShortWithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, false, "-i", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "hell", "h", "rld", "HeLlO", "RLD"})
    @DisplayName("test `grep --ignore-case pattern file.txt` call")
    public void testSingleGrepWithIgnoreCaseLongWithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, false, "--ignore-case", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -i -w pattern file.txt` call")
    public void testSingleGrepWithIW1WithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "-i", "-w", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -w -i pattern file.txt` call")
    public void testSingleGrepWithIW2WithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "-w", "-i", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -w --ignore-case pattern file.txt` call")
    public void testSingleGrepWithIW3WithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "-w", "--ignore-case", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep --word-regexp --ignore-case pattern file.txt` call")
    public void testSingleGrepWithIW4WithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "--word-regexp", "--ignore-case", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep --ignore-case --word-regexp pattern file.txt` call")
    public void testSingleGrepWithIW5WithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "--ignore-case", "--word-regexp", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -iw pattern file.txt` call")
    public void testSingleGrepWithIW6WithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "-iw", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HeLlO"})
    @DisplayName("test `grep -wi pattern file.txt` call")
    public void testSingleGrepWithIW7WithFile(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "-wi", "greptest1.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"world", "WORLD"})
    @DisplayName("test `grep -wi pattern file1.txt file2.txt` call")
    public void testGrepWorksWithTwoFiles(String substring) throws IOException {
        checkGrepWorkWithTwoFiles(substring, true, "-wi", "greptest1.txt", "greptest2.txt");
    }

    @Test
    @DisplayName("test `grep -A 1 pattern file1.txt` call")
    public void testGrepWithAfterContextShort() throws IOException {
        String[] arguments = {"-A", "1", "greptest3.txt"};
        List<String> grepArguments = Arrays.stream(arguments).collect(Collectors.toList());
        grepArguments.add(0, "a");
        commandGrep.execute(grepArguments);
        OutputBuffer.print();
        assertEquals(
                "a\n" +
                        "AB\n" +
                        "ab\n" +
                        "ba\n" +
                        "bb\n" +
                        "aa\n" +
                        "BB\n",

                outContent.toString()
        );
        outContent.close();
    }

    @Test
    @DisplayName("test `grep --after-context=1 pattern file1.txt` call")
    public void testGrepWithAfterContextLong() throws IOException {
        String[] arguments = {"--after-context=1", "greptest3.txt"};
        List<String> grepArguments = Arrays.stream(arguments).collect(Collectors.toList());
        grepArguments.add(0, "a");
        commandGrep.execute(grepArguments);
        OutputBuffer.print();
        assertEquals(
                "a\n" +
                        "AB\n" +
                        "ab\n" +
                        "ba\n" +
                        "bb\n" +
                        "aa\n" +
                        "BB\n",

                outContent.toString()
        );
        outContent.close();
    }

    @Test
    @DisplayName("test `grep -wA 5 pattern file1.txt` call")
    void testGrepWA() throws IOException {
        String[] arguments = {"-wA", "5", "greptest3.txt"};
        List<String> grepArguments = Arrays.stream(arguments).collect(Collectors.toList());
        grepArguments.add(0, "a");
        commandGrep.execute(grepArguments);
        OutputBuffer.print();
        assertEquals(
                "a\n" +
                        "AB\n" +
                        "BA\n" +
                        "ab\n" +
                        "ba\n" +
                        "bb\n",
                outContent.toString()
        );
        outContent.close();
    }

    @Test
    @DisplayName("test `grep -wiA 5 pattern file1.txt` call")
    void testGrepWIA() throws IOException {
        String[] arguments = {"-wiA", "5", "greptest3.txt"};
        List<String> grepArguments = Arrays.stream(arguments).collect(Collectors.toList());
        grepArguments.add(0, "a");
        commandGrep.execute(grepArguments);
        OutputBuffer.print();
        assertEquals(
                "A\n" +
                        "B\n" +
                        "b\n" +
                        "a\n" +
                        "AB\n" +
                        "BA\n" +
                        "ab\n" +
                        "ba\n" +
                        "bb\n",
                outContent.toString()
        );
        outContent.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"h[a-z]*o", "hel{2}o", "(hello)|(world)", "h[\\w]+o", "w[orl]{3}d", "h[^\\s]*"})
    @DisplayName("test `grep -wi pattern file.txt` call, pattern is regexp")
    public void testRegexps(String substring) throws IOException {
        checkGrepWorkWithFile(substring, true, "-wi", "greptest1.txt");
    }

    private void checkGrepWorkWithUserInput(String substring, boolean wordOrSubstring, String... arguments) throws IOException {
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(testData.getBytes()));
        List<String> grepArguments = Arrays.stream(arguments).collect(Collectors.toList());
        grepArguments.add(substring);
        commandGrep.execute(grepArguments);
        OutputBuffer.print();
        assertEquals(
                testDataLine1,
                outContent.toString(),
                String.format("string \"%s\" is a %s \"hello world\"", substring, wordOrSubstring ? "word in" : "substring of")
        );
        outContent.close();
        System.setIn(stdin);
    }

    private void checkGrepWorkWithFile(String substring, boolean wordOrSubstring, String... arguments) throws IOException {
        List<String> grepArguments = Arrays.stream(arguments).collect(Collectors.toList());
        grepArguments.add(0, substring);
        commandGrep.execute(grepArguments);
        OutputBuffer.print();
        assertEquals(
                testDataLine1,
                outContent.toString(),
                String.format("string \"%s\" is a %s \"hello world\"", substring, wordOrSubstring ? "word in" : "substring of")
        );
        outContent.close();
    }

    private void checkGrepWorkWithTwoFiles(String substring, boolean wordOrSubstring, String... arguments) throws IOException {
        List<String> grepArguments = Arrays.stream(arguments).collect(Collectors.toList());
        grepArguments.add(0, substring);
        commandGrep.execute(grepArguments);
        OutputBuffer.print();
        assertEquals(
                "greptest1.txt:" + testDataLine1 + "greptest2.txt:" + testDataLine1 + "greptest2.txt:" + testDataLine3,
                        outContent.toString()
        );
        outContent.close();
    }
}
