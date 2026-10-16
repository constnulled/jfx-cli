package dev.joyel.cafe6.output;

import java.io.InputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link JDOutput} implementation which prints decompiled java source to a {@link PrintStream}. It skips resource processing.
 *
 * @author Josef Cacek
 */
public class PrintStreamOutput extends AbstractJDOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintStreamOutput.class);

    private final PrintStream ps;

    /**
     * Constructor.
     *
     * @param ps {@link PrintStream} to be used by this instance (not- <code>null</code>)
     */
    public PrintStreamOutput(final PrintStream ps) {
        if (ps == null) {
            throw new NullPointerException("PrintStream can't be null.");
        }
        this.ps = ps;
    }

    /**
     * Prints decompiled class to wrapped {@link PrintStream}.
     *
     * @see dev.joyel.cafe6.output.JDOutput#processClass(java.lang.String, java.lang.String)
     */
    public void processClass(final String className, final String src) {
        LOGGER.debug("Processing class {}", className);
        ps.println(src);
    }

    /**
     * Empty implementation.
     *
     * @see dev.joyel.cafe6.output.JDOutput#processResource(java.lang.String, java.io.InputStream)
     */
    public void processResource(String fileName, InputStream is) {
        LOGGER.debug("Skipping resource {}", fileName);
    }

}
