package dev.joyel.cafe6.output;

import static dev.joyel.cafe6.JavaDecompilerConstants.JAVA_SUFFIX;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.joyel.cafe6.IOUtils;
import dev.joyel.cafe6.options.DecompilerOptions;

/**
 * {@link JDOutput} implementation, which stores decompiled classes to a stream or file as a zip.
 *
 * @author Josef Cacek
 */
public class ZipOutput extends AbstractJDOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipOutput.class);

    private final OutputStream os;
    private final File file;

    private final AtomicLong countClasses = new AtomicLong();
    private final AtomicLong countResources = new AtomicLong();

    private volatile ZipOutputStream zos;

    /**
     * {@link OutputStream} based constructor.
     *
     * @param os OutputStream to which the zip content should be written (not- <code>null</code>)
     */
    public ZipOutput(final OutputStream os) {
        if (os == null) {
            throw new NullPointerException("OutputStream can't be null.");
        }
        this.os = os;
        this.file = null;
    }

    /**
     * {@link File} based constructor. It creates the file (and parent directories).
     *
     * @param file instance of {@link File} (not-<code>null</code>)
     */
    public ZipOutput(final File file) {
        if (file == null) {
            throw new NullPointerException("File can't be null.");
        }
        this.os = null;
        this.file = file;
    }

    @Override
    public void init(DecompilerOptions options, String basePath) {
        super.init(options, basePath);
        countClasses.set(0);
        countResources.set(0);
        zos = null;
        if (file == null) {
            LOGGER.info("ZIP output will be initialized for an OutputStream.");
            zos = new ZipOutputStream(os);
        } else {
            LOGGER.info("ZIP file output will be initialized - {}", file);
            try {
                final File parentDir = file.getAbsoluteFile().getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                if (!parentDir.isDirectory()) {
                    LOGGER.error("Parent directory can't be created: {}", parentDir);
                    return;
                }
                zos = new ZipOutputStream(new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                LOGGER.error("ZipOutput can't be initialized", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jd.core.output.JDOutput#processClass(java.lang.String, java.lang.String)
     */
    public void processClass(final String className, final String src) {
        if (className == null || src == null || zos == null) {
            return;
        }
        synchronized (zos) {
            try {
                zos.putNextEntry(new ZipEntry(className + JAVA_SUFFIX));
                zos.write(src.getBytes(UTF_8));
                zos.closeEntry();
                countClasses.addAndGet(1);
            } catch (IOException e) {
                LOGGER.error("Exception occured during writing decompiled class {} to a ZIP output.", className, e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jd.core.output.JDOutput#processResource(java.lang.String, java.io.InputStream)
     */
    public void processResource(final String fileName, final InputStream is) {
        if (options.isSkipResources() || fileName == null || is == null || zos == null) {
            return;
        }
        synchronized (zos) {
            try {
                zos.putNextEntry(new ZipEntry(fileName));
                IOUtils.copy(is, zos);
                zos.closeEntry();
                countResources.addAndGet(1);
            } catch (IOException e) {
                LOGGER.error("Exception occured during writing resource {} to a ZIP output.", fileName, e);
            }
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
        if (zos != null) {
            try {
                if (file != null) {
                    zos.close();
                } else {
                    zos.finish();
                }
            } catch (IOException e) {
                LOGGER.error("Exception occured during finishing ZIP output.", e);
            }
        }

        LOGGER.info("Finished with {} class file(s) and {} resource file(s) written.", countClasses, countResources);
        zos = null;
    }

}
