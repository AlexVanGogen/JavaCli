package ru.spbau.mit.softwaredesign.cli.commands;

import org.apache.commons.cli.*;
import ru.spbau.mit.softwaredesign.cli.errors.ErrorMessage;
import ru.spbau.mit.softwaredesign.cli.pipe.BlockInfo;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Redefined "grep" command.
 */
public class CommandGrep implements AbstractCommand {

    private final Options commandOptions = new Options();
    private CommandLineParser commandLineParser = new PosixParser();
    private HelpFormatter formatter = new HelpFormatter();

    private int linesCountAfterFound = 1;
    private boolean ignoreCase = false;
    private boolean patternAsWord = false;
    private String[] commandData;

    private BlockInfo currentBlockInfo;

    public CommandGrep() {
        commandOptions
                .addOption(OptionBuilder
                        .withLongOpt("after-context")
                        .withDescription("Print num lines of trailing context after each match.")
                        .hasArg()
                        .withArgName("num")
                        .create('A')
                )
                .addOption(OptionBuilder
                        .withLongOpt("ignore-case")
                        .withDescription("Perform case insensitive matching.  By default, grep is case sensitive.")
                        .create('i')
                )
                .addOption(OptionBuilder
                        .withLongOpt("word-regexp")
                        .withDescription("The expression is searched for as a word (as if surrounded by`[[:<:]]' and `[[:>:]]'.")
                        .create('w')
                );
    }

    /**
     * Set block information {@see BlockInfo} to executor.
     * @param blockInfo information about the block where the command has been called
     */
    public void passInfo(BlockInfo blockInfo) {
        this.currentBlockInfo = blockInfo;
    }

    /**
     * Implements "grep" function without parameters.
     * Simply prints help text.
     *
     * @return code that interprets result of command execution {@see AbstractCommand}
     */
    @Override
    public int execute() {
        printHelp();
        return 0;
    }

    /**
     * Implements "grep" function with parameters.
     * Arguments have already been interpolated.
     * Result is stored in {@link OutputBuffer}.
     *
     * @param args pattern (required), options (optional), filenames (optional)
     * @return code that interprets result of command execution {@see AbstractCommand}
     */
    @Override
    public int execute(List<String> args) {
        try {
            handleArguments(args);
            if (commandData.length > 1) {
                executeWithFile();
            } else {
                if (currentBlockInfo.getRelativePosition() == BlockInfo.SpecificPosition.SINGLE_BLOCK) {
                    executeWithUserInput();
                } else {
                    executeWithInputBuffer();
                }
                return 0;
            }
        } catch (ParseException ignored) {
            printHelp();
        } catch (NumberFormatException e) {
            ErrorMessage.print(ErrorMessage.PARSE_ERROR, "grep: ");
        }
        return 0;
    }

    /**
     * Reads data from file (stored in {@see commandData})
     * and matches each line with pattern (stored in {@see commandData}).
     */
    private void executeWithFile() {
        List<String> allLines = new ArrayList<>();
        for (int i = 1; i < commandData.length; i++) {
            String filePointer = "";
            if (commandData.length > 2) {
                filePointer = commandData[i] + ":";
            }
            try {
                List<String> linesInFile = Files.readAllLines(Paths.get(commandData[i]));
                for (String line : linesInFile) {
                    allLines.add(filePointer + line);
                }
            } catch (IOException e) {
                ErrorMessage.print(ErrorMessage.FILE_NOT_FOUND, "grep: " + commandData[i]);
            }
        }
        List<String> linesToOutput = getAllMatchingLines(allLines, commandData[0]);
        linesToOutput.forEach(line -> OutputBuffer.add(line + System.getProperty("line.separator")));
        if (currentBlockInfo.isLastBlock()) {
            OutputBuffer.print();
        }
    }

    /**
     * Reads lines from user input on-line
     * and immediately matches each line with pattern (stored in {@see commandData}).
     */
    private void executeWithUserInput() {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                final String nextLine = br.readLine();
                if (nextLine == null || nextLine.length() == 0) {
                    br.close();
                    return;
                }
                saveLineInOutputBufferIfMatches(nextLine);
            } catch (IOException e) {
                ErrorMessage.print(ErrorMessage.IO_ERROR, "grep: ");
            }
        }
    }

    /**
     * Reads data from {@link InputBuffer}
     * and matches each line with pattern (stored in {@see commandData}).
     */
    private void executeWithInputBuffer() {
        String[] allLinesInInputBuffer = InputBuffer.get().split(System.getProperty("line.separator"));
        List<String> allLines = Arrays.asList(allLinesInInputBuffer);
        List<String> linesToOutput = getAllMatchingLines(allLines, commandData[0]);
        linesToOutput.forEach(line -> OutputBuffer.add(line + System.getProperty("line.separator")));
        if (currentBlockInfo.isLastBlock()) {
            OutputBuffer.print();
        }
    }

    /**
     * Writes line to {@link OutputBuffer} if it contains pattern.
     *
     * @param nextLine line to compare with pattern
     */
    private void saveLineInOutputBufferIfMatches(String nextLine) {
        if (isLineContainsPattern(nextLine, commandData[0])) {
            OutputBuffer.add(nextLine);
            OutputBuffer.add(System.getProperty("line.separator"));
            if (currentBlockInfo.isLastBlock()) {
                OutputBuffer.print();
            }
        }
    }

    /**
     * Find all lines that match given pattern.
     *
     * @param lines list of lines to compare with pattern
     * @param pattern pattern lines will be compared with
     * @return list of lines that contain pattern
     */
    private List<String> getAllMatchingLines(List<String> lines, String pattern) {
        List<String> matchedLines = new ArrayList<>();
        int lastAddedLineNumber = -1;
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String nextLine = lines.get(lineNumber);
            if (isLineContainsPattern(nextLine, pattern)) {
                if (lineNumber > lastAddedLineNumber) {
                    matchedLines.add(nextLine);
                }
                for (int i = 1; i < linesCountAfterFound && lineNumber + i < lines.size(); i++) {
                    if (lineNumber + i > lastAddedLineNumber) {
                        matchedLines.add(lines.get(lineNumber + i));
                    }
                }
                lastAddedLineNumber = lineNumber + linesCountAfterFound - 1;
            }
        }
        return matchedLines;
    }

    /**
     * Parses given arguments to find options (i.e. -i, --word-regexp) and data (i.e. pattern, filename).
     *
     * @param args given arguments
     * @throws ParseException when parser cannot recognize given arguments
     * @throws NumberFormatException if parameter for --after-context option isn't a number
     */
    private void handleArguments(List<String> args) throws ParseException, NumberFormatException {
        String[] argumentsAsArray = args.toArray(new String[0]);
        CommandLine commandLine = commandLineParser.parse(commandOptions, argumentsAsArray);
        commandData = commandLine.getArgs();
        if (commandLine.hasOption("A")) {
            linesCountAfterFound = Integer.valueOf(commandLine.getOptionValue("A")) + 1;
        }
        if (commandLine.hasOption("i")) {
            ignoreCase = true;
        }
        if (commandLine.hasOption("w")) {
            patternAsWord = true;
        }
    }

    /**
     * Checks if given line contains given pattern.
     *
     * @param line line from file / user input / input buffer
     * @param pattern pattern that can be contained in line
     * @return true if given line contains given pattern, false otherwise
     */
    private boolean isLineContainsPattern(String line, String pattern) {
        if (ignoreCase) {
            pattern = pattern.toLowerCase();
            line = line.toLowerCase();
        }
        if (patternAsWord) {
            pattern = patternToWord(pattern);
        }
        Pattern regexPattern = Pattern.compile(pattern);
        return regexPattern.matcher(line).find();
    }

    /**
     * Wraps (if needed) pattern with whitespaces.
     *
     * @param pattern pattern to transform
     * @return transformed pattern
     */
    private String patternToWord(String pattern) {
        return "(\\W|^)" + pattern + "(\\W|$)";
    }

    /**
     * Prints usage of command to the console,
     * if user has not entered command options properly.
     */
    private void printHelp() {
        formatter.printHelp("grep", commandOptions, true);
    }
}
