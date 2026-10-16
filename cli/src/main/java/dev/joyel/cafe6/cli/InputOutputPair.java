package dev.joyel.cafe6.cli;

import dev.joyel.cafe6.input.JDInput;
import dev.joyel.cafe6.output.JDOutput;

/**
 * Holds a pair of {@link JDInput} and {@link JDOutput} instances.
 */
public class InputOutputPair {

    private final JDInput jdInput;
    private final JDOutput jdOutput;

    public InputOutputPair(final JDInput jdIn, final JDOutput jdOut, final JDOutput jdOutFallback) {
        jdInput = jdIn;
        jdOutput = (jdOut != null ? jdOut : jdOutFallback);
    }

    /**
     * @return the jdInput
     */
    public JDInput getJdInput() {
        return jdInput;
    }

    /**
     * @return the jdOutput
     */
    public JDOutput getJdOutput() {
        return jdOutput;
    }

}
