package dev.joyel.cafe6.output;

import static dev.joyel.cafe6.JavaDecompilerConstants.JAVA_SUFFIX;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.joyel.cafe6.IOUtils;
import dev.joyel.cafe6.options.DecompilerOptions;

/**
 * {@link JDOutput} implementation, which stores decompiled classes to a directory.
 *
 * @author Josef Cacek
 */
public class DirOutput extends AbstractJDOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirOutput.class);

    protected final File dir;

    private final AtomicLong countClasses = new AtomicLong();
    private final AtomicLong countResources = new AtomicLong();

    /**
     * Constructor which takes directory path as a parameter.
     *
     * @param outputDir directory path to save output not-<code>null</code>
     */
    public DirOutput(final File outputDir) {
        if (outputDir == null) {
            throw new NullPointerException("Null directory given");
        }
        dir = outputDir;
    }

    @Override
    public void init(DecompilerOptions options, String basePath) {
        super.init(options, basePath);
        countClasses.set(0);
        countResources.set(0);
        LOGGER.info("Directory output will be initialized for path {}", dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            LOGGER.error("Provided path is either not a directory or mkdirs() command failed: {}", dir);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jd.core.output.JDOutput#processClass(java.lang.String, java.lang.String)
     */
    public void processClass(String className, String src) {
        if (className == null || src == null) {
            LOGGER.warn("Class name or java source is null");
            return;
        }
        final File decompiledFile = new File(getTargetDir(), className + JAVA_SUFFIX);
        LOGGER.trace("Writing decompiled class to {}", decompiledFile);
        createDir(decompiledFile.getParentFile());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(decompiledFile);
            fos.write(src.getBytes(UTF_8));
            countClasses.addAndGet(1);
        } catch (IOException e) {
            LOGGER.error("Writing decompiled class to file {} failed.", decompiledFile, e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jd.core.output.JDOutput#processResource(java.lang.String, java.io.InputStream)
     */
    public void processResource(String fileName, InputStream is) {
        if (options.isSkipResources() || fileName == null || is == null) {
            LOGGER.trace("Skipping resource {}", fileName);
            return;
        }
        final File tmpFile = new File(getTargetDir(), fileName);
        LOGGER.trace("Storing resource {}", tmpFile);
        createDir(tmpFile.getParentFile());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tmpFile);
            IOUtils.copy(is, fos);
            countResources.addAndGet(1);
        } catch (IOException e) {
            LOGGER.error("Writing resource to file {} failed.", tmpFile, e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jd.core.output.AbstractJDOutput#commit()
     */
    @Override
    public void commit() {
        super.commit();
        LOGGER.info("Finished with {} class file(s) and {} resource file(s) written.", countClasses, countResources);
    }

    protected File getTargetDir() {
        return dir;
    }

    /**
     * Creates given directory if it doesn't exist.
     *
     * @param dir
     */
    private void createDir(final File dir) {
        if (!dir.exists()) {
            boolean dirCreated = dir.mkdirs();
            LOGGER.trace("Creating directory {} finished with result: {}", dir, dirCreated);
        }
    }
}
