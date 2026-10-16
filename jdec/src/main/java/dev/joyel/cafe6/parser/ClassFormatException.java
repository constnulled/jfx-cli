package dev.joyel.cafe6.parser;

/**
 * Exception class used to report problems during class file parsing.
 *
 * @author Josef Cacek
 */
public class ClassFormatException extends Exception {

    private static final long serialVersionUID = 1L;

    public ClassFormatException() {
    }

    public ClassFormatException(String message) {
        super(message);
    }

    public ClassFormatException(Throwable cause) {
        super(cause);
    }

    public ClassFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
