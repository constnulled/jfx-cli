package dev.joyel.cafe6.input;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract parent for file-based input plugins (JAR, directory, ...).
 *
 * @author Josef Cacek
 */
public abstract class AbstractFileJDInput implements JDInput {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileJDInput.class);

    protected final File file;
    private final String pattern;

    /**
     * Constructor based on an existing file path.
     *
     * @param filePath path to input file
     * @throws IllegalArgumentException path doesn't denote an existing file
     */
    public AbstractFileJDInput(String filePath) throws IllegalArgumentException {
        this(filePath, null);
    }

    public AbstractFileJDInput(String filePath, String pattern) throws IllegalArgumentException {
        LOGGER.trace("Creating JDInput instance for file {} and pattern {}", filePath, pattern);
        this.pattern = pattern == null ? pattern : (".*" + pattern + ".*");
        file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Path doesn't denote an existing file.");
        }
    }

    /**
     * Returns {@code true} when a pattern is configured and given path doesn't match it.
     */
    protected boolean skipThePath(String path) {
        boolean skip = pattern != null && path != null && !path.matches(pattern);
        if (skip) {
            LOGGER.debug("Skipping the path {} as it doesn't match the pattern {}", path, pattern);
        }
        return skip;
    }
}
