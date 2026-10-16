package dev.joyel.cafe6.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.joyel.cafe6.IOUtils;
import dev.joyel.cafe6.JavaDecompiler;
import dev.joyel.cafe6.loader.FileLoader;
import dev.joyel.cafe6.output.JDOutput;

/**
 * Input plugin which takes a directory as an input.
 *
 * @author Josef Cacek
 */
public class DirInput extends AbstractFileJDInput {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirInput.class);
    private final FileLoader fileLoader;

    public DirInput(String path, String pattern) throws IllegalArgumentException {
        super(path, pattern);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Path doesn't denote a directory.");
        }
        this.fileLoader = new FileLoader(path);
    }

    public DirInput(String path) {
        this(path, null);
    }

    @Override
    public void decompile(JavaDecompiler javaDecompiler, JDOutput jdOutput) {
        if (javaDecompiler == null || jdOutput == null) {
            LOGGER.warn("Decompiler or JDOutput are null");
            return;
        }

        LOGGER.debug("Initializing decompilation of directory {}", file);
        jdOutput.init(javaDecompiler.getOptions(), file.getPath());
        for (File f : file.listFiles()) {
            processFile(javaDecompiler, jdOutput, "", f);
        }
        jdOutput.commit();
    }

    private void processFile(JavaDecompiler javaDecompiler, JDOutput jdOutput, String pathPrefix, File nextFile) {
        final String fileName = nextFile.getName();
        final String nameWithPath = pathPrefix + fileName;
        if (nextFile.isDirectory()) {
            LOGGER.trace("Processing directory {}", nextFile);
            for (File f : nextFile.listFiles()) {
                processFile(javaDecompiler, jdOutput, pathPrefix + fileName + "/", f);
            }
        } else {
            if (skipThePath(nameWithPath)) {
                return;
            }
            if (IOUtils.isClassFile(fileName)) {
                if (IOUtils.isInnerClass(fileName)) {
                    // don't handle inner classes
                    LOGGER.trace("Skipping inner class {}", nextFile);
                    return;
                }
                LOGGER.debug("Decompiling {}", nextFile);
                String internalName = IOUtils.cutClassSuffix(nameWithPath);
                jdOutput.processClass(internalName, javaDecompiler.decompileClass(fileLoader, internalName));
            } else if (!javaDecompiler.getOptions().isSkipResources()) {
                LOGGER.debug("Processing resource file {}", nextFile);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(nextFile);
                    jdOutput.processResource(nameWithPath, fis);
                } catch (IOException ioe) {
                    LOGGER.error("Resource processing failed for {}", nextFile, ioe);
                } finally {
                    IOUtils.closeQuietly(fis);
                }
            }

        }
    }
}
