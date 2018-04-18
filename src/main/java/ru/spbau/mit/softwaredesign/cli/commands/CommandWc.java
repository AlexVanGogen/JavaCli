package ru.spbau.mit.softwaredesign.cli.commands;

import ru.spbau.mit.softwaredesign.cli.errors.ErrorMessage;
import ru.spbau.mit.softwaredesign.cli.pipe.BlockCounter;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Redefined "wc" command.
 * Calculates lines, words, and characters count from input.
 */
public class CommandWc implements AbstractCommand {

    private WcCalculator totalCalculator;

    public CommandWc() {
        totalCalculator = new WcCalculator();
    }

    /**
     * Implements "wc" command without parameters.
     * Behavior depends of position of this command in the pipeline:
     *  - if this is the first command in the pipeline, then calculate the user input.
     *  - otherwise calculate the input buffer.
     *
     * @return code that interprets result of command execution {@see AbstractCommand}
     */
    @Override
    public int execute() {
        if (BlockCounter.get() == 0) {
            try {
                calculateUserInput();
            } catch (IOException e) {
                ErrorMessage.print(ErrorMessage.IO_ERROR, "wc: ");
            }
        } else {
            calculateInputBuffer();
        }
        return 0;
    }

    /**
     * Implements "wc" function with parameters.
     * Arguments have already been interpolated, so that is file names.
     *
     * @param filenames Files given for calculating
     * @return code that interprets result of command execution {@see AbstractCommand}
     */
    @Override
    public int execute(List<String> filenames) {
        Charset charset = Charset.forName("UTF-8");
        for (String filename : filenames) {
            WcCalculator nextFileCalculator = new WcCalculator();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename), charset)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    nextFileCalculator.calculateAdd(line + System.getProperty("line.separator"));
                }
                OutputBuffer.add(nextFileCalculator.dump(filename));
                OutputBuffer.add(System.getProperty("line.separator"));
                totalCalculator.addFrom(nextFileCalculator);
            } catch (IOException e) {
                ErrorMessage.print(ErrorMessage.FILE_NOT_FOUND, "wc: " + filename);
            }
        }
        if (filenames.size() > 1) {
            OutputBuffer.add(totalCalculator.dumpTotal());
            OutputBuffer.add(System.getProperty("line.separator"));
        }
        return 0;
    }

    /**
     * Implements behavior when no arguments were passed and this is the first command in pipeline.
     * Calculates lines, words, and characters count from the user input.
     *
     * @throws IOException for the case of input/output error
     */
    public void calculateUserInput() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            final String nextLine = br.readLine();
            if (nextLine == null || nextLine.length() == 0) {
                OutputBuffer.add(totalCalculator.dump());
                OutputBuffer.add(System.getProperty("line.separator"));
                totalCalculator.resetAll();
                br.close();
                return;
            }
            totalCalculator.calculateAdd(nextLine + System.getProperty("line.separator"));
        }
    }

    /**
     * Implements behavior when no arguments were passed and this is not the first command in pipeline.
     * Calculates lines, words, and characters count from the input buffer.
     */
    private void calculateInputBuffer() {
        totalCalculator.calculateReset(InputBuffer.get());
        OutputBuffer.add(totalCalculator.dump());
        OutputBuffer.add(System.getProperty("line.separator"));
        totalCalculator.resetAll();
    }

    /**
     * Special class for calculations
     */
    final class WcCalculator {

        /* number of all lines */
        private long linesCount;

        /* number of all words */
        private long wordsCount;

        /* number of all characters, including whitespaces and escaped symbols */
        private long charactersCount;

        public WcCalculator() {
            resetAll();
        }

        /**
         * Add lines, words and characters count from given string to counters.
         * Lines are separated by system-specific separator.
         * Words are separated by one or more whitespaces.
         * @param data Data for calculating
         */
        public void calculateAdd(String data) {
            char[] dataChars = data.toCharArray();

            /* count characters */
            charactersCount += dataChars.length;

            /* count words */
            Pattern nonWhitespacePattern = Pattern.compile("[\\S]+");
            Matcher m = nonWhitespacePattern.matcher(data);
            while (m.find()) {
                wordsCount++;
            }

            /* count lines */
            int lastSeparatorPosition = 0;
            while ((lastSeparatorPosition = data.indexOf(System.getProperty("line.separator"), lastSeparatorPosition) + 1) != 0) {
                linesCount++;
            }
        }

        /**
         * Set lines, words and characters count from given string to counters.
         * Lines are separated by system-specific separator.
         * Words are separated by one or more whitespaces.
         * @param data Data for calculating
         */
        public void calculateReset(String data) {
            resetAll();
            calculateAdd(data);
        }

        /**
         * Sum values of counters from given calculator with current values
         *
         * @param other Other calculator
         */
        public void addFrom(WcCalculator other) {
            linesCount += other.linesCount;
            wordsCount += other.wordsCount;
            charactersCount += other.charactersCount;
        }

        /**
         * Write counters values in the following format:
         * <tab><lines><tab><words><tab><characters>
         * to string.
         * @return String with info described above.
         */
        public String dump() {
            StringJoiner formattedDump = new StringJoiner("\t");
            formattedDump.add(String.valueOf(linesCount));
            formattedDump.add(String.valueOf(wordsCount));
            formattedDump.add(String.valueOf(charactersCount));
            return "\t" + formattedDump.toString();
        }

        /**
         * Write counters values in the following format:
         * <tab><lines><tab><words><tab><characters> <filename>
         * to string.
         * @param filename Filename
         * @return String with info described above.
         */
        public String dump(String filename) {
            return dump() + " " + filename;
        }

        /**
         * Write counters values in the following format:
         * <tab><lines><tab><words><tab><characters> total
         * to string.
         * @return String with info described above.
         */
        public String dumpTotal() {
            return dump() + " total";
        }

        /**
         * Reset counters inside class.
         */
        public final void resetAll() {
            linesCount = 0;
            wordsCount = 0;
            charactersCount = 0;
        }
    }
}
