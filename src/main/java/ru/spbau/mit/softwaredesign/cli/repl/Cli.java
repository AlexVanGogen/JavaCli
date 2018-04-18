package ru.spbau.mit.softwaredesign.cli.repl;

import ru.spbau.mit.softwaredesign.cli.errors.*;
import ru.spbau.mit.softwaredesign.cli.parser.ChainExecutor;
import ru.spbau.mit.softwaredesign.cli.parser.InputLineTokenizer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;
import ru.spbau.mit.softwaredesign.cli.substitutions.Interpolator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Main class for launching command-line interface.
 */
public class Cli {

    /**
     * Launch main loop.
     */
    public static void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");

            /* Get new prompt and tokenize it */
            String nextLine = null;
            try {
                nextLine = reader.readLine();
            } catch (IOException e) {
                System.exit(0);
            }
            if (nextLine.isEmpty())
                continue;

            try {
                int returnCode = execute(nextLine);
                if (returnCode == -1) {
                    return;
                }
            } catch (PipelineException e) {
                ErrorMessage.print(ErrorMessage.PIPELINE_SYNTAX_ERROR, "<pipe>");
            } catch (UnknownExternalCommandException e) {
                ErrorMessage.print(ErrorMessage.COMMAND_NOT_FOUND, e.getFrom());
            } catch (UncompletedLineException e) {
                ErrorMessage.print(ErrorMessage.SYNTAX_ERROR, "<interpreter>");
                continue;
            }

            OutputBuffer.print();
        }
    }

    /**
     * Handle prompt: tokenize, interpolate, and execute it.
     * @param nextLine new prompt
     * @return code that interprets result of command execution {@see AbstractCommand, ChainExecutor}
     */
    public static int execute(String nextLine) throws UnknownExternalCommandException, PipelineException, UncompletedLineException {
        List<String> tokens = InputLineTokenizer.tokenize(nextLine);

        /* Interpolate prompt */
        Interpolator interpolator = new Interpolator();
        List<String> interpolatedTokens;
        interpolatedTokens = interpolator.interpolate(tokens);

        /* Execute prompt */
        ChainExecutor chainExecutor = new ChainExecutor(interpolatedTokens);
        return chainExecutor.execute();
    }

    public static void main(String[] args) {
        run();
    }
}
