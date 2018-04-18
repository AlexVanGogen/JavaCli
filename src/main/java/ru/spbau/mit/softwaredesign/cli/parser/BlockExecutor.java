package ru.spbau.mit.softwaredesign.cli.parser;

import ru.spbau.mit.softwaredesign.cli.errors.UnknownExternalCommandException;
import ru.spbau.mit.softwaredesign.cli.utils.BoundVariablesStorage;
import ru.spbau.mit.softwaredesign.cli.commands.*;
import ru.spbau.mit.softwaredesign.cli.resourses.MainCommands;
import ru.spbau.mit.softwaredesign.cli.utils.TokenizerHelper;

import java.util.*;

/**
 * Class that executes separated block and produces appropriate output data.
 */
public class BlockExecutor {

    /**
     * A structure that maps command name to its instances
     */
    private Map<String, AbstractCommand> boundCommands;

    public BlockExecutor() {
        boundCommands = new HashMap<>();
        boundCommands.put(MainCommands.COMMAND_CAT, new CommandCat());
        boundCommands.put(MainCommands.COMMAND_ECHO, new CommandEcho());
        boundCommands.put(MainCommands.COMMAND_WC, new CommandWc());
        boundCommands.put(MainCommands.COMMAND_PWD, new CommandPwd());
        boundCommands.put(MainCommands.COMMAND_EXIT, new CommandExit());
    }

    /**
     * Receives the list of one-pipe command line tokens, handles first token and transfers control
     * to module corresponding to the entity of this token.
     *
     * The following situations are possible:
     * 1. First token refers to one of the main commands (e.g. cat, echo, wc, pwd, exit)
     * 2. First token declares a variable name and therefore the second token must be '='
     * 3. Otherwise, first token must refer to an external command
     * @param tokenList list of command line tokens (after substitutions)
     * @return code that interprets result of command execution built from tokens list {@see AbstractCommand}
     */
    public int execute(List<String> tokenList) throws UnknownExternalCommandException {

        List<String> trimmedTokenList = trimList(tokenList);

        if (trimmedTokenList.size() != 0) {
            if (boundCommands.containsKey(trimmedTokenList.get(0))) {
                AbstractCommand command = boundCommands.get(trimmedTokenList.get(0));
                if (trimmedTokenList.size() == 1) {
                    return command.execute();
                } else {
                    List<String> arguments = new ArrayList<>();
                    for (int i = 1; i < trimmedTokenList.size(); i++) {
                        if (!trimmedTokenList.get(i).equals(TokenizerHelper.whitespace)) {
                            arguments.add(trimmedTokenList.get(i));
                        }
                    }
                    return command.execute(arguments);
                }
            } else if (trimmedTokenList.size() > 1 && trimmedTokenList.get(1).equals(TokenizerHelper.assignmentOperator)) {
                if (isCorrectVariableName(trimmedTokenList.get(0)) && trimmedTokenList.size() > 2) {
                    BoundVariablesStorage.putVariable(trimmedTokenList.get(0), trimmedTokenList.get(2));
                }
            } else {
                StringJoiner joiner = new StringJoiner("");
                trimmedTokenList.forEach(joiner::add);
                String externalCommandInvocation = joiner.toString();
                ExternalCommand.execute(externalCommandInvocation);
            }
        }
        return 0;
    }

    /**
     * Checks if given variable name is correct.
     *
     * Variable name is considered correct if it starts with Latin symbol or underscore
     * and the rest consists of Latin symbols, numbers, and underscores.
     * @param variableName variable name
     * @return boolean value showing if given name satisfies the rule above.
     */
    public boolean isCorrectVariableName(String variableName) {
        return variableName.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    /**
     * Trim leading and trailing whitespace tokens.
     * @param tokenList list of command line tokens (after substitutions)
     * @return trimmed list
     */
    public List<String> trimList(List<String> tokenList) {
        List<String> trimmedTokenList = new ArrayList<>();
        int firstNonWhitespaceToken = 0;
        while (firstNonWhitespaceToken < tokenList.size() && tokenList.get(firstNonWhitespaceToken).equals(TokenizerHelper.whitespace)) {
            firstNonWhitespaceToken++;
        }
        int lastNonWhitespaceToken = tokenList.size() - 1;
        while (lastNonWhitespaceToken >= 0 && tokenList.get(lastNonWhitespaceToken).equals(TokenizerHelper.whitespace)) {
            lastNonWhitespaceToken--;
        }
        for (int i = firstNonWhitespaceToken; i <= lastNonWhitespaceToken; i++) {
            trimmedTokenList.add(tokenList.get(i));
        }
        return trimmedTokenList;
    }
}
