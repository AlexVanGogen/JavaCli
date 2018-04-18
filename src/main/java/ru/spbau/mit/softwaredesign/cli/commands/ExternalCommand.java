package ru.spbau.mit.softwaredesign.cli.commands;

import ru.spbau.mit.softwaredesign.cli.errors.ErrorMessage;
import ru.spbau.mit.softwaredesign.cli.errors.UnknownExternalCommandException;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Any command that behavior is defined by the code.
 */
public class ExternalCommand {

    /**
     * Executes external command in the original way.
     * @param command external command with all parameters
     */
    public static void execute(String command) throws UnknownExternalCommandException {
        StringBuilder executionOutput = new StringBuilder();

        try {
            ProcessBuilder externalCommandExecutor = new ProcessBuilder(command);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(externalCommandExecutor.start().getInputStream()));
            String line;
            while ( (line = reader.readLine()) != null) {
                executionOutput.append(line);
                executionOutput.append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            throw new UnknownExternalCommandException();
        }
        OutputBuffer.add(executionOutput.toString());
    }
}
