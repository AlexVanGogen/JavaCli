package ru.spbau.mit.softwaredesign.cli.errors;

/**
 * Auxiliary class for showing errors during the working with command line interface.
 */
public class ErrorMessage {

    public static final String COMMAND_NOT_FOUND = "command not found";
    public static final String FILE_NOT_FOUND = "file not found";
    public static final String SYNTAX_ERROR = "syntax error";
    public static final String PIPELINE_SYNTAX_ERROR = "syntax error near pipeline";
    public static final String IO_ERROR = "unexpected input/output error";
    public static final String PARSE_ERROR = "cannot parse input";

    /**
     * Prints error message to console.
     * @param from error deliver
     * @param what error info
     */
    public static void print(String from, String what) {
        System.out.println(what + ": error - " + from);
    }
}
